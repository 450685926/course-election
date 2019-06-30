package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;

/**
 * 新选课程不出现重修班
 * 课程是新选 教学班不能是重修班
 * CanNotRetakeClassForNewComFilter
 *
 * !elected&&lesson.getTags().size()>0
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
        boolean count = RetakeCourseUtil.isRetakeCourse(context,
            courseClass.getCourseCode());
        
        if (!count
            && courseClass.getTeachClassType().equals(Constants.REBUILD_CALSS))
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.canNotRetakeClassForNewCom"));
            return false;
        }
        
        return true;
    }
    
}