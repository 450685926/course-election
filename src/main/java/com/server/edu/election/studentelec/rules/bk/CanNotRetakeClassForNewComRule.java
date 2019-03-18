package com.server.edu.election.studentelec.rules.bk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 新选课程不出现重修班
 *
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
        Long id = courseClass.getTeachClassId();
        if (id != null)
        {
            if (StringUtils.isNotBlank(courseClass.getTeachClassType()))
            {
                if (Constants.REBUILD_CALSS
                    .equals(courseClass.getTeachClassType()))
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg(
                                "ruleCheck.canNotRetakeClassForNewCom"));
                    return false;
                }
            }
        }
        
        return true;
    }
    
}