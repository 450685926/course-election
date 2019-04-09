package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 结业生通过课程不能重修
 */
@Component("UnElectLessonByPassedFornoGraduate")
public class UnElectLessonByPassedFornoGraduate extends AbstractElecRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        if (courseClass.getTeachClassId() != null && studentInfo != null)
        {
            if (studentInfo.isGraduate())//结业生
            {
                Set<CompletedCourse> completedCourses =
                    context.getCompletedCourses();
                if (CollectionUtil.isNotEmpty(completedCourses))
                {
                    List<CompletedCourse> list = completedCourses.stream()
                        .filter(temp -> courseClass.getCourseCode()
                            .equals(temp.getCourseCode()))
                        .collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(list))
                    {
                        ElecRespose respose = context.getRespose();
                        respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                I18nUtil.getMsg(
                                    "ruleCheck.unElectLessonByPassedFornoGraduate"));
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
}