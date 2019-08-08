package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElecRoundService;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.service.ElecYjsService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.AllCourseVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "研究生选课", version = ""))
@RestSchema(schemaId = "ElecYjsController")
@RequestMapping("student")
public class ElecYjsController
{
    
    private RestTemplate restTemplate = RestTemplateBuilder.create();
    
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private ExemptionCourseService exemptionCourseServiceImpl;
    
    @Autowired
    private ElecRoundService electionRoundService;
    
    @Autowired
    private ElecYjsService yjsService;
    
    @ApiOperation(value = "获取研究生选课数据")
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
        if (!Constants.PROJ_UNGRADUATE.equals(session.getCurrentManageDptId()))
        {
            c = yjsService.setData(session.realUid(), c, roundId, null);
        }
        
        return RestResult.successData(c);
    }
    
    /**
     * 全部课程指：在本次选课学期，学生学籍所在校区对应的培养层次所有的排课信息
     */
    @ApiOperation(value = "查询全部课程")
    @PostMapping("/round/arrangementCourses")
    public RestResult<List<TeachingClassCache>> arrangementCourses(
        @RequestBody @Valid AllCourseVo allCourseVo)
    {
        logger.info("election getAllCourse start !!!");
        
        Session session = SessionUtils.getCurrentSession();
        String uid = "";
        if (session.getMock().booleanValue())
        {
            uid = session.getMockUid();
        }
        else
        {
            uid = session.getUid();
        }
        RestResult<Student> studentMessage =
            exemptionCourseServiceImpl.findStudentMessage(uid);
        Student student = studentMessage.getData();
        allCourseVo.setTrainingLevel(student.getTrainingLevel());
        allCourseVo.setCampu(student.getCampus());
        
        ElectionRoundsDto roundsDto =
            electionRoundService.get(allCourseVo.getRoundId());
        allCourseVo.setCalendarId(roundsDto.getCalendarId());
        
        List<TeachingClassCache> restResult =
            yjsService.arrangementCourses(allCourseVo);
        return RestResult.successData(restResult);
    }
    
    @ApiOperation(value = "获取个人培养计划完成情况")
    @PostMapping("/culturePlanData")
    public RestResult<?> getCulturePlanData()
    {
        logger.info("election getCulturePlanData start !!!");
        
        Session session = SessionUtils.getCurrentSession();
        String uid = "";
        if (session.getMock().booleanValue())
        {
            uid = session.getMockUid();
        }
        else
        {
            uid = session.getUid();
        }
        
        /**
         * 调用培养：个人培养计划完成情况接口
         * coursesLabelList (课程分类列表)
         * cultureCourseLabelRelationList(课程列表)
         */
        String path = ServicePathEnum.CULTURESERVICE.getPath(
            "/culturePlan/getCulturePlanByStudentIdForElection?id={id}&&isPass={isPass}");
        RestResult<Map<String, Object>> restResult1 =
            restTemplate.getForObject(path, RestResult.class, uid, 0);
        
        /** 调用培养：培养方案的课程分类学分 */
        String culturePath = ServicePathEnum.CULTURESERVICE
            .getPath("/studentCultureRel/getCultureCredit?studentId={id}");
        RestResult<Map<String, Object>> restResult2 =
            restTemplate.getForObject(culturePath, RestResult.class, uid);
        
        Map<String, Object> data1 = restResult1.getData();
        Map<String, Object> data2 = restResult2.getData();
        
        ArrayList<Map<String, Object>> resultList =
            new ArrayList<Map<String, Object>>(2);
        resultList.add(data1);
        resultList.add(data2);
        
        return RestResult.successData(resultList);
    }
    
    @ApiOperation(value = "获取研究生个人培养计划信息")
    @PostMapping("/culturePlanMsg")
    public RestResult<?> getCulturePlanMsg(
        @RequestParam("roundId") Long roundId)
    {
        Session session = SessionUtils.getCurrentSession();
        String uid = session.realUid();
        
        logger.info("culturePath select success");
        Map<String, Object> restResult3 =
            yjsService.getElectResultCount(uid, roundId);
        List<Map<String, Object>> resultList =
            new ArrayList<Map<String, Object>>(1);
        
        resultList.add(restResult3);
        return RestResult.successData(resultList);
    }
    
}