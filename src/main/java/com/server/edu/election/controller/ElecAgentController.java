package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.common.rest.codec.RestObjectMapperFactory;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.validate.AgentElcGroup;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "代理选课", version = ""))
@RestSchema(schemaId = "ElecAgentController")
@RequestMapping("agentElc")
public class ElecAgentController
{
    @Autowired
    private StudentElecService elecService;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode)
    {
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        Date date = new Date();
        for (ElectionRounds round : allRound)
        {
            if (StringUtils.equals(round.getProjectId(), projectId)
                && StringUtils.equals(electionObj, round.getElectionObj())
                && Objects.equals(mode, round.getMode())
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime()))
            {
                ElectionRoundsVo vo = new ElectionRoundsVo(round);
                List<ElectionRuleVo> rules =
                    dataProvider.getRules(round.getId());
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
    @PostMapping("/loading")
    public RestResult<ElecRespose> studentLoading(
        @RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        return elecService.loading(elecRequest.getRoundId(), studentId);
    }
    
    @ApiOperation(value = "获取学生选课数据")
    @PostMapping("/getData")
    public RestResult<ElecContext> getData(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = dataProvider.getRound(elecRequest.getRoundId());
        if (round == null)
        {
            return RestResult.error("elec.roundNotExistTip");
        }
        ElecContext c = new ElecContext(studentId, round.getCalendarId());
        
        return RestResult.successData(c);
    }
    
    @ApiOperation(value = "获取学生选课数据(测试不返回，只查询redis)")
    @PostMapping("/getData1")
    public RestResult<ElecContext> getData1(@RequestBody ElecRequest elecRequest) throws JsonProcessingException
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = dataProvider.getRound(elecRequest.getRoundId());
        if (round == null)
        {
            return RestResult.error("elec.roundNotExistTip");
        }
        ElecContext c = new ElecContext(studentId, round.getCalendarId());
        String str = RestObjectMapperFactory.getRestObjectMapper().writeValueAsString(c);
        System.out.println(str.substring(0, 20));
        return RestResult.success();
    }
    
    @ApiOperation(value = "获取学生选课数据2")
    @PostMapping("/getData2")
    public String getData2(@RequestBody ElecRequest elecRequest) throws JsonProcessingException
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = dataProvider.getRound(elecRequest.getRoundId());
        ElecContext c = new ElecContext(studentId, round.getCalendarId());
        
        return RestObjectMapperFactory.getRestObjectMapper().writeValueAsString(c);
    }
    
    @ApiOperation(value = "获取学生选课数据3")
    @PostMapping("/getData3")
    public String getData3(@RequestBody ElecRequest elecRequest) throws JsonProcessingException
    {
        return "{\"code\":200,\"msg\":\"\",\"data\":{\"studentInfo\":{\"studentId\":\"1659350\",\"studentName\":\"梅锐梓\",\"sex\":1,\"campus\":\"1\",\"faculty\":\"000192\",\"major\":\"01025\",\"grade\":2016,\"trainingLevel\":\"1\",\"englishLevel\":null,\"spcialPlan\":\"\",\"paid\":false,\"graduate\":false,\"repeater\":false,\"aboard\":false,\"sexI18n\":\"男\",\"campusI18n\":\"四平路校区\",\"facultyI18n\":\"经济与管理学院\",\"majorI18n\":\"工商管理(国际班)\",\"trainingLevelI18n\":\"本科\",\"spcialPlanI18n\":\"\"},\"completedCourses\":[],\"selectedCourses\":[{\"courseCode\":\"10001921094\",\"courseName\":\"质量管理\",\"credits\":2.0,\"nameEn\":\"Quality Management\",\"campus\":\"1\",\"publicElec\":false,\"calendarName\":null,\"electionApplyId\":null,\"apply\":null,\"teachClassId\":111111112458015,\"teachClassCode\":\"01037801\",\"teachClassType\":\"1\",\"maxNumber\":35,\"currentNumber\":1,\"times\":[{\"teachClassId\":111111112458015,\"arrangeTimeId\":2950648,\"timeStart\":5,\"timeEnd\":6,\"dayOfWeek\":1,\"weeks\":[1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17],\"value\":\"质量管理(10001921094) null\",\"teacherCode\":\"04066\"}],\"teacherCode\":null,\"teacherName\":null,\"chooseObj\":3,\"courseTakeType\":1,\"turn\":1,\"isApply\":null,\"practice\":false,\"retraining\":false}],\"applyForDropCourses\":[],\"courseGroups\":[],\"planCourses\":[{\"courseCode\":\"10001921094\",\"courseName\":\"质量管理\",\"credits\":2.0,\"nameEn\":\"Quality Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921531\",\"courseName\":\"中国市场研究\",\"credits\":2.0,\"nameEn\":\"Chinese Market Research\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921377\",\"courseName\":\"公司治理\",\"credits\":2.0,\"nameEn\":\"Corporate Governance\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921530\",\"courseName\":\"中国房地产开发\",\"credits\":2.0,\"nameEn\":\"Real Estate Development in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921376\",\"courseName\":\"货币金融学\",\"credits\":2.0,\"nameEn\":\"The Economics of Money, Banking and Financial Markets\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921533\",\"courseName\":\"中国城市与房地产开发\",\"credits\":2.0,\"nameEn\":\"Urban and Real Estate Development in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921532\",\"courseName\":\"国际商务及中国企业国际化\",\"credits\":2.0,\"nameEn\":\"International Business & Internationalization of Chinese Firms\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921516\",\"courseName\":\"市场营销学（英）\",\"credits\":2.0,\"nameEn\":\"Marketing（English）\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001920649\",\"courseName\":\"管理学概论\",\"credits\":2.0,\"nameEn\":\"Introduction to Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921513\",\"courseName\":\"管理信息系统A（英）\",\"credits\":2.0,\"nameEn\":\"Management Information System A(English)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001920666\",\"courseName\":\"电子商务\",\"credits\":2.0,\"nameEn\":\"Electronic Business\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921535\",\"courseName\":\"毕业论文（国际班）\",\"credits\":16.0,\"nameEn\":\"Graduation Thesis\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":25,\"weekType\":0,\"semester\":\"8\",\"subCourseCode\":null},{\"courseCode\":\"10001921512\",\"courseName\":\"商务谈判（英）\",\"credits\":2.0,\"nameEn\":\"Business Negotiation（English）\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921534\",\"courseName\":\"信息技术与管理\",\"credits\":2.0,\"nameEn\":\"Information Technology and Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921515\",\"courseName\":\"项目管理 （英）\",\"credits\":2.0,\"nameEn\":\"Project Management(English)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921537\",\"courseName\":\"毕业实习(国际班)\",\"credits\":6.0,\"nameEn\":\"Internship\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":25,\"weekType\":0,\"semester\":\"8\",\"subCourseCode\":null},{\"courseCode\":\"10001921514\",\"courseName\":\"人力资源管理（英）\",\"credits\":2.0,\"nameEn\":\"Human Resource Management（English）\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":191,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10000390105\",\"courseName\":\"基础中文\",\"credits\":6.0,\"nameEn\":\"Chinese Basic\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10000390106\",\"courseName\":\"初级中文\",\"credits\":6.0,\"nameEn\":\"Chinese Elementary\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10000390107\",\"courseName\":\"中级中文\",\"credits\":6.0,\"nameEn\":\"Chinese Intermediate\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921388\",\"courseName\":\"物流与供应链管理\",\"credits\":2.0,\"nameEn\":\"Logistics and Supply Chain Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921368\",\"courseName\":\"创业学\",\"credits\":2.0,\"nameEn\":\"Entrepreneurship\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921522\",\"courseName\":\"物流与供应链管理Ⅱ\",\"credits\":2.0,\"nameEn\":\"Logistics & Supply Chain Management in China (Ⅱ)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921521\",\"courseName\":\"学术讲座\",\"credits\":2.0,\"nameEn\":\"Academic Lectures and Seminars\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921045\",\"courseName\":\"运营管理\",\"credits\":2.0,\"nameEn\":\"Operation Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":203,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921143\",\"courseName\":\"公司金融\",\"credits\":2.0,\"nameEn\":\"Corporate Finance\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10000390096\",\"courseName\":\"中国概论（英）\",\"credits\":2.0,\"nameEn\":\"Chinese Affairs(English)\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":100,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921528\",\"courseName\":\"资本市场分析\",\"credits\":2.0,\"nameEn\":\"Capital Market Analysis\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921527\",\"courseName\":\"跨文化沟通\",\"credits\":2.0,\"nameEn\":\"Multicultural Communication\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921529\",\"courseName\":\"私募股权与风险资本\",\"credits\":2.0,\"nameEn\":\"Private Equity and Venture Capital in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921524\",\"courseName\":\"财务与管理会计\",\"credits\":2.0,\"nameEn\":\"Financial & Management Accounting\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第2学期\",\"electionApplyId\":null,\"apply\":null,\"label\":203,\"weekType\":0,\"semester\":\"6\",\"subCourseCode\":null},{\"courseCode\":\"10001921523\",\"courseName\":\"可持续发展经济学\",\"credits\":2.0,\"nameEn\":\"Economics of Sustainable Development in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001921526\",\"courseName\":\"中国金融市场\",\"credits\":2.0,\"nameEn\":\"Financial Market in China\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001921525\",\"courseName\":\"中国经济：改革与发展\",\"credits\":2.0,\"nameEn\":\"China Economy: Reform and Development\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null},{\"courseCode\":\"10001920216\",\"courseName\":\"投资学\",\"credits\":2.0,\"nameEn\":\"Investment\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2019-2020学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":101,\"weekType\":0,\"semester\":\"7\",\"subCourseCode\":null},{\"courseCode\":\"10001920816\",\"courseName\":\"战略管理\",\"credits\":2.0,\"nameEn\":\"Strategic Management\",\"campus\":null,\"publicElec\":false,\"calendarName\":\"2018-2019学年第1学期\",\"electionApplyId\":null,\"apply\":null,\"label\":203,\"weekType\":0,\"semester\":\"5\",\"subCourseCode\":null}],\"publicCourses\":[],\"failedCourse\":[],\"applyCourse\":[\"10001921531\"],\"elecApplyCourses\":[{\"id\":4,\"studentId\":\"1659350\",\"calendarId\":107,\"courseCode\":\"10001921531\",\"apply\":0,\"applyBy\":null,\"remark\":null,\"mode\":1,\"createdAt\":\"2019-06-13 16:47:49\"}],\"request\":null,\"respose\":{\"status\":null,\"successCourses\":[],\"failedReasons\":{},\"data\":null}}}";
    }
    
    /**
     * 选课请求,选课时发送一次，此时应该返回ElecRespose.status=processing
     */
    @ApiOperation(value = "学生选课")
    @PostMapping("/elect")
    public RestResult<ElecRespose> elect(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        if (elecRequest.getChooseObj() == null)
        {
            throw new ParameterValidateException("chooseObj not be null");
        }
        Session session = SessionUtils.getCurrentSession();
        elecRequest.setCreateBy(session.getUid());
        elecRequest.setRequestIp(session.getIp());
        return elecService.elect(elecRequest);
    }
    
    /**
     * 获取选课结果的请求 未完成时status为processing， 前端会定时执行请求直到status变为ready，此时应返回所有选课结果
     */
    @ApiOperation(value = "查询选课结果")
    @PostMapping("/electRes")
    public RestResult<ElecRespose> getElect(
        @RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        ElecRespose response =
            elecService.getElectResult(elecRequest.getRoundId(), studentId);
        return RestResult.successData(response);
    }
    
    /**
    *@Description: 通过学号和轮次获取学生信息
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/5 14:13
    */
    @ApiOperation(value = "查询轮次学生信息")
    @PostMapping("/findStuRound")
    public RestResult<Student> findStuRound(
        @RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        Student stu = elecService.findStuRound(elecRequest.getRoundId(),
            elecRequest.getStudentId());
        return RestResult.successData(stu);
    }
    
    @ApiOperation(value = "清除学生选课缓存数据")
    @PostMapping("/clearCache")
    public RestResult<?> clearCache(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        ElecContextUtil.setElecStatus(elecRequest.getRoundId(),
            elecRequest.getStudentId(),
            ElecStatus.Init);
        
        return RestResult.success();
    }
    
}
