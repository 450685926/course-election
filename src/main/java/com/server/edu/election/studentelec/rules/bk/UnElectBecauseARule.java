package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 得优课程不能重修
 * UnElectLessonBecauseA
 */
@Component("UnElectBecauseARule")
public class UnElectBecauseARule extends AbstractElecRuleExceutorBk
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        if (CollectionUtil.isNotEmpty(completedCourses)
            && courseClass.getTeachClassId() != null)
        {
            List<CompletedCourse> list = completedCourses.stream()
                .filter(temp -> temp.isExcellent() == true)
                .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(list))
            {
                String courseCode = courseClass.getCourseCode();
                //还要判断是否有替代得优的课程todo
                long count = list.stream()
                    .filter(c -> courseCode.equals(c.getTeachingClass().getCourseCode()))
                    .count();
                if (count > 0)
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.unElectBecauseA"));
                    return false;
                }
            }
        }
        return true;
    }
    
}