package com.server.edu.election.studentelec.rules.bk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 按教学班选课
 */
@Component("ElecByTeachClassRule")
public class ElecByTeachClassRule extends AbstractElecRuleExceutor
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

    private static final  String IS_OVERSEAS_ ="1";
    private static final  String IS_NOT_OVERSEAS_ ="0";
    private static final  String NOT_DISTINGYISH_SEX ="0";

    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        Long teachClassId = courseClass.getTeachClassId();
        StudentInfoCache studentInfo = context.getStudentInfo();
        
        List<String> stringList =
            restrictAttrDao.selectRestrictStudent(teachClassId);//限制学生
        List<SuggestProfessionDto> suggestProfessionDtos =
            restrictAttrDao.selectRestrictProfession(teachClassId);//限制专业
        TeachingClassElectiveRestrictAttr record =
            new TeachingClassElectiveRestrictAttr();
        record.setTeachingClassId(teachClassId);
        TeachingClassElectiveRestrictAttr restrictAttr =
            restrictAttrDao.selectOne(record);//其他限制条件
        if (CollectionUtil.isNotEmpty(stringList))
        {
            if (stringList.contains(studentInfo.getStudentId()))
            {
                return true;
            }
        }
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
        
        if (restrictAttr != null)
        {
            String isOverseas = restrictAttr.getIsOverseas();//是否留学限制
            String trainingLevel = restrictAttr.getTrainingLevel();//培养层次
            String spcialPlan = restrictAttr.getSpcialPlan();//专项计划
            String isDivsex = restrictAttr.getIsDivsex();//男女班.
            boolean flag = false;
            if (isOverseas != null)
            {//
                String s = studentInfo.isAboard() ? IS_OVERSEAS_ : IS_NOT_OVERSEAS_;
                flag = isOverseas.equals(s);
            }
            if (trainingLevel != null)
            {
                flag = trainingLevel.equals(studentInfo.getTrainingLevel());
            }
            
            if (spcialPlan != null)
            {
                flag = spcialPlan.equals(studentInfo.getSpcialPlan());
            }
            
            if (isDivsex != null && !NOT_DISTINGYISH_SEX.equals(isDivsex))
            {
                flag = isDivsex.equals(String.valueOf(studentInfo.getSex()));
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