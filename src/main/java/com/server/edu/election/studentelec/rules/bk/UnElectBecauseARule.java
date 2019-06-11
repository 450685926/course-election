package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 得优课程不能重修
 * UnElectLessonBecauseA
 */
@Component("UnElectBecauseARule")
public class UnElectBecauseARule extends AbstractElecRuleExceutor
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
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        if (CollectionUtil.isNotEmpty(completedCourses)
            && courseClass.getTeachClassId() != null)
        {
            List<CompletedCourse> list = completedCourses.stream()
                .filter(temp -> temp.isExcellent() == true)
                .collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(list))
            {
                //还要判断是否有替代得优的课程todo
                long count = list.stream().filter(c -> courseClass.getCourseCode().equals(c.getCourseCode())).count();
                if (count>0)
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