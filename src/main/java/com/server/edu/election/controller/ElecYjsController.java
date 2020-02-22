package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.util.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElecRoundService;
import com.server.edu.election.service.ExemptionApplyConditionService;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.service.ElecYjsService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.AllCourseVo;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.election.vo.RedisVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

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
    private ExemptionApplyConditionService exemptionApplyConditionSerice;
    
    @Autowired
    private ElecYjsService yjsService;
    
    private static final String LIST_RULE = "yjsMustInElectableListRule";
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    @ApiOperation(value = "研究生选课获取生效的轮次")
    @PostMapping("/getGraduateRounds")
    public RestResult<List<ElectionRoundsVo>> getGraduateRounds(
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
                && dataProvider.containsStuCondition(roundId, studentId, projectId))
            {
            	List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
            	boolean flag = true;
            	if (CollectionUtil.isNotEmpty(rules)) {
            		List<ElectionRuleVo> nameList = rules.stream().filter(vo->StringUtils.equals(vo.getServiceName(), LIST_RULE)).collect(Collectors.toList());
            		if (CollectionUtil.isNotEmpty(nameList)) {
            			if (!dataProvider.containsStu(roundId, studentId) ) {
            				flag = false;
    					}
					}
				}
            	if (flag) {
            		ElectionRoundsVo vo = new ElectionRoundsVo(round);
                    vo.setRuleVos(rules);
                    data.add(vo);
				}
            }
        }
        return RestResult.successData(data);
    }
    
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
    public RestResult<PageResult<TeachingClassCache>> arrangementCourses(
        @RequestBody @Valid PageCondition<AllCourseVo> allCourseVo)
    {
        logger.info("election getAllCourse start !!!");
        Session session = SessionUtils.getCurrentSession();
        allCourseVo.getCondition().setProjectId(session.getCurrentManageDptId());

        /** 学生 */
        boolean isStudent = session.isStudent();
        /** 管理员 */
        boolean isAdmin = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
        						&& session.isAdmin();
        /** 教务员  */
        boolean isDepartAdmin = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
				                && !session.isAdmin() && session.isAcdemicDean();

        logger.info("isStudent: {};isAdmin: {}; isDepartAdmin {}",isStudent, isAdmin, isDepartAdmin);
        // 如果前端传值campu和trainingLevel,则不需要访问后端接口
//        if (CommonConstant.isEmptyStr(allCourseVo.getCondition().getCampu()) && CommonConstant.isEmptyStr(allCourseVo.getCondition().getTrainingLevel())) {
//            RestResult<Student> studentMessage = new RestResult<>();
//            logger.info("arrangementCourses enter findStudentMessage");
//            if (isStudent) {
//                studentMessage = exemptionCourseServiceImpl.findStudentMessage(session.realUid());
//            }else if (isAdmin || isDepartAdmin) {
//                studentMessage = exemptionCourseServiceImpl.findStudentMessage(allCourseVo.getCondition().getStudentCode());
//            }
//            Student student = studentMessage.getData();
//            allCourseVo.getCondition().setTrainingLevel(student.getTrainingLevel());
//            allCourseVo.getCondition().setCampu(student.getCampus());
//        }

        // 管理员不受校区，培养层次限制
        if(!isAdmin) {
            if (CommonConstant.isEmptyStr(allCourseVo.getCondition().getCampu()) && CommonConstant.isEmptyStr(allCourseVo.getCondition().getTrainingLevel())) {
                RestResult<Student> studentMessage = new RestResult<>();
                logger.info("arrangementCourses enter findStudentMessage");
                if (isStudent) {
                    studentMessage = exemptionCourseServiceImpl.findStudentMessage(session.realUid());
                }else if (isAdmin || isDepartAdmin) {
                    studentMessage = exemptionCourseServiceImpl.findStudentMessage(allCourseVo.getCondition().getStudentCode());
                }
                Student student = studentMessage.getData();
                allCourseVo.getCondition().setTrainingLevel(student.getTrainingLevel());
                allCourseVo.getCondition().setCampu(student.getCampus());
            }
        } else {
            allCourseVo.getCondition().setTrainingLevel("");
            allCourseVo.getCondition().setCampu("");
        }

        if (isStudent || isDepartAdmin) {
            logger.info("arrangementCourses enter getCalendarIdByRoundId");
        	ElectionRoundsDto roundsDto =
        			electionRoundService.getCalendarIdByRoundId(allCourseVo.getCondition().getRoundId());
        	allCourseVo.getCondition().setCalendarId(roundsDto.getCalendarId());
		}

        logger.info("arrangementCourses enter arrangementCourses.service");
        PageResult<TeachingClassCache> restResult =
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
    		@RequestParam("courseCode") String courseCode,
    		@RequestParam("studentId") String studentId)
    {
    	List<ExemptionApplyGraduteCondition> list = exemptionApplyConditionSerice.queryApplyConditionByCourseCodeAndStudentId(courseCode,studentId);
    	return RestResult.successData(list);
    }  
    
    @ApiOperation(value = "教学班信息缓存")
    @PostMapping("/teachingClassLoad")
    public RestResult<?> teachingClassLoad()
    {
        logger.info("初始化缓存 !!!");
        dataProvider.load();
    	return RestResult.success();
    } 
    
    @ApiOperation(value = "手动操作redis中的数据")
    @PostMapping("/operatorRedisKeys")
    public RestResult<?> operatorRedisKeys(@RequestBody RedisVo redisVo)
    {
    	String key = redisVo.getKey();
    	String pattern = redisVo.getPattern();
    	List<String> list = redisVo.getList();
    	String method = redisVo.getMethod();
    	
    	RestResult<?> result = null;
    	
    	Session session = SessionUtils.getCurrentSession();
    	Boolean isAdmin = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && session.isAdmin(); 
    	if (!isAdmin) {
			return new RestResult().error();
		}
    	
    	if (StringUtils.equalsIgnoreCase(method, Constants.QUERY)) { // 查询
    		/** 支持精确查询和模糊匹配查询
    		 *  elec-stdstatus-109_1931454
    		 *  elec-stdstatus-109_*
    		 */
    		if (StringUtils.isNotBlank(key)) {
    			Set<String> keys = strTemplate.keys(key);
    			return RestResult.successData(keys);
			}else {
				return RestResult.error("key不能为空！");
			}
		}else if (StringUtils.equalsIgnoreCase(method, Constants.DELETE)) { // 删除
			/**
			 * key值不为空则直接使用key删除(可精确删除也可模糊删除)
			 *    elec-stdstatus-109_1931454
			 *    elec-stdstatus-109_*
			 */
			if (StringUtils.isNotBlank(key)) {
				Set<String> keys = strTemplate.keys(key);
				strTemplate.delete(keys);
				return RestResult.successData(keys);
			}
			
			/** key为空则直接使用pattern和list组合删除 */
			if (StringUtils.isNotBlank(pattern)) {
				if (CollectionUtil.isNotEmpty(list)) {
					Set<String> patternSet = new HashSet<String>();
					for (String studnetId : redisVo.getList()) {
						String keyString = redisVo.getPattern() + studnetId;
						patternSet.add(keyString);
					}
					strTemplate.delete(patternSet);
					return RestResult.successData(patternSet);
				}else {
					result = RestResult.error("list不能为空！");
				}
			}else {
				result = RestResult.error("pattern不能为空！");
			}
		}
    	
    	return result;
    }
    
    @ApiOperation(value = "学生培养计划发生修改时删除redis中的选课状态")
    @PostMapping("/deleteRedisSelectedStatus")
    public RestResult<?> deleteRedisSelectedStatus(@RequestBody String studentId)
    {
        System.out.println(studentId);
    	String pattern = "elec-stdstatus-*_"+studentId;
    	Set<String> keys = strTemplate.keys(pattern);
        System.out.println(pattern);
        System.out.println(keys.toString());
    	if (CollectionUtil.isNotEmpty(keys)) {
    	    strTemplate.delete(keys);
		}
    	return RestResult.success();
    }
}
