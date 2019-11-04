package com.server.edu.election.studentelec.rules.bk;

import java.util.List;

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
        Long teachClassId = courseClass.getTeachClassId();
        StudentInfoCache studentInfo = context.getStudentInfo();
        
        List<String> stringList =
            restrictAttrDao.selectRestrictStudent(teachClassId);//限制学生
        if (CollectionUtil.isNotEmpty(stringList))
        {
            if (stringList.contains(studentInfo.getStudentId()))
            {
                return true;
            }
        }
        
        List<SuggestProfessionDto> suggestProfessionDtos =
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
        }
        
        TeachingClassElectiveRestrictAttr record =
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
        }
        
    }
    
}