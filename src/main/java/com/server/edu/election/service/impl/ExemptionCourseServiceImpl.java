package com.server.edu.election.service.impl;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.common.entity.StudentCultureRel;
import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.dictionary.DictCache;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ExemptionApplyConditionDao;
import com.server.edu.election.dao.ExemptionApplyDao;
import com.server.edu.election.dao.ExemptionAuditSwitchDao;
import com.server.edu.election.dao.ExemptionCourseDao;
import com.server.edu.election.dao.ExemptionCourseMaterialDao;
import com.server.edu.election.dao.ExemptionCourseRuleDao;
import com.server.edu.election.dao.ExemptionCourseScoreDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.ElcResultDto;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ExemptionApplyAuditSwitch;
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.entity.ExemptionCourseMaterial;
import com.server.edu.election.entity.ExemptionCourseScore;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ExemptionQuery;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.util.ExcelStoreConfig;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElecFirstLanguageContrastVo;
import com.server.edu.election.vo.ExemptionApplyManageVo;
import com.server.edu.election.vo.ExemptionCourseMaterialVo;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import com.server.edu.election.vo.ExemptionCourseVo;
import com.server.edu.election.vo.ExemptionStudentCountVo;
import com.server.edu.election.vo.ExemptionStudentCourseVo;
import com.server.edu.election.vo.StudentAndCourseVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.welcomeservice.util.ExcelEntityExport;

import tk.mybatis.mapper.entity.Example;

/**
 * @description: 免修免考课程实现类
 * @author: bear
 * @create: 2019-01-31 09:56
 */

@Service
@Primary
public class ExemptionCourseServiceImpl implements ExemptionCourseService{

	Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ExemptionCourseDao exemptionCourseDao;
    
    @Autowired
    private CourseDao courseDao;

    @Autowired
    private ExemptionCourseScoreDao scoreDao;

    @Autowired
    private ExemptionCourseRuleDao ruleDao;

    @Autowired
    private ExemptionCourseMaterialDao materialDao;

    @Autowired
    private ExemptionApplyDao applyDao;

    @Autowired
    private ElcCourseTakeDao elcCourseTakeDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private DictionaryService dictionaryService;  

    @Autowired
    private ExemptionAuditSwitchDao exemptionAuditSwitchDao;
    
    @Autowired
    private ExemptionApplyConditionDao exemptionApplyGraduateConditionDao;
    
    @Autowired
    private ExcelStoreConfig excelStoreConfig;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private RedisTemplate<String, List<DictCache>> redisTemplate;
    
    @Value("${cache.directory}")
    private String cacheDirectory;


    private static final Integer SUCCESS_STATUS=1;//免修申请审批状态通过
    private static final Integer STATUS=0;//免修申请审批状态初始化

    /**
    *@Description: 分页查询免修免考课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 10:05
    */
    @Override
    public PageResult<ExemptionCourseVo> findExemptionCourse(PageCondition<ExemptionCourseVo> condition) {
        Session currentSession = SessionUtils.getCurrentSession();
        String dptId = currentSession.getCurrentManageDptId();
        condition.getCondition().setManagerDeptId(dptId);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ExemptionCourseVo> exemptionCourse = exemptionCourseDao.findExemptionCourse(condition.getCondition());
        if(exemptionCourse!=null){
            List<ExemptionCourseVo> result = exemptionCourse.getResult();
            SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
                for (ExemptionCourseVo exemptionCourseVo : result) {
                    exemptionCourseVo.setCalendarName(schoolCalendar.getFullName());
                }
        }
        return new PageResult<>(exemptionCourse);
    }


    /**
    *@Description: 删除免修免考课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 10:20
    */

