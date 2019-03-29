package com.server.edu.election.studentelec.rules.bk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;

/**
 * 限制不能选择重修课
 */
@Component("NoRetakeRule")
public class NoRetakeRule extends AbstractElecRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        if (StringUtils.isNotBlank(courseClass.getTeachClassType()))
        {
            if (Constants.ORDINARY_CALSS
                .equals(courseClass.getTeachClassType()))
            {
                return true;
            }
            
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.noRetake"));
            return false;
        }
        return true;
    }
    
}