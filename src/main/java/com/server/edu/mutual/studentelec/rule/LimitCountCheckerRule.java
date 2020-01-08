package com.server.edu.mutual.studentelec.rule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;

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
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean checkRule(ElecContextMutualBk context,
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
        respose.getFailedReasons()
            .put(courseClass.getCourseCodeAndClassCode(),
                I18nUtil.getMsg("ruleCheck.limitCount"));
        
        return false;
    }
}