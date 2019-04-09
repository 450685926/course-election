package com.server.edu.election.studentelec.rules.bk;

import java.util.HashSet;
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
 * 新选课程不出现重修班
 */
@Component("CanNotRetakeClassForNewComRule")
public class CanNotRetakeClassForNewComRule extends AbstractElecRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FOURTH.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        Set<CompletedCourse> set = new HashSet<>();
        set.addAll(context.getFailedCourse());
        set.addAll(context.getCompletedCourses());
        if (CollectionUtil.isNotEmpty(set))
        {
            Set<CompletedCourse> collect = set.stream()
                .filter(vo -> vo.getCourseCode()
                    .equals(courseClass.getCourseCode()))
                .collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(collect))
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil
                            .getMsg("ruleCheck.canNotRetakeClassForNewCom"));
                return false;
            }
        }
        
        return true;
    }
    
}