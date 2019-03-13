package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 考试通过的课程不能选择
 */
@Component("UnElectLessonByPassed")
public class UnElectLessonByPassed extends AbstractRuleExceutor
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
        /** 已完成课程 */
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        if (courseClass.getTeachClassId() != null
            && CollectionUtil.isNotEmpty(completedCourses))
        {
            if (StringUtils.isNotBlank(courseClass.getCourseCode()))
            {
                List<CompletedCourse> list = completedCourses.stream()
                    .filter(temp -> courseClass.getCourseCode()
                        .equals(temp.getCourseCode()))
                    .collect(Collectors.toList());
                if (CollectionUtil.isEmpty(list))
                {
                    return true;
                }
                else
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getTeachClassId().toString(),
                            I18nUtil.getMsg("ruleCheck.unElectLessonByPassed"));
                    return false;
                }
            }
        }
        
        return true;
    }
    
}