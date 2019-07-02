package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElecRoundService;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElcCourseResult;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.AllCourseVo;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "学生选课", version = ""))
@RestSchema(schemaId = "ElecController")
@RequestMapping("student")
public class ElecController
{
    @Autowired
    private StudentElecService elecService;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private ExemptionCourseService exemptionCourseServiceImpl;
    
    @Autowired
    private ElecRoundService electionRoundService;
    
    @ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("projectId") @NotBlank String projectId)
    {
        Session session = SessionUtils.getCurrentSession();
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        Date date = new Date();
        String studentId = session.realUid();
        for (ElectionRounds round : allRound)
        {
            Long roundId = round.getId();
            if (StringUtils.equals(round.getProjectId(), projectId)
                && StringUtils.equals(Constants.STU, round.getElectionObj())
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime())
                && dataProvider.containsStu(roundId, studentId)
                && dataProvider.containsStuCondition(roundId, studentId))
            {
                ElectionRoundsVo vo = new ElectionRoundsVo(round);
                List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
                vo.setRuleVos(rules);
                data.add(vo);
            }
        }
        
        return RestResult.successData(data);
    }
    
    /**
     * 进入选课,此时要加载数据
     * 登录选课界面时预加载数据的请求，只需要包括studentId status，前端会定时执行请求直到status变为ready 即加载完成
     */
    @ApiOperation(value = "数据加载")
    @PostMapping("/{roundId}/loading")
    public RestResult<ElecRespose> studentLoading(
        @PathVariable("roundId") @NotNull Long roundId)
    {
        Session session = SessionUtils.getCurrentSession();
        
        if (session.realType() != UserTypeEnum.STUDENT.getValue())
        {
            return RestResult.fail("elec.mustBeStu");
        }
        return elecService.loading(roundId, session.realUid());
    }
    
    @ApiOperation(value = "获取学生选课数据")
    @PostMapping("/{roundId}/getData")
    public RestResult<ElecContext> getData(
        @PathVariable("roundId") @NotNull Long roundId)
    {
        Session session = SessionUtils.getCurrentSession();
        
        if (session.realType() != UserTypeEnum.STUDENT.getValue())
        {
            return RestResult.fail("elec.mustBeStu");
        }
        ElectionRounds round = dataProvider.getRound(roundId);
        if (round == null)
        {
            return RestResult.error("elec.roundNotExistTip");
        }
        ElecContext c =
            new ElecContext(session.realUid(), round.getCalendarId());
        
        return RestResult.successData(c);
    }
    
    @ApiOperation(value = "获取可选课程列表")
    @PostMapping("/getOptionalCourses")
    public RestResult<List<ElcCourseResult>> getOptionalCourses(
        @RequestParam("roundId") @NotBlank String roundId)
    {
        Session session = SessionUtils.getCurrentSession();
        String studentId = session.realUid();
        List<ElcCourseResult> data = elecService.getOptionalCourses(Long.parseLong(roundId),studentId);
        return RestResult.successData(data);
    }
    
    @ApiOperation(value = "获取课程对应的教学班数据")
    @PostMapping("/getTeachClass")
    public RestResult<List<TeachingClassCache>> getTeachClass(
        @RequestParam("roundId") @NotNull Long roundId,
        @RequestParam("courseCode") @NotBlank String courseCode)
    {
        List<TeachingClassCache> teachClasss =
            dataProvider.getTeachClasss(roundId, courseCode);
        if (CollectionUtil.isNotEmpty(teachClasss))
        {
            for (TeachingClassCache teachClass : teachClasss)
            {
                Long teachClassId = teachClass.getTeachClassId();
                Integer elecNumber = dataProvider.getElecNumber(teachClassId);
                teachClass.setCurrentNumber(elecNumber);
            }
        }
        
        return RestResult.successData(teachClasss);
    }
    
    /**
     * 选课请求,选课时发送一次，此时应该返回ElecRespose.status=processing
     */
    @ApiOperation(value = "学生选课")
    @PostMapping("/elect")
    public RestResult<ElecRespose> elect(
        @RequestBody @Valid ElecRequest elecRequest)
    {
        Session session = SessionUtils.getCurrentSession();
        if (session.realType() != UserTypeEnum.STUDENT.getValue())
        {
            return RestResult.fail("elec.mustBeStu");
        }
        elecRequest.setChooseObj(ChooseObj.STU.type());
        elecRequest.setStudentId(session.realUid());
        elecRequest.setCreateBy(session.getUid());
        elecRequest.setRequestIp(session.getIp());
        
        return elecService.elect(elecRequest);
    }
    
    /**
     * 获取选课结果的请求 未完成时status为processing， 前端会定时执行请求直到status变为ready，此时应返回所有选课结果
     */
    @ApiOperation(value = "查询选课结果")
    @PostMapping("/{roundId}/electRes")
    public RestResult<ElecRespose> getElect(
        @PathVariable("roundId") @NotNull Long roundId)
    {
        Session session = SessionUtils.getCurrentSession();
        if (session.realType() != UserTypeEnum.STUDENT.getValue())
        {
            return RestResult.fail("elec.mustBeStu");
        }
        ElecRespose response =
            elecService.getElectResult(roundId, session.realUid());
        return RestResult.successData(response);
    }
    
    /**
     * 全部课程指：在本次选课学期，学生学籍所在校区对应的培养层次所有的排课信息
     */
    @ApiOperation(value = "查询全部课程")
    @PostMapping("/{roundId}/allCourse")
    public RestResult<Map<String,List<ElcCourseResult>>> getAllCourse(
    		@RequestBody @Valid AllCourseVo allCourseVo){
    	Session session = SessionUtils.getCurrentSession();
    	String uid = session.getUid();
    	RestResult<Student> studentMessage = exemptionCourseServiceImpl.findStudentMessage(uid);
    	Student student = studentMessage.getData();
    	allCourseVo.setTrainingLevel(student.getTrainingLevel());
    	allCourseVo.setCampu(student.getCampus());
    	
    	ElectionRoundsDto roundsDto = electionRoundService.get(allCourseVo.getRoundId());
    	allCourseVo.setCalendarId(roundsDto.getCalendarId());
    	
    	Map<String,List<ElcCourseResult>> map = elecService.getAllCourse(allCourseVo);
    	return RestResult.successData(map);
    }
}
