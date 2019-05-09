package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.rules.RetakeCourse;
import com.server.edu.util.CollectionUtil;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 只允许选重修课
 * ElectableLessonOnlyRetakeFilter
 */
@Component("OnlyRetakeFilter")
public class OnlyRetakeFilter extends AbstractElecRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIRST.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        long count = RetakeCourse.isRetakeCourse(context, courseClass.getCourseCode());
        if (count>0)//重修
        {
            return true;
        }
        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
            .put(courseClass.getCourseCodeAndClassCode(),
                I18nUtil.getMsg("ruleCheck.onlyRetakeFilter"));
        return false;
    }
    
}