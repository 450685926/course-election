package com.server.edu.election.studentelec.rules.bk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 限制不能选择重修课
 *
 */
@Component("NoRetakeRule")
public class NoRetakeRule extends AbstractRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        if (courseClass.getTeachClassId() != null)
        {
            if (StringUtils.isNotBlank(courseClass.getTeachClassType()))
            {
                if (Constants.ORDINARY_CALSS
                    .equals(courseClass.getTeachClassType()))
                {
                    return true;
                }
                else
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getTeachClassId().toString(),
                            I18nUtil.getMsg("ruleCheck.noRetake"));
                }
            }
        }
        return false;
    }
    
}