package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;

/**
 * 限制不能选择重修课
 * ElectableLessonNoRetakeFilter
 */
@Component("NoRetakeRule")
public class NoRetakeRule extends AbstractElecRuleExceutorBk
{
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        boolean count = RetakeCourseUtil.isRetakeCourseBk(context,
            courseClass.getCourseCode());
        if (count)//重修
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.noRetake"));
            return false;
        }
        return true;
    }
    
}