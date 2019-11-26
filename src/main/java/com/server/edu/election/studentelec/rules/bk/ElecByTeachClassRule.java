package com.server.edu.election.studentelec.rules.bk;

import java.util.List;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElectionParameterDao;
import com.server.edu.election.dao.ElectionRuleDao;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dto.ElcCourseLimitDto;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;
import tk.mybatis.mapper.entity.Example;

/**
 * 按教学班选课
 * <p>
 * <p>
 * ElectableLessonByTeachClassFilter
 */
@Component("ElecByTeachClassRule")
public class ElecByTeachClassRule extends AbstractElecRuleExceutorBk
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    public ElecByTeachClassRule()
    {
        super();
    }
    
    @Autowired
    private TeachingClassElectiveRestrictAttrDao restrictAttrDao;

    @Autowired
    private ElectionRuleDao ectionRuleDao;

    @Autowired
    private ElectionParameterDao electionParameterDao;
    
    @Autowired
    private ElcCourseTakeDao takeDao;
    
    private static final String IS_OVERSEAS_ = "1";
    
    private static final String IS_NOT_OVERSEAS_ = "0";
    
    //    private static final String NOT_DISTINGYISH_SEX = "0";//不区分性别
    private static final String MALE = "1";//男性
    
    private static final String FEMALE = "2";//女性
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {

        //教学班规则
        Example electionRuleExample = new Example(ElectionRule.class);
        String projectId = context.getRequest().getProjectId();
        electionRuleExample.createCriteria().andEqualTo("serviceName", "ElecByTeachClassRule")
                .andEqualTo("managerDeptId", projectId);
        ElectionRule rule =
                ectionRuleDao.selectOneByExample(electionRuleExample);

        //获取教学班选课规则参数  拿到教学班选课需要校验的小规则
        Example example = new Example(ElectionParameter.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ruleId", rule.getId());
        List<ElectionParameter> list =
                electionParameterDao.selectByExample(example);

        //教学班Id
        Long teachClassId = courseClass.getTeachClassId();

        //教学班年级专业人数限制
        List<SuggestProfessionDto> suggestProfessionDtos =
                restrictAttrDao.selectRestrictProfession(teachClassId);

        TeachingClassElectiveRestrictAttr record =
                new TeachingClassElectiveRestrictAttr();
        record.setTeachingClassId(teachClassId);

        //其他限制条件
        TeachingClassElectiveRestrictAttr restrictAttr =
                restrictAttrDao.selectOne(record);

        //学生信息
        StudentInfoCache studentInfo = context.getStudentInfo();

        Boolean resultFlag = true;

        //限制学生
        List<String> stringList =
            restrictAttrDao.selectRestrictStudent(teachClassId);//限制学生
        if (CollectionUtil.isNotEmpty(stringList))
        {
            if (stringList.contains(studentInfo.getStudentId()))
            {
                return resultFlag;
            }
        }
        for (ElectionParameter parameter:list) {
            boolean enabled = Boolean.parseBoolean(parameter.getValue());
            if (enabled && StringUtils.equals("EDUCATION", parameter.getName())){//培养层次EDUCATION
                if (restrictAttr != null)
                {
                    String trainingLevel = restrictAttr.getTrainingLevel();//培养层次
                    if (StringUtils.isNotBlank(trainingLevel))
                    {
                        resultFlag = trainingLevel.equals(studentInfo.getTrainingLevel());
                    }
                }
                if (!resultFlag) {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil.getMsg("ruleCheck.classLimit.trainingLevel"));
                    return false;
                }
            }else if (enabled && StringUtils.equals("MAJORANDGRADE", parameter.getName())){      //学生年级专业限制项MAJOR
                if (CollectionUtil.isNotEmpty(suggestProfessionDtos)){
                    for (SuggestProfessionDto suggestProfessionDto: suggestProfessionDtos) {
                        if (StringUtils.equalsIgnoreCase(suggestProfessionDto.getProfession(),studentInfo.getMajor())
                        &&suggestProfessionDto.getGrade().intValue() == studentInfo.getGrade().intValue()){
                            resultFlag = true;
                        }else{
                            resultFlag = false;
                        }
                    }
                }
                if (!resultFlag) {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil.getMsg("ruleCheck.classLimit.majorAndgrade"));
                    return false;
                }
            }else if (enabled && StringUtils.equals("GENDER", parameter.getName())){     //学生性别限制项GENDER
                if (restrictAttr != null) {
                    //男女班
                    String isDivsex = restrictAttr.getIsDivsex();
                    String sex = String.valueOf(studentInfo.getSex());
                    Integer numberMale = restrictAttr.getNumberMale();//男生人数 1
                    Integer numberFemale = restrictAttr.getNumberFemale();//女生人数 2
                    if (isDivsex != null && isDivsex != "") {
                        ElcCourseLimitDto sexNumber =
                                takeDao.findSexNumber(teachClassId);
                        int currentNum = 0;
                        if (sexNumber == null)
                        {//当前还没有选课人数
                            currentNum = 0;
                        }
                        else
                        {
                            if (MALE.equals(sex) && sexNumber.getMaleNum() != null)
                            {
                                currentNum = sexNumber.getMaleNum();
                            }
                            if (FEMALE.equals(sex) && sexNumber.getFeMaleNum() != null)
                            {
                                currentNum = sexNumber.getFeMaleNum();
                            }
                        }

                        int limitNumber = 0;
                        if (MALE.equals(sex))
                        {//男
                            limitNumber = numberMale;
                        }
                        else
                        {
                            limitNumber = numberFemale;
                        }
                        if ((currentNum + 1) <= limitNumber)
                        {
                            resultFlag = true;
                        }
                        else
                        {
                            resultFlag = false;
                        }
                    }
                }
                if (!resultFlag) {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil.getMsg("ruleCheck.classLimit.isDivsex"));
                    return false;
                }
            }else if (enabled && StringUtils.equals("ISOVERSEAS", parameter.getName())){//是否留学限制
                if (restrictAttr != null) {
                    String isOverseas = restrictAttr.getIsOverseas();//是否留学限制
                    if (StringUtils.isNotBlank(isOverseas))
                    {
                        String s =
                                studentInfo.isAboard() ? IS_OVERSEAS_ : IS_NOT_OVERSEAS_;
                        resultFlag = isOverseas.equals(s);
                    }
                }
                if (!resultFlag) {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil.getMsg("ruleCheck.classLimit.isOverseas"));
                    return false;
                }
            }else if (enabled && StringUtils.equals("SPCIALPLAN", parameter.getName())){//专项计划限制
                if (restrictAttr != null) {
                    String spcialPlan = restrictAttr.getSpcialPlan();
                    if (StringUtils.isNotBlank(spcialPlan))
                    {
                        resultFlag = spcialPlan.equals(studentInfo.getSpcialPlan());
                    }
                }
                if (!resultFlag) {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil.getMsg("ruleCheck.classLimit.spcialPlan"));
                    return false;
                }
            }
        }

        return true;
        //是否按教学班选课TEACHCLASS
        /*List<SuggestProfessionDto> suggestProfessionDtos =
            restrictAttrDao.selectRestrictProfession(teachClassId);//限制专业
        if (CollectionUtil.isNotEmpty(suggestProfessionDtos))
        {
            Integer grade = studentInfo.getGrade();
            String major = studentInfo.getMajor();
            for (SuggestProfessionDto suggestProfessionDto : suggestProfessionDtos)
            {
                if (suggestProfessionDto.getGrade().intValue() == grade
                        .intValue()
                        && suggestProfessionDto.getProfession().equals(major))
                {
                    return true;
                }
            }
        }*/
        
        /*TeachingClassElectiveRestrictAttr record =
            new TeachingClassElectiveRestrictAttr();
        record.setTeachingClassId(teachClassId);
        TeachingClassElectiveRestrictAttr restrictAttr =
            restrictAttrDao.selectOne(record);//其他限制条件
        if (restrictAttr != null)
        {
            String isOverseas = restrictAttr.getIsOverseas();//是否留学限制
            String trainingLevel = restrictAttr.getTrainingLevel();//培养层次
            String spcialPlan = restrictAttr.getSpcialPlan();//专项计划
            String isDivsex = restrictAttr.getIsDivsex();//男女班
            Integer numberMale = restrictAttr.getNumberMale();//男生人数 1
            Integer numberFemale = restrictAttr.getNumberFemale();//女生人数 2
            boolean flag = false;
            if (StringUtils.isNotBlank(isOverseas))
            {
                String s =
                    studentInfo.isAboard() ? IS_OVERSEAS_ : IS_NOT_OVERSEAS_;
                flag = isOverseas.equals(s);
            }
            if (StringUtils.isNotBlank(trainingLevel))
            {
                flag = trainingLevel.equals(studentInfo.getTrainingLevel());
            }
            
            if (StringUtils.isNotBlank(spcialPlan))
            {
                flag = spcialPlan.equals(studentInfo.getSpcialPlan());
            }
            
            if (StringUtils.isNotBlank(isDivsex))
            {
                ElcCourseLimitDto sexNumber =
                    takeDao.findSexNumber(teachClassId);
                String sex = String.valueOf(studentInfo.getSex());//当前学生性别
                int currentNum = 0;
                if (sexNumber == null)
                {//当前还没有选课人数
                    currentNum = 0;
                }
                else
                {
                    if (MALE.equals(sex) && sexNumber.getMaleNum() != null)
                    {
                        currentNum = sexNumber.getMaleNum();
                    }
                    if (FEMALE.equals(sex) && sexNumber.getFeMaleNum() != null)
                    {
                        currentNum = sexNumber.getFeMaleNum();
                    }
                }
                
                int limitNumber = 0;
                if (MALE.equals(sex))
                {//男
                    limitNumber = numberMale;
                }
                else
                {
                    limitNumber = numberFemale;
                }
                if ((currentNum + 1) <= limitNumber)
                {
                    flag = true;
                }
                else
                {
                    flag = false;
                }
            }
            
            if (flag == true)
            {
                return flag;
            }
        }
        
        if (CollectionUtil.isEmpty(stringList)
            && CollectionUtil.isEmpty(suggestProfessionDtos)
            && restrictAttr == null)
        {
            return true;
        }
        else
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.classLimit"));
            return false;
        }*/


    }
    
}