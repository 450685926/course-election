package com.server.edu.election.controller;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.GradeStuPlanCourseDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.GradePlanCourse;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.PublicCourseQuery;
import com.server.edu.election.query.PublicCourseTag;
import com.server.edu.election.service.ElecRoundService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.election.vo.PublicCourseVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@SwaggerDefinition(info = @Info(title = "学生选课", version = ""))
@RestSchema(schemaId = "ElecController")
@RequestMapping("student")
public class ElecController
{
    
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StudentElecService elecService;

    @Autowired
    private ElecRoundService roundService;

    @Autowired
    private RoundDataProvider dataProvider;

    @ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("projectId") @NotBlank String projectId)
    {
        Session session = SessionUtils.getCurrentSession();
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        Date date = new Date();
        if(CollectionUtil.isEmpty(allRound)) {
            ElectionRounds rounds = new ElectionRounds();
            rounds.setElectionObj(Constants.STU);
            rounds.setBeginTime(date);
            rounds.setProjectId(projectId);
            allRound = roundService.getAllList(rounds);
        	for(ElectionRounds electionRounds :allRound) {
        		dataProvider.updateRoundCache(electionRounds.getId());
        	}
        }
        String studentId = session.realUid();
        //判断学生类型 （在校 ，结业 ，留学结业，按照模式显示轮次信息）
        List<String> studentRoundType = roundService.findStudentRoundType(studentId);
        for (ElectionRounds round : allRound)
        {
            Long roundId = round.getId();
            if (StringUtils.equals(round.getProjectId(), projectId)
                && StringUtils.equals(Constants.STU, round.getElectionObj())
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime())
//                && dataProvider.containsStu(roundId, studentId)
                && dataProvider
                    .containsStuCondition(roundId, studentId, projectId)
                    && studentRoundType.contains(String.valueOf(round.getMode())))
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
        ElecRequest elecRequest = new ElecRequest();
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(session.realUid());
        elecRequest.setProjectId(session.getCurrentManageDptId());
        return elecService.loading(elecRequest);
    }
    
    @ApiOperation(value = "获取本科生选课数据")
    @PostMapping("/{roundId}/getDataBk")
    public RestResult<ElecContextBk> getDataBk(
        @PathVariable("roundId") @NotNull Long roundId)
    {
        Session session = SessionUtils.getCurrentSession();

        if (session.realType() != UserTypeEnum.STUDENT.getValue())
        {
            return RestResult.fail("elec.mustBeStu");
        }
        ElectionRounds round = dataProvider.getRound(roundId);
        Assert.notNull(round, "elec.roundNotExistTip");
        ElecContextBk c =
            new ElecContextBk(session.realUid(), round.getCalendarId());
        elecService.getDataBk(c,roundId);
        return RestResult.successData(c);
    }
    
    @ApiOperation(value = "获取课程对应的教学班数据")
    @PostMapping("/getTeachClass")
    @Cacheable(value = "teachingClassCache", key ="#roundId+'-'+#courseCode" )
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

    @ApiOperation(value = "获取课程对应的教学班数据(同时排除掉不满足选课限制的班级)")
    @PostMapping("/getTeachClass4Limit")
    @Cacheable(value = "teachingClassCacheVo", key ="#roundId+'-'+#courseCode+'-'+#studentId" )
    public RestResult<List<TeachingClassCache>> getTeachClass4Limit(
            @RequestParam("roundId") @NotNull Long roundId,
            @RequestParam("studentId") @NotNull Long studentId,
            @RequestParam("courseCode") @NotBlank String courseCode)
    {
        List<TeachingClassCache> teachClasss =
                dataProvider.getTeachClasss(roundId, courseCode);
        List<TeachingClassCache> teachClasss4Limit = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teachClasss)){
            teachClasss4Limit = elecService.getTeachClass4Limit(teachClasss,studentId);
            if (CollectionUtil.isNotEmpty(teachClasss4Limit)){
                for (TeachingClassCache teachClass : teachClasss4Limit)
                {
                    Long teachClassId = teachClass.getTeachClassId();
                    Integer elecNumber = dataProvider.getElecNumber(teachClassId);
                    teachClass.setCurrentNumber(elecNumber);
                }
            }
        }
        return RestResult.successData(teachClasss4Limit);
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
        elecRequest.setRequestIp(SessionUtils.getRequestIp());
        elecRequest.setProjectId(session.getCurrentManageDptId());
        return elecService.elect(elecRequest);
    }
    
    /**
     * 登陆规则校验
     */
    @ApiOperation(value = "登陆规则校验")
    @PostMapping("/loginCheck")
    public RestResult<ElecRespose> loginCheck(
        @RequestBody  ElecRequest elecRequest)
    {
        return elecService.loginCheck(elecRequest);
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
        ElecRequest elecRequest = new ElecRequest();
        elecRequest.setRoundId(roundId);
        elecRequest.setStudentId(session.realUid());
        ElecRespose response = elecService.getElectResult(elecRequest);
        return RestResult.successData(response);
    }

    /**
     * 查询课程冲突
     */
    @ApiOperation(value = "查询课程冲突")
    @GetMapping("/getConflict")
    public RestResult getConflict(@RequestParam("roundId") @NotNull Long roundId,
                                  @RequestParam("studentId") @NotBlank String studentId,
                                  @RequestParam("teachClassId") @NotNull Long teachClassId)
    {
        RestResult conflict = elecService.getConflict(roundId, studentId, teachClassId);
        return conflict;
    }

    /**
     * 查询课程冲突
     */
    @ApiOperation(value = "查询通识选修课")
    @PostMapping("/getPublicCourse")
    public RestResult<List<PublicCourseTag>> getPublicCourse(@RequestBody PublicCourseQuery query)
    {
        List<PublicCourseTag> publicCourse = elecService.getPublicCourse(query);
        return RestResult.successData(publicCourse);
    }

    @ApiOperation(value = "重修缴费邮件发送")
    @PostMapping("/sendEmail")
    public RestResult<?> sendEmail()
    {
        logger.info("send Email !!!");
        elecService.sendEmail();
        return RestResult.success();
    }
    
	 /**
     * 获取结业生培养计划课程接口
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "特殊学生名单列表")
    @PostMapping("/getGradeStuPlanCourse")
    public RestResult<List<GradePlanCourse>> getGradeStuPlanCourse(
        @RequestBody GradeStuPlanCourseDto dto)
        throws Exception
    {
    	List<GradePlanCourse> list =elecService.getGradeStuPlanCourse(dto);
        return RestResult.successData(list);
    }
}
