package com.server.edu.election.studentelec.rules.bk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 如果有课程开启了“建议课表”选课，并且该选课批次也开启了“建议课表”选课。<br>
 * 那么学生看到的这些课的任务都是适合自己的（逻辑类似于根据教学班过滤任务）
 */
@Component("SuggestCourseRule")
public class SuggestCourseRule extends AbstractRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FOURTH.ordinal();
    }
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        Long TeachClassId = courseClass.getTeachClassId();
        StudentInfoCache studentInfo = context.getStudentInfo();
        String studentId = studentInfo.getStudentId();
        Integer grade = studentInfo.getGrade();
        String major = studentInfo.getMajor();
        if (TeachClassId != null)
        {//查询建议学生
            List<String> teachingStudent =
                classDao.selectSuggestStudent(TeachClassId);
            if (CollectionUtil.isNotEmpty(teachingStudent))
            {//有配课学生建议
                if (teachingStudent.contains(studentId))
                {
                    return true;
                }
                else
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getTeachClassId().toString(),
                            I18nUtil.getMsg("ruleCheck.suggestCourse"));
                    return false;
                }
                
            }
            List<SuggestProfessionDto> professionDtos =
                classDao.selectSuggestProfession(TeachClassId);
            if (CollectionUtil.isNotEmpty(professionDtos))
            {
                SuggestProfessionDto professionDto = professionDtos.stream()
                    .filter(profession -> (profession.getGrade()
                        .intValue() == grade.intValue()
                        && major.equals(profession.getProfession())))
                    .findAny()
                    .orElse(null);
                if (professionDto != null)
                {
                    return true;
                }
                else
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getTeachClassId().toString(),
                            I18nUtil.getMsg("ruleCheck.suggestCourse"));
                    return false;
                }
            }
            
            return true;
            
        }
        
        return false;
    }
    
}
