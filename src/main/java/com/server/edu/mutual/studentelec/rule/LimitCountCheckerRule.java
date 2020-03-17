package com.server.edu.mutual.studentelec.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.mutual.studentelec.context.ElecContextMutual;

/**
 * 本研互选--人数上限检查
 * LimitCountChecker
 */
@Component("mutualLimitCountCheckerRule")
public class LimitCountCheckerRule extends AbstractMutualElecRuleExceutor
{
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Override
    public int getOrder()
    {
        return RulePriority.SECOND.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextMutual context,
        TeachingClassCache courseClass)
    {
        Long teachClassId = courseClass.getTeachClassId();
        Integer maxNumber = courseClass.getMaxNumber();
        Integer reserveNumber = courseClass.getReserveNumber();
        Integer currentNumber = dataProvider.getElecNumber(teachClassId);
        if (maxNumber != null && currentNumber != null
            && currentNumber + reserveNumber + 1 <= maxNumber)
        {
            return true;
        }
        ElecRespose respose = context.getRespose();
        String key = courseClass.getCourseCode() + "[" + courseClass.getCourseName() + "]";
        respose.getFailedReasons()
            .put(key,I18nUtil.getMsg("ruleCheck.limitCount"));
        
        return false;
    }
}