    @Override
    @Transactional
    public String deleteExemptionCourse(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
             return "common.parameterError";
        }
        exemptionCourseDao.deleteExemptionCourseByIds(ids);
        return  "common.deleteSuccess";

    }

    /**
    *@Description: 新增免修免考课程
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/1/31 12:51
    */
    @Override
    @Transactional
    public String addExemptionCourse(ExemptionCourse exemptionCourse) {
        ExemptionCourseVo ex=new ExemptionCourseVo();
        ex.setCalendarId(exemptionCourse.getCalendarId());
        ex.setCourseCode(exemptionCourse.getCourseCode());
        Page<ExemptionCourseVo> exCourse = exemptionCourseDao.findExemptionCourse(ex);
        if(exCourse!=null && exCourse.getResult().size()>0){
            return "common.exist";
        }
        exemptionCourse.setCreatedAt(new Date());
        exemptionCourseDao.insertSelective(exemptionCourse);
        return "common.addsuccess";
    }

    /**
    *@Description: 修改课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 16:17
    */
    @Override
    @Transactional
    public String updateExemptionCourse(ExemptionCourse exemptionCourse) {
        ExemptionCourseVo ex=new ExemptionCourseVo();
        Page<ExemptionCourseVo> exCourse = exemptionCourseDao.findExemptionCourse(ex);
        if(exCourse!=null&&exCourse.getResult().size()>0){
            List<ExemptionCourseVo> result = exCourse.getResult();
            List<ExemptionCourseVo> collect = result.stream().filter((ExemptionCourseVo vo) -> vo.getId().longValue() != exemptionCourse.getId().longValue()).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(collect)){
                for (ExemptionCourseVo exemptionCourseVo : collect) {
                    if(exemptionCourseVo.getCourseCode().equals(exemptionCourse.getCourseCode())
                            &&exemptionCourseVo.getCalendarId().longValue()==exemptionCourse.getCalendarId().longValue()){
                        return "common.exist";
                    }
                }
            }
        }


        exemptionCourse.setCreatedAt(new Date());
        exemptionCourseDao.updateByPrimaryKeySelective(exemptionCourse);
        return "common.editSuccess";
    }

    /**
    *@Description: 查询免修免考入学成绩
    *@Param: 
    *@return:
    *@Author: bear
    *@date: 2019/1/31 18:45
    */
    @Override
    public PageResult<ExemptionCourseScoreVo> findExemptionScore(PageCondition<ExemptionCourseScoreDto>  courseScoreDto) {
        PageHelper.startPage(courseScoreDto.getPageNum_(), courseScoreDto.getPageSize_());
        Page<ExemptionCourseScoreVo> exemptionScore = scoreDao.findExemptionScore(courseScoreDto.getCondition());
        return new PageResult<>(exemptionScore);
    }


    /**
    *@Description: 查询免修免考规则
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/1 16:22
    */
    @Override
    public PageResult<ExemptionCourseRuleVo> findExemptionRule(PageCondition<ExemptionCourseRuleVo> rulePageCondition) {
        PageHelper.startPage(rulePageCondition.getPageNum_(), rulePageCondition.getPageSize_());
        Page<ExemptionCourseRuleVo> exemptionCourseRule = ruleDao.findExemptionCourseRule(rulePageCondition.getCondition());
        if(exemptionCourseRule!=null){
            SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(rulePageCondition.getCondition().getCalendarId());
            List<ExemptionCourseRuleVo> result = exemptionCourseRule.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                for (ExemptionCourseRuleVo exemptionCourseRuleVo : result) {
                    List<String> codes = Arrays.asList(exemptionCourseRuleVo.getCourseCode().split(","));
                    if(CollectionUtil.isNotEmpty(codes)){
                      List<Course> courseNames= ruleDao.findCourseName(codes);
                      List<String> names=new ArrayList<>();
                        Map<String, List<Course>> map = courseNames.stream().collect(Collectors.groupingBy(Course::getCode));
                        for (String code : codes) {
                            names.add(map.get(code).get(0).getName());
                        }
                        exemptionCourseRuleVo.setCourseName(StringUtils.join(names,","));
                    }
                    exemptionCourseRuleVo.setCalendarName(schoolCalendar.getFullName());
                }
            }
        }
        return new PageResult<>(exemptionCourseRule);
    }

    /**
    *@Description: 删除申请规则
    *@Param: id  applyType申请类型
    *@return:
    *@Author: bear
    *@date: 2019/2/1 17:47
    */
    @Override
    public String deleteExemptionCourseRule(List<Long> ids, Integer applyType) {
        if(CollectionUtil.isEmpty(ids) || applyType==null){
            return "common.parameterError";
        }
        ruleDao.deleteExemptionCourseRule(ids);
        if(applyType==1){//材料申请
            materialDao.deleteExemptionCourseMaterial(ids);
        }
        return "common.deleteSuccess";
    }


    /**
    *@Description: 新增免修免考申请规则
    *@Param: courseCode 以逗号隔开
    *@return:
    *@Author: bear
    *@date: 2019/2/2 9:28
    */
    @Override
    public String addExemptionCourseRule(ExemptionCourseRuleVo courseRuleVo) {
        String courseCode = courseRuleVo.getCourseCode();
        Long calendarId = courseRuleVo.getCalendarId();
        Integer applyType = courseRuleVo.getApplyType();
        if("".equals(courseCode) || calendarId==null){
            return "common.parameterError";
        }
        if(applyType==0){//入学成绩规则（）
            String[] strings = courseCode.split(",");//拿到所有课程代码//根据学期（年级，培养类别），代码获取人数，分数线
            List<String> stringList = Arrays.asList(strings);

            List<ExemptionCourseScore> courseScore = scoreDao.findCourseScore(courseRuleVo, stringList);
            if(CollectionUtil.isEmpty(courseScore)){
                return "学期对应课程成绩没有导入，无法生成对应规则";
            }
            int number=courseScore.size();
            double score=0;
            if(courseRuleVo.getPencent()!=null &&courseRuleVo.getPencent()!=0){//按规则计算
                int percent=courseRuleVo.getPencent();
                int round = (int)Math.round(number * percent * 0.1 / 10);
                double totalScore=0;
                for (int i = 0; i < round; i++) {
                    if(courseScore.get(i).getScore()!=null){
                        totalScore+=courseScore.get(i).getScore();
                    }
                }
                score=(double)(Math.round((totalScore/round)*10))/10;
                courseRuleVo.setMinimumPassScore(score);
            }
            if(courseRuleVo.getMinimumPassScore()!=null){//指定分数线
                score=(double)(Math.round((courseRuleVo.getMinimumPassScore())*10))/10;
                courseRuleVo.setMinimumPassScore(score);
            }
            courseRuleVo.setNumber(number);
            courseRuleVo.setApplyType(applyType);
            ruleDao.addExemptionCourseRule(courseRuleVo);

            return "common.addsuccess";
        }else{
            List<ExemptionCourseMaterial> list = courseRuleVo.getList();
            if(CollectionUtil.isNotEmpty(list)){
                ruleDao.addExemptionCourseRule(courseRuleVo);
                Long ruleId = courseRuleVo.getId();
                for (ExemptionCourseMaterial exemptionCourseMaterial : list) {
                    exemptionCourseMaterial.setExemptionRuleId(ruleId);
                }
                materialDao.addExemptionMaterial(list);
                return "common.addsuccess";
            }else{
                return "common.parameterError";
            }
        }

    }

    //修改申请规则待做

    /**
    *@Description: 查询免修免考管理
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/2 17:39
    */
    @Override
    public PageResult<ExemptionApplyManageVo> findExemptionApply(PageCondition<ExemptionApplyCondition> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ExemptionApplyManageVo> exemptionApply = applyDao.findExemptionApply(condition.getCondition());
        return new PageResult<>(exemptionApply);
    }

    /**
    *@Description: 新增免修免考
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/12 10:44
    */
    @Override
    public String addExemptionApply(ExemptionApplyManage applyManage) {//材料上传todo
        if(applyManage.getApplyType() == null){
            return "common.parameterError";
        }
        //查询是否重复申请
        List<ExemptionApplyManage> exemptionApplyManageVo = applyDao.applyRepeat(applyManage.getCalendarId(), applyManage.getStudentCode(), applyManage.getCourseCode());
        if(exemptionApplyManageVo!=null && exemptionApplyManageVo.size() > 0){
            return "common.exist";
        }
        if(applyManage.getApplyType()==0){//成绩申请
            applyManage.setScore("免修");
            applyManage.setExamineResult(ExemptionCourseServiceImpl.SUCCESS_STATUS);
        }else{
            applyManage.setExamineResult(ExemptionCourseServiceImpl.STATUS);
        }
        applyDao.insertSelective(applyManage);
        return "common.addsuccess";
    }
    
    /**
     *@Description: 新增免修免考
     *@Param:
     *@return: 
     *@Author: bear
     *@date: 2019/2/12 10:44
     */
    @Override
    public String adminAddApply(ExemptionApplyManage applyManage) {
    	if(applyManage.getApplyType() == null){
    		return "common.parameterError";
    	}
    	//查询是否重复申请
    	List<ExemptionApplyManage> exemptionApplyManageVo = applyDao.applyRepeat(applyManage.getCalendarId(), applyManage.getStudentCode(), applyManage.getCourseCode());
    	if(exemptionApplyManageVo!=null && exemptionApplyManageVo.size() > 0){
    		return "common.exist";
    	}
	    String[] codes = applyManage.getCourseCode().split(",");
	    for (String code : codes) {
	    	List<ExemptionApplyManage> exemptionApplyManage = applyDao.applyRepeat(applyManage.getCalendarId(), applyManage.getStudentCode(), code);
	    	if(exemptionApplyManage!=null  && exemptionApplyManage.size() > 0){
	    		return "common.exist";
	    	}
		}
	    
	    if (applyManage.getApplyType() == 1 && StringUtils.isNotEmpty(applyManage.getExemptionType())) {
    		return "Please.select";
		}
	    //判断本门可是否已经选课或者完成
	    List<String> haveScoreCourse = getHaveScoreCourse(applyManage.getStudentCode(),applyManage.getCourseCode());
	    if (CollectionUtil.isNotEmpty(haveScoreCourse)) {
	    	return "please.delete.before.applying";
		}
	    //判断本学期是否已经选课
	    List<String> selectedCourse = getSelectedCourse(applyManage.getStudentCode(),applyManage.getCourseCode(),applyManage.getCalendarId());
	    if (CollectionUtil.isNotEmpty(selectedCourse)) {
	    	return "please.drop.the.relevant.courses";
	    }
	  //查找申请信息
	    Student student = studentDao.findStudentByCode(applyManage.getStudentCode());
	    applyManage.setName(student.getName());
		String[] courseCodes = applyManage.getCourseCode().split(",");
		String[] courseNames = applyManage.getCourseName().split(",");
		applyManage.setScore("免修");
		applyManage.setExamineResult(ExemptionCourseServiceImpl.SUCCESS_STATUS);
		for (int i = 0; i < courseCodes.length; i++) {
			if (courseCodes.length == courseNames.length) {
				applyManage.setCourseCode(courseCodes[i]);
//				Example example = new Example(Course.class);
//				example.createCriteria().andEqualTo("code", courseCodes[i]);
//				Course course = courseDao.selectOneByExample(example);
//				if (course != null) {
//					applyManage.setCourseName(course.getName());
//				}
				applyManage.setCourseName(courseNames[i]);
				saveExemptionScore(applyManage, courseCodes[i]);
				int code = saveExemptionScore(applyManage, courseCodes[i]);
        		if(code != 200){
        			return "common.editError";
        		}
				applyDao.insertSelective(applyManage);
			}
		}
		 applicationContext
         .publishEvent(new ElectLoadEvent(applyManage.getCalendarId(), applyManage.getStudentCode()));
    	return "common.addsuccess";
    }

    /**
    *@Description: 申请条件限制
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/12 12:52
    */
    @Override
    public RestResult<ExemptionCourseMaterialVo> addExemptionApplyConditionLimit(ExemptionApplyManage applyManage) {
        ExemptionCourseMaterialVo courseMaterialVo=new ExemptionCourseMaterialVo();
        Long calendarId = applyManage.getCalendarId();
        String courseCode = applyManage.getCourseCode();
        String studentCode = applyManage.getStudentCode();
        if(calendarId==null||"".equals(courseCode)||"".equals(studentCode)){
            return RestResult.fail("common.parameterError");
        }
        //个人计划中的课程待做（页面可以直接下拉个人计划课程）
        //查询该课程是否在免修范围内
        ExemptionCourse course=new ExemptionCourse();
        course.setCalendarId(applyManage.getCalendarId());
        course.setCourseCode(applyManage.getCourseCode());
        Page<ExemptionCourseVo> exemptionCourse = exemptionCourseDao.findExemptionCourse(course);
        if(exemptionCourse==null||CollectionUtil.isEmpty(exemptionCourse.getResult())){
            return RestResult.fail("该课程不在免修免考范围内");
        }
        //已选课不能免修
        int i = elcCourseTakeDao.findIsEletionCourse(studentCode, calendarId, courseCode);
        if(i!=0){
            return RestResult.fail("该课程已经选课，不能申请免修免考");
        }

        Student student = studentDao.findStudentByCode(studentCode);
        if(applyManage.getApplyType()==0){//成绩申请条件限制
        //查询成绩，校验规则
            ExemptionCourseScore studentScore = scoreDao.findStudentScore(calendarId, studentCode, courseCode);
            if(studentScore==null){
                return RestResult.fail("该学生没有入学考试成绩");
            }
            Double score = studentScore.getScore();
            ExemptionCourseRuleVo scoreOrMaterial = ruleDao.findScoreOrMaterial(calendarId, courseCode, student,applyManage.getApplyType());
            if(scoreOrMaterial==null){
                return RestResult.fail("没有该入学成绩规则");
            }
            Double minimumPassScore = scoreOrMaterial.getMinimumPassScore();
            courseMaterialVo.setScore(score);
            courseMaterialVo.setMinimumPassScore(minimumPassScore);
            return RestResult.successData(courseMaterialVo);

        }else{//材料规则限制
            student.setGrade(null);
            ExemptionCourseRuleVo courseRuleVo = ruleDao.findScoreOrMaterial(calendarId, courseCode, student,applyManage.getApplyType());
            if(courseRuleVo==null){
                return RestResult.fail("没有该材料申请规则");
            }
            List<ExemptionCourseMaterial> materialById = materialDao.findMaterialById(courseRuleVo.getId());
            courseMaterialVo.setList(materialById);
            return RestResult.successData(courseMaterialVo);
        }
    }

    /**
    *@Description: 查询学生信息
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/13 10:47
    */
    @Override
    public RestResult<Student> findStudentMessage(String studentCode) {
        if("".equals(studentCode)){
            return RestResult.fail("common.parameterError");
        }
        Student student = studentDao.findStudentByCode(studentCode);
        return RestResult.successData(student);
    }

    /**
    *@Description: 删除申请免修免考人
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/13 11:05
    */
    @Override
    public String deleteExemptionApply(List<Long> ids) {
        if(CollectionUtil.isEmpty(ids)){
            return "common.parameterError";
        }
        applyDao.deleteExemptionApply(ids);
        return "common.deleteSuccess";
    }

    /**
    *@Description: 批量审批
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/2/13 11:21
    */
    @Override
    public String approvalExemptionApply(List<Long> ids,Integer status,String auditor) {
        if(CollectionUtil.isEmpty(ids)){
            return "common.parameterError";
        }
        String score="";
        if(status==1){
            score="免修";
        }
        applyDao.approvalExemptionApply(ids,status,score,auditor);
        return "common.editSuccess";
    }

    /**
    *@Description: 编辑申请人信息
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/2/13 14:03
    */
    @Override
    public String editExemptionApply(ExemptionApplyManage applyManage) {
        if(applyManage.getId()==null){
            return "common.parameterError";
        }

        if(applyManage.getApplyType()==0){//成绩申请
            applyManage.setScore("免修");
            applyManage.setExamineResult(ExemptionCourseServiceImpl.SUCCESS_STATUS);
        }else{
            applyManage.setScore("");
            applyManage.setExamineResult(ExemptionCourseServiceImpl.STATUS);
        }
        applyDao.updateByPrimaryKeySelective(applyManage);
        return "common.editSuccess";
    }

    /**
    *@Description: 免修免考申请管理导出
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/2/20 9:27
    */
    @Override
    public String export(ExemptionApplyCondition condition) throws Exception{
        PageCondition<ExemptionApplyCondition> pageCondition = new PageCondition<ExemptionApplyCondition>();
        pageCondition.setCondition(condition);
        pageCondition.setPageSize_(Constants.ZERO);
        pageCondition.setPageNum_(Constants.ZERO);
        PageResult<ExemptionApplyManageVo> exemptionApply = findExemptionApply(pageCondition);
        if(exemptionApply!=null){
            List<ExemptionApplyManageVo> list = exemptionApply.getList();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for (SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            for (ExemptionApplyManageVo exemptionApplyManageVo : list) {
                if (0 != schoolCalendarMap.size()) {
                    String schoolCalendarName = schoolCalendarMap.get(exemptionApplyManageVo.getCalendarId());
                    if (StringUtils.isNotEmpty(schoolCalendarName)) {
                        exemptionApplyManageVo.setCalendarName(schoolCalendarName);
                    }
                }

                if(exemptionApplyManageVo.getExamineResult()==0){
                    exemptionApplyManageVo.setExamineResultStr("待审批");
                }else if(exemptionApplyManageVo.getExamineResult()==1){
                    exemptionApplyManageVo.setExamineResultStr("审批通过");
                }else{
                    exemptionApplyManageVo.setExamineResultStr("审批未通过");
                }

            }
            if (list == null) {
                list = new ArrayList<>();
            }
            GeneralExcelDesigner design = getDesign();
            design.setDatas(list);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "exemptionApplyManage.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;
        }
        return "";
    }

    /**
    *@Description: 导入入学成绩
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/28 10:48
    */
    @Override
    public String addExcel(List<ExemptionCourseScore> datas, Long calendarId) {
        List<String> list=new ArrayList<>();
        for (ExemptionCourseScore data : datas) {
            //查询是否有该课程该学期成绩
            ExemptionCourseScore studentScore = scoreDao.findStudentScore(calendarId, data.getStudentCode(), data.getCourseCode());
            if(studentScore==null){//没有成绩
                data.setCalendarId(calendarId);
                scoreDao.insertSelective(data);
            }else{//有成绩不能导入
                list.add(data.getStudentCode()+" "+data.getCourseCode());
            }
        }

        if(list.size()>0){
            list.add("成绩存在");
            return StringUtils.join(list,",");
        }

        return StringUtils.EMPTY;
    }

    /**
    *@Description: 导入免修申请
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/1 10:57
    */
    @Override
    public String addExcelApply(List<ExemptionApplyManage> datas, Long calendarId) {
        List<String> list =new ArrayList<>();
        for (ExemptionApplyManage data : datas) {
            data.setCalendarId(calendarId);
            //查询是否重复申请
            List<ExemptionApplyManage> exemptionApplyManageVo = applyDao.applyRepeat(data.getCalendarId(), data.getStudentCode(), data.getCourseCode());
            if(exemptionApplyManageVo!=null && exemptionApplyManageVo.size() > 0){
                list.add(data.getStudentCode());
            }else{
                if(data.getApplyType()==0){//成绩申请
                    data.setScore("免修");
                    data.setExamineResult(ExemptionCourseServiceImpl.SUCCESS_STATUS);
                }else{
                    data.setExamineResult(ExemptionCourseServiceImpl.STATUS);
                }
                applyDao.insertSelective(data);
            }
        }

        if(list.size()>0){
            list.add("该学期课程已经申请");
            return StringUtils.join(list,",");
        }

        return StringUtils.EMPTY;
    }

    /**
    *@Description: 新增下拉代码取值
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/2 9:59
    */
    @Override
    public RestResult<List<ExemptionCourseVo>> filterCourseCode(ExemptionCourseRuleVo courseRuleVo, Integer applyType) {
        //查询学期所有免修课程
        ExemptionCourseVo vo=new ExemptionCourseVo();
        vo.setCalendarId(courseRuleVo.getCalendarId());
        Page<ExemptionCourseVo> exemptionCourse = exemptionCourseDao.findExemptionCourse(vo);
        if(exemptionCourse!=null){
            List<ExemptionCourseVo> result = exemptionCourse.getResult();
            //查询该条件下已有规则课程代码
           List<String> courseCodes= ruleDao.findRuleExist(courseRuleVo,applyType);
           if(CollectionUtil.isNotEmpty(result)){
               List<ExemptionCourseVo> collect = result.stream().filter((ExemptionCourseVo courseVo) -> !courseCodes.contains(courseVo.getCourseCode())).collect(Collectors.toList());
               return RestResult.successData(collect);
           }
        }

        return RestResult.fail("请先添加免修免考课程");

    }

    /**
    *@Description: 编辑申请规则
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/6/4 18:28
    */
    @Override
    public String editExemptionCourseRule(ExemptionCourseRuleVo courseRuleVo) {
        //todo
        return null;
    }

    private GeneralExcelDesigner getDesign() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.calendarName"), "calendarName");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "name");
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, SessionUtils.getLang());
                });

        design.addCell(I18nUtil.getMsg("exemptionApply.major"), "profession").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_ZY", value, SessionUtils.getLang());
                });

        design.addCell(I18nUtil.getMsg("exemptionApply.examineResult"), "examineResultStr");
        design.addCell(I18nUtil.getMsg("exemptionApply.examineAuditor"), "auditor");
        return design;
    }


	@Override
	public PageResult<ExemptionStudentCountVo> exemptionCount(PageCondition<ExemptionQuery> page) {

        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<ExemptionStudentCountVo> countResult = applyDao.exemptionCount(page.getCondition());
        
        return new PageResult<>(countResult);
	}


	@Override
	public RestResult<String> exemptionCountExport(ExemptionQuery condition) {
		String path="";
        try {
        	 PageCondition<ExemptionQuery> pageCondition = new PageCondition<ExemptionQuery>();
             pageCondition.setCondition(condition);
             pageCondition.setPageSize_(100);
             int pageNum = 0;
             pageCondition.setPageNum_(pageNum);
             List<ExemptionStudentCountVo> list = new ArrayList<>();
             while (true)
             {
                 pageNum++;
                 pageCondition.setPageNum_(pageNum);
                 PageResult<ExemptionStudentCountVo> studentList = exemptionCount(pageCondition);
                 
                 list.addAll(studentList.getList());

                 if (studentList.getTotal_() <= list.size())
                 {
                     break;
                 }
             }
             list = SpringUtils.convert(list);
        	@SuppressWarnings("unchecked")
			ExcelEntityExport<ElcResultDto> excelExport = new ExcelEntityExport(list,
        			excelStoreConfig.getExemptionCountExportKey(),
        			excelStoreConfig.getExemptionCountExportTitle(),
        			cacheDirectory);
        	path = excelExport.exportExcelToCacheDirectory("研究生免修免考统计");
        }catch (Exception e){
            return RestResult.failData("minor.export.fail");
        }
        return RestResult.successData("minor.export.success",path);
	}

    /**
    *@Description: 批量审批
    *@Param: 
    *@return: 
    *@Author: bear
    *@date: 2019/2/13 11:21
    */
	@Transactional
    @Override
    public String approvalGraduateExemptionApply(List<Long> ids,Integer status) {
    	Session session = SessionUtils.getCurrentSession();
    	String auditor = session.realUid();
        if(CollectionUtil.isEmpty(ids)){
            return "common.parameterError";
        }
        String score="";
        if(status==1){
        	score="免修";
        	for (Long id : ids) {
				//查找申请信息
        		ExemptionApplyManage applyRecord = applyDao.selectByPrimaryKey(id);
        		int code = saveExemptionScore(applyRecord, applyRecord.getCourseCode());
        		if(code != 200){
        			return "common.editError";
        		}
        		applicationContext
                .publishEvent(new ElectLoadEvent(applyRecord.getCalendarId(), applyRecord.getStudentCode()));
			}
            
        }else{
        	List<Long> optList = new ArrayList<>();
        	//查询申请表，判断是否状态为审批通过，若审批通过，则无法改为其他状态
        	for (Long id : ids) {
				ExemptionApplyManage applyRecord = applyDao.selectByPrimaryKey(id);
				if (applyRecord != null) {
					if (applyRecord.getExamineResult().intValue() != 1) {
						optList.add(id);
					}
				}
        	}   
        	ids.clear();
        	ids.addAll(optList);
        }
        if (CollectionUtil.isNotEmpty(ids)) {
        	applyDao.approvalExemptionApply(ids,status,score,auditor);
		}
        return "common.editSuccess";
    }

	@Override
	public PageResult<ExemptionApplyManageVo> findGraduateExemptionApply(PageCondition<ExemptionQuery> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		Page<ExemptionApplyManageVo> exemptionApply = applyDao.findGraduteExemptionApply(condition.getCondition());
		for (ExemptionApplyManageVo exemptionApplyManageVo : exemptionApply) {
			exemptionApplyManageVo.setApplyCourse(exemptionApplyManageVo.getCourseCode()+""+exemptionApplyManageVo.getCourseName());
		}
		return new PageResult<>(exemptionApply);
	}


	@Override
	public RestResult<String> findGraduateExemptionApplyExport(ExemptionQuery page) {
		String path="";
        try {
        	PageCondition<ExemptionQuery> pageCondition = new PageCondition<ExemptionQuery>();
            pageCondition.setCondition(page);
            pageCondition.setPageSize_(100);
            int pageNum = 0;
            pageCondition.setPageNum_(pageNum);
            List<ExemptionApplyManageVo> list = new ArrayList<>();
            while (true)
            {
                pageNum++;
                pageCondition.setPageNum_(pageNum);
                PageResult<ExemptionApplyManageVo> applyResult = findGraduateExemptionApply(pageCondition);
                
                list.addAll(applyResult.getList());

                if (applyResult.getTotal_() <= list.size())
                {
                    break;
                }
            }
            for (ExemptionApplyManageVo exemptionApplyManageVo : list) {
            	exemptionApplyManageVo.setStudentId(exemptionApplyManageVo.getStudentCode());
            	exemptionApplyManageVo.setResult(exemptionApplyManageVo.getExamineResult().toString());
            	exemptionApplyManageVo.setStudentName(exemptionApplyManageVo.getName());
			}
            list = SpringUtils.convert(list);
        	@SuppressWarnings("unchecked")
			ExcelEntityExport<ExemptionApplyManageVo> excelExport = new ExcelEntityExport(list,
        			excelStoreConfig.getGraduateExemptionApplyExportKey(),
        			excelStoreConfig.getGraduateExemptionApplyExportTitle(),
        			cacheDirectory);
        	path = excelExport.exportExcelToCacheDirectory("研究生免修免考申请");
        }catch (Exception e){
            return RestResult.failData("minor.export.fail");
        }
        return RestResult.successData("minor.export.success",path);
	}

	@Override
	public StudentAndCourseVo findCourseCode(String studentId, Long calendarId) {
		Session session = SessionUtils.getCurrentSession();
		Student student = new Student();
		List<ExemptionStudentCourseVo> applyCourses = new ArrayList<ExemptionStudentCourseVo>();
		student = studentDao.findStudentByCode(studentId);
		if (student == null) {
			RestResult.fail("common.notExist",studentId);
		}
		Example example = new Example(ExemptionApplyAuditSwitch.class);
		example.createCriteria().andEqualTo("applyOpen",Constants.ONE).andEqualTo("deleteStatus",Constants.ZERO);
		List<ExemptionApplyAuditSwitch> applySwitchs = exemptionAuditSwitchDao.selectByExample(example);
		ExemptionApplyAuditSwitch applySwitch = getStudentExemptionSwitch(student, applySwitchs);
		if (applySwitch == null) {
			StudentAndCourseVo studentAndCourseVo = new StudentAndCourseVo();
			studentAndCourseVo.setStudent(student);
			studentAndCourseVo.setApplyCourse(applyCourses);
			return studentAndCourseVo;
		}
		ValueOperations<String, List<DictCache>> ops = redisTemplate.opsForValue();
		List<DictCache> firstForeignLanguageList = ops.get(DictCache.getKey("X_DYWY"));
		//调取字典表，获取第一外语列表
		Example scoreexample = new Example(ExemptionCourseScore.class);
		scoreexample.createCriteria().andEqualTo("studentCode",studentId);
		List<ExemptionCourseScore>  scoreList = scoreDao.selectByExample(scoreexample);
		boolean scoreFlag = false;
		String firstForeignLanguageCode = "";
		
		for (ExemptionCourseScore exemptionCourseScore : scoreList) {
			for (DictCache firstForeignLanguage : firstForeignLanguageList) {
				if (StringUtils.equalsIgnoreCase(exemptionCourseScore.getCourseCode(), firstForeignLanguage.getCode())) {
					firstForeignLanguageCode = firstForeignLanguage.getCode();
					scoreFlag = true;
					break;
				}
			}
			if (scoreFlag) {
				break;
			}
		}
		final String  languageCode = firstForeignLanguageCode;
		
		Set<PlanCourse> studentExemptionCouses = getStudentExemptionCouses(student, applySwitch,calendarId,languageCode,scoreFlag,session);
		for (PlanCourse course : studentExemptionCouses) {
			ExemptionStudentCourseVo applyCourse = new ExemptionStudentCourseVo();
			applyCourse.setCourseNameAndCode(course.getCourseCode() + course.getCourseName() + "");
			applyCourse.setApplyType(Constants.ONE);
			applyCourse.setCourseCode(course.getCourseCode());
			applyCourse.setCourseName(course.getCourseName());
			applyCourses.add(applyCourse);
		}
		StudentAndCourseVo studentAndCourseVo = new StudentAndCourseVo();
		studentAndCourseVo.setStudent(student);
		studentAndCourseVo.setApplyCourse(applyCourses);
		return studentAndCourseVo;
	}


	@Override
	public StudentAndCourseVo findStudentApplyCourse(String studentId,Long calendarId) {
		Session session = SessionUtils.getCurrentSession();
		Student student = studentDao.findStudentByCode(studentId);
		Boolean isAchievement = false;
		//查找本次开通的免修免考课程
		Example example = new Example(ExemptionApplyAuditSwitch.class);
		example.createCriteria().andEqualTo("applyOpen",Constants.ONE).andEqualTo("deleteStatus",Constants.ZERO).andEqualTo("projId",session.getCurrentManageDptId());
		
		List<ExemptionApplyAuditSwitch> applySwitchs = exemptionAuditSwitchDao.selectByExample(example);
		ExemptionApplyAuditSwitch applySwitch = getStudentExemptionSwitch(student, applySwitchs);
		if (applySwitch == null) {
			StudentAndCourseVo studentAndCourseVo = new StudentAndCourseVo();
			studentAndCourseVo.setStudent(student);
			studentAndCourseVo.setApplyCourse(null);
			return studentAndCourseVo;
		}
		List<ExemptionStudentCourseVo> applyCourses = new ArrayList<ExemptionStudentCourseVo>();

		ValueOperations<String, List<DictCache>> ops = redisTemplate.opsForValue();
		List<DictCache> firstForeignLanguageList = ops.get(DictCache.getKey("X_DYWY"));
		//调取字典表，获取第一外语列表
		Example scoreexample = new Example(ExemptionCourseScore.class);
		scoreexample.createCriteria().andEqualTo("studentCode",studentId);
		List<ExemptionCourseScore>  scoreList = scoreDao.selectByExample(scoreexample);
		boolean scoreFlag = false;
		DictCache dictCache = null;
		String firstForeignLanguageCode = "";
		ExemptionCourseScore scoreModel = null;
		
		for (ExemptionCourseScore exemptionCourseScore : scoreList) {
			for (DictCache firstForeignLanguage : firstForeignLanguageList) {
				if (StringUtils.equalsIgnoreCase(exemptionCourseScore.getCourseCode(), firstForeignLanguage.getCode())) {
					scoreModel = exemptionCourseScore;
					dictCache = firstForeignLanguage;
					firstForeignLanguageCode = firstForeignLanguage.getCode();
					scoreFlag = true;
					break;
				}
			}
			if (scoreFlag) {
				break;
			}
		}
		final String  languageCode = firstForeignLanguageCode;
		
		Set<PlanCourse> optCourses = getStudentExemptionCouses(student, applySwitch,calendarId,languageCode,scoreFlag,session);
		
		
		
		//满足优线生申请条件
		if (CollectionUtil.isNotEmpty(optCourses)) {
			if (scoreModel != null && applySwitch.getExcellentScore().doubleValue() <= scoreModel.getScore().doubleValue()) {
				isAchievement = true;
				ExemptionStudentCourseVo applyCourse = new ExemptionStudentCourseVo();
				String courseNameAndCode = " ";
				List<String> courseCode = new ArrayList<>();
				List<String> courseName = new ArrayList<>();
				for (PlanCourse course : optCourses) {
					courseNameAndCode = courseNameAndCode + course.getCourseCode() + course.getCourseName() + " ";
					applyCourse.setFirstForeignLanguageCode(scoreModel.getCourseCode());
					applyCourse.setFirstForeignLanguageScore(scoreModel.getScore());
					courseCode.add(course.getCourseCode());
					courseName.add(course.getCourseName());
					applyCourse.setApplyType(Constants.ZERO);
				}
				applyCourse.setFirstForeignLanguageName(dictCache.getNameCN());
				applyCourse.setCourseNameAndCode(courseNameAndCode);
				applyCourse.setCourseCode(StringUtils.join(courseCode.toArray(new String[courseCode.size()]), ","));
				applyCourse.setCourseName(StringUtils.join(courseName.toArray(new String[courseName.size()]), ","));
				Integer examineResult = getGraduteExemptionApplyRecord(calendarId ,studentId,StringUtils.join(courseCode.toArray(new String[courseCode.size()]), ","));
				applyCourse.setExamineResult(examineResult);
				applyCourses.add(applyCourse);
			}else{
				for (PlanCourse course : optCourses) {
					ExemptionStudentCourseVo applyCourse = new ExemptionStudentCourseVo();
					applyCourse.setCourseNameAndCode(course.getCourseCode() + course.getCourseName() + "");
					applyCourse.setFirstForeignLanguageCode(scoreModel.getCourseCode());
					applyCourse.setFirstForeignLanguageName(dictCache.getNameCN());
					applyCourse.setFirstForeignLanguageScore(scoreModel.getScore());
					applyCourse.setApplyType(Constants.ONE);
					applyCourse.setCourseCode(course.getCourseCode());
					applyCourse.setCourseName(course.getCourseName());
					Integer examineResult = getGraduteExemptionApplyRecord(calendarId ,studentId,course.getCourseCode());
					applyCourse.setExamineResult(examineResult);
					applyCourses.add(applyCourse);
				}
			}
		}
		StudentAndCourseVo studentAndCourseVo = new StudentAndCourseVo();
		studentAndCourseVo.setStudent(student);
		studentAndCourseVo.setIsAchievement(isAchievement);
		studentAndCourseVo.setApplyCourse(applyCourses);
		return studentAndCourseVo;
	}

	//根据学号和课程code查找是否已有申请记录
	private Integer getGraduteExemptionApplyRecord(Long calendarId ,String studentCode,String courseCode){
		//查询是否重复申请
		Integer examineResult = null;
	    List<ExemptionApplyManage> exemptionApplyManageVo = applyDao.applyRecord(calendarId, studentCode, courseCode);
	    for (ExemptionApplyManage exemptionApplyManage : exemptionApplyManageVo) {
			
	    	if (exemptionApplyManage.getExamineResult() != null && exemptionApplyManage.getExamineResult() == 1) {
	    		return exemptionApplyManage.getExamineResult();
	    	}else if (exemptionApplyManage.getExamineResult() != null && exemptionApplyManage.getExamineResult() == 0) {
	    		return exemptionApplyManage.getExamineResult();
	    	}else{
	    		examineResult=exemptionApplyManage.getExamineResult();
	    	}
		}
		return examineResult;
	}
	
	private ExemptionApplyAuditSwitch getStudentExemptionSwitch(Student student, List<ExemptionApplyAuditSwitch> applySwitchs) {
		ExemptionApplyAuditSwitch applyAuditSwitch = null;
		for (ExemptionApplyAuditSwitch applySwitch : applySwitchs) {
			boolean openObjectFlag = contains(applySwitch.getGrades(),student.getGrade()+"")&&
					 contains(applySwitch.getTrainingLevels(),student.getTrainingLevel())&&
					 contains(applySwitch.getFormLearnings(),student.getFormLearning())&&
					 contains(applySwitch.getTrainingCategorys(),student.getTrainingCategory())&&
					 contains(applySwitch.getDegreeTypes(),student.getDegreeType())
//					 && contains(applySwitch.getEnrolSeason(),student.getEnrolSeason())
					 ;
			if (openObjectFlag) {
				applyAuditSwitch = applySwitch;
				break;
			}
		}
		return applyAuditSwitch;
	}
	/**
	 *查找学生免修免考课程
	 * @param student
	 * @param applySwitch
	 * @param scoreFlag 
	 * @param languageCode 
	 * @param session 
	 * @param collect 
	 * @return
	 */
	private Set<PlanCourse> getStudentExemptionCouses(Student student, ExemptionApplyAuditSwitch applySwitch, Long calendarId, String languageCode, boolean scoreFlag, Session session) {
		StudentCultureRel studentCultureRel = new StudentCultureRel();
		studentCultureRel.setPageNum_(1);
		studentCultureRel.setPageSize_(100);
		studentCultureRel.setProjId(session.getCurrentManageDptId());
		studentCultureRel.setStudentId(student.getStudentCode());
		List<StudentCultureRel> findStudentCultureRelList = CultureSerivceInvoker.findStudentCultureRelList(studentCultureRel);
		
		
		//培养计划课程
		List<PlanCourseDto> courseType = CultureSerivceInvoker.findUnGraduateCourse(student.getStudentCode());
		Set<PlanCourse> planCourses = new HashSet<>();//培养课程
		if (CollectionUtil.isNotEmpty(courseType)) {
			for (PlanCourseDto planCourse : courseType) {
                List<PlanCourseTypeDto> list = planCourse.getList();
                if(CollectionUtil.isNotEmpty(list)){
                    for (PlanCourseTypeDto planCourseTypeDto : list) {//培养课程
                        PlanCourse pl=new PlanCourse();
                        pl.setCourseCode(planCourseTypeDto.getCourseCode());
                        pl.setCourseName(planCourseTypeDto.getName());
                        planCourses.add(pl);
                    }
                }
            }
		}
		
		//培养计划与第一外语的交集
		Set<PlanCourse> studentlanguageCourse = new HashSet<>();
		for (StudentCultureRel code : findStudentCultureRelList) {
			for (PlanCourse planCourse : planCourses) {
				if (StringUtils.equalsIgnoreCase(planCourse.getCourseCode(), code.getCourseCode())) {
					studentlanguageCourse.add(planCourse);
				}
			}
		}
		return studentlanguageCourse;
	}

	/**查看学生是否满足条件*/
    boolean contains(String source, String taget)
    {
        if (StringUtils.isBlank(source))
        {
            return true;
        }
        if (StringUtils.isBlank(taget))
        {
        	return false;
        }
        return source.contains(taget);
    }

    @Transactional
	@Override
	public RestResult<?> addGraduateExemptionApply(ExemptionApplyManage applyManage) {
		if("".equals(applyManage.getApplyType())){
	        return RestResult.fail("common.parameterError");
	    }
	    //查询是否重复申请
	    List<ExemptionApplyManage> exemptionApplyManageVo = applyDao.applyRepeat(applyManage.getCalendarId(), applyManage.getStudentCode(), applyManage.getCourseCode());
	    if(exemptionApplyManageVo!=null && exemptionApplyManageVo.size() > 0){
	    	return RestResult.fail("common.exist",applyManage.getCourseCode());
	    }
	    String[] codes = applyManage.getCourseCode().split(",");
	    for (String code : codes) {
	    	List<ExemptionApplyManage> exemptionApplyManage = applyDao.applyRepeat(applyManage.getCalendarId(), applyManage.getStudentCode(), code);
	    	if(exemptionApplyManage!=null && exemptionApplyManage.size() > 0){
	    		return RestResult.fail("common.exist",applyManage.getCourseCode());
	    	}
		}
	    //判断本门可是否已经选课或者完成
	    List<String> haveScoreCourse = getHaveScoreCourse(applyManage.getStudentCode(),applyManage.getCourseCode());
	    if (CollectionUtil.isNotEmpty(haveScoreCourse)) {
	    	return RestResult.fail("please.delete.before.applying");
		}
	    //判断本学期是否已经选课
	    List<String> selectedCourse = getSelectedCourse(applyManage.getStudentCode(),applyManage.getCourseCode(),applyManage.getCalendarId());
	    if (CollectionUtil.isNotEmpty(selectedCourse)) {
	    	return RestResult.fail("please.drop.the.relevant.courses");
	    }
	    
	    if(applyManage.getApplyType()==0){//成绩申请
	        applyManage.setScore("免修");
	        applyManage.setExamineResult(ExemptionCourseServiceImpl.SUCCESS_STATUS);
	      //查找申请信息
    		String[] courseCodes = applyManage.getCourseCode().split(",");
    		String[] courseNames = applyManage.getCourseName().split(",");
    		for (int i = 0; i < courseCodes.length; i++) {
				if (courseCodes.length == courseNames.length) {
					applyManage.setCourseCode(courseCodes[i]);
					applyManage.setCourseName(courseNames[i]);
					int code = saveExemptionScore(applyManage, courseCodes[i]);
	        		if(code != 200){
	        			return RestResult.fail("common.editError","");
	        		}
					applyDao.insertSelective(applyManage);
				}
			}
    		 applicationContext
             .publishEvent(new ElectLoadEvent(applyManage.getCalendarId(), applyManage.getStudentCode()));
	    }else{
	    	if (StringUtils.isNotEmpty(applyManage.getExemptionType())) {
	    		return RestResult.fail("Please.select");
			}
	        applyManage.setExamineResult(ExemptionCourseServiceImpl.STATUS);
	        String[] courseCodes = applyManage.getCourseCode().split(",");
    		String[] courseNames = applyManage.getCourseName().split(",");
    		for (int i = 0; i < courseCodes.length; i++) {
				if (courseCodes.length == courseNames.length) {
					applyManage.setCourseCode(courseCodes[i]);
					applyManage.setCourseName(courseNames[i]);
					applyDao.insertSelective(applyManage);
				}
			}
	    }
	    return RestResult.success("common.addsuccess","");
	}

	/**
	 * 向成绩模块添加记录
	 * @param applyManage
	 * @param courseCodes
	 * @return 
	 */
	private int saveExemptionScore(ExemptionApplyManage applyManage, String courseCode) {
		
		
		logger.info("save score start -----------------------------");
		//调用成绩接口，向成绩中添加数据
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("studentId", applyManage.getStudentCode());
		jsonObject.put("studentName", applyManage.getName());
		jsonObject.put("calendarId", applyManage.getCalendarId());
		jsonObject.put("courseCode", courseCode);
		jsonObject.put("recoredType", 9);
		RestResult saveExemptionScore = ScoreServiceInvoker.saveExemptionScore(jsonObject);
		logger.info("save score end -----------------------------"+saveExemptionScore.getCode());
		return saveExemptionScore.getCode();
	}


	@Override
	public RestResult<?> deleteGraduteExemptionApply(List<Long> ids) {
		List<Long> effectiveIds = new ArrayList<>();
		List<String> noEffectiveIds = new ArrayList<>();
		for (Long id : ids) {
			//查找申请信息
			ExemptionApplyManage applyRecord = applyDao.selectByPrimaryKey(id);
			if (applyRecord.getExamineResult().intValue() == Constants.ONE) {
				//调用成绩接口，查看是否有成绩
				StudentScore findViolationStu = ScoreServiceInvoker.findViolationStu(applyRecord.getStudentCode(), applyRecord.getCourseCode(), applyRecord.getCalendarId());
				logger.info("======================="+findViolationStu);
				if (findViolationStu != null) {
					noEffectiveIds.add(applyRecord.getStudentCode());
				}else{
					effectiveIds.add(id);
				}
			}else{
				effectiveIds.add(id);
			}
		}
		if (CollectionUtil.isNotEmpty(effectiveIds)) {
			applyDao.deleteExemptionApply(effectiveIds);
		}
		if (CollectionUtil.isNotEmpty(noEffectiveIds)) {
			return RestResult.fail("common.faild",StringUtils.join(noEffectiveIds,","));
		}
		return RestResult.success("common.deleteSuccess",StringUtils.join(effectiveIds,","));
	}


	@Override
	public Boolean getIsOpenAuditAuthority(String projId) {
		Boolean  flag = false;
		Example example = new Example(ExemptionApplyAuditSwitch.class);
		example.createCriteria().andEqualTo("applyOpen",Constants.ONE).andEqualTo("deleteStatus",Constants.ZERO).andEqualTo("projId",projId);
		List<ExemptionApplyAuditSwitch> applySwitchs = exemptionAuditSwitchDao.selectByExample(example);
		for (ExemptionApplyAuditSwitch exemptionApplyAuditSwitch : applySwitchs) {
			if (exemptionApplyAuditSwitch.getAuditOpen().intValue() == Constants.ONE) {
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	private List<String> getHaveScoreCourse(String studentId,String courseCode){
		//学生已经完成的课程
		Set<String> courseCodes = new HashSet<>();
		List<StudentScoreVo> stuScoreBest = ScoreServiceInvoker.findStuScoreBest(studentId);
		if (CollectionUtil.isNotEmpty(stuScoreBest))
        {

            for (StudentScoreVo studentScore : stuScoreBest)
            {
                CompletedCourse lesson = new CompletedCourse();
                lesson.setCourseCode(studentScore.getCourseCode());
                lesson.setCourseName(studentScore.getCourseName());
                if (studentScore.getIsPass() != null
                    && studentScore.getIsPass().intValue() == Constants.ONE)
                {//已經完成課程
                	courseCodes.add(lesson.getCourseCode());
                }
            }
        }
		String[] applyCourseCodes = courseCode.split(",");
		List<String> applyCourse = new ArrayList<>();
		for (String applyCourseCode : applyCourseCodes) {
			for (String code : courseCodes) {
				if (code.equals(applyCourseCode)) {
					applyCourse.add(applyCourseCode);
				}
			}
		}
		
		return applyCourse;
	}
	private List<String> getSelectedCourse(String studentId,String courseCode,Long calendarId){
		//学生已经完成的课程
		Set<String> courseCodes = new HashSet<>();
		List<ElcCourseTakeVo> courseTakes =
	            elcCourseTakeDao.findSelectedCourses(studentId,calendarId);
		for (ElcCourseTakeVo elcCourseTakeVo : courseTakes) {
			courseCodes.add(elcCourseTakeVo.getCourseCode());
		}
		
		String[] applyCourseCodes = courseCode.split(",");
		List<String> applyCourse = new ArrayList<>();
		for (String applyCourseCode : applyCourseCodes) {
			for (String code : courseCodes) {
				if (code.equals(applyCourseCode)) {
					applyCourse.add(applyCourseCode);
				}
			}
		}
		
		return applyCourse;
	}
	
	
	/*private void getcourse(){
		Set<String> courseCodes = new HashSet<>();
		List<StudentScoreVo> stuScoreBest = ScoreServiceInvoker.findStuScoreBest(student.getStudentCode());
		if (CollectionUtil.isNotEmpty(stuScoreBest))
        {

            for (StudentScoreVo studentScore : stuScoreBest)
            {
                CompletedCourse lesson = new CompletedCourse();
                lesson.setCourseCode(studentScore.getCourseCode());
                lesson.setCourseName(studentScore.getCourseName());
                if (studentScore.getIsPass() != null
                    && studentScore.getIsPass().intValue() == Constants.ONE)
                {//已經完成課程
                	courseCodes.add(lesson.getCourseCode());
                }
            }
        }
		List<ElcCourseTakeVo> courseTakes =
	            elcCourseTakeDao.findSelectedCourses(student.getStudentCode(),calendarId);
		for (ElcCourseTakeVo elcCourseTakeVo : courseTakes) {
			courseCodes.add(elcCourseTakeVo.getCourseCode());
		}
		
		Set<PlanCourse> studentExemptionCouses = new HashSet<>();
		for (PlanCourse planCourse : studentlanguageCourse) {
			boolean flag = true;
			for (String code : courseCodes) {
				if (StringUtils.equalsIgnoreCase(planCourse.getCourseCode(), code)) {
					flag = false;
					break;
				}
			}
			if (flag) {
				studentExemptionCouses.add(planCourse);
			}
		}

		Set<ExemptionApplyGraduteCondition> conditionCourses = new HashSet<>();
		
		//查询免修免考条件，获得可以进行免修免考的课程
		Example conditionExample = new Example(ExemptionApplyGraduteCondition.class);
		conditionExample.createCriteria().andEqualTo("deleteStatus",Constants.ZERO);
		List<ExemptionApplyGraduteCondition> conditionList = exemptionApplyGraduateConditionDao.selectByExample(conditionExample);
		//筛选学生满足哪些课程
		for (ExemptionApplyGraduteCondition graduteCondition : conditionList) {
			boolean openObjectConditionFlag = contains(applySwitch.getTrainingLevels(),student.getTrainingLevel())&&
					 contains(applySwitch.getFormLearnings(),student.getFormLearning())&&
					 contains(applySwitch.getTrainingCategorys(),student.getTrainingCategory())&&
					 contains(applySwitch.getDegreeTypes(),student.getDegreeType());
			if (openObjectConditionFlag) {
				conditionCourses.add(graduteCondition);
			}
		}
		
		Set<PlanCourse> optCourses = new HashSet<>();
		//取交集获得学生可以进行免修免考的课程
		for (ExemptionApplyGraduteCondition conditionCourse : conditionCourses) {
			for (PlanCourse planCourse : studentExemptionCouses) {
				if (planCourse.getCourseCode().equals(conditionCourse.getCourseCode())) {
					optCourses.add(planCourse);
				}
			}
		}
	}*/
}
