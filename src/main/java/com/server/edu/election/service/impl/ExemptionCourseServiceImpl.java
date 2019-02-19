package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.*;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.vo.*;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @description: 免修免考课程实现类
 * @author: bear
 * @create: 2019-01-31 09:56
 */

@Service
@Primary
public class ExemptionCourseServiceImpl implements ExemptionCourseService{

    @Autowired
    private ExemptionCourseDao exemptionCourseDao;

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
    public PageResult<ExemptionCourseVo> findExemptionCourse(PageCondition<ExemptionCourse> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ExemptionCourseVo> exemptionCourse = exemptionCourseDao.findExemptionCourse(condition.getCondition());
        if(exemptionCourse!=null){
            List<ExemptionCourseVo> result = exemptionCourse.getResult();
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            if(schoolCalendarMap.size()!=0){
                for (ExemptionCourseVo exemptionCourseVo : result) {
                    String schoolCalendarName = schoolCalendarMap.get(exemptionCourseVo.getCalendarId());
                    if(StringUtils.isNotEmpty(schoolCalendarName)) {
                        exemptionCourseVo.setCalendarName(schoolCalendarName);
                    }
                }
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
        ExemptionCourse ex=new ExemptionCourse();
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
        ExemptionCourse ex=new ExemptionCourse();
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
    *@Description: 查询免修免考入学成绩//todo导入成绩
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
    public PageResult<ExemptionCourseRuleVo> findExemptionRule(PageCondition<ExemptionCourseRule> rulePageCondition) {
        PageHelper.startPage(rulePageCondition.getPageNum_(), rulePageCondition.getPageSize_());
        Page<ExemptionCourseRuleVo> exemptionCourseRule = ruleDao.findExemptionCourseRule(rulePageCondition.getCondition());
        if(exemptionCourseRule!=null){
            List<SchoolCalendarVo> schoolCalendarList = BaseresServiceInvoker.getSchoolCalendarList();
            Map<Long, String> schoolCalendarMap = new HashMap<>();
            for(SchoolCalendarVo schoolCalendarVo : schoolCalendarList) {
                schoolCalendarMap.put(schoolCalendarVo.getId(), schoolCalendarVo.getFullName());
            }
            List<ExemptionCourseRuleVo> result = exemptionCourseRule.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                for (ExemptionCourseRuleVo exemptionCourseRuleVo : result) {
                    String calendarName=schoolCalendarMap.get(exemptionCourseRuleVo.getCalendarId());
                    if(StringUtils.isNotEmpty(calendarName)) {
                        exemptionCourseRuleVo.setCalendarName(calendarName);
                    }
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
    public String addExemptionCourseRule(ExemptionCourseRuleVo courseRuleVo, Integer applyType) {
        String courseCode = courseRuleVo.getCourseCode();
        Long calendarId = courseRuleVo.getCalendarId();
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
                    totalScore+=courseScore.get(i).getScore();
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
                courseRuleVo.setApplyType(applyType);
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
        if("".equals(applyManage.getApplyType())){
            return "common.parameterError";
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
    public String approvalExemptionApply(List<Long> ids,Integer status) {
        if(CollectionUtil.isEmpty(ids)){
            return "common.parameterError";
        }
        String score="";
        if(status==1){
            score="免修";
        }
        applyDao.approvalExemptionApply(ids,status,score);
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

}
