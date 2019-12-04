package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;

/**
 * 人数上限检查
 * LimitCountChecker
 */
@Component("LimitCountCheckerRule")
public class LimitCountCheckerRule extends AbstractElecRuleExceutorBk
{
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Override
    public int getOrder()
    {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        Long teachClassId = courseClass.getTeachClassId();
        Integer maxNumber = courseClass.getMaxNumber();
        Integer reserveNumber = courseClass.getReserveNumber();
        Integer currentNumber = dataProvider.getElecNumber(teachClassId);
        ElecRequest request = context.getRequest();
        Long roundId = request.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        Integer chooseObj = request.getChooseObj();
        int turn = round.getTurn().intValue();
        // 判断是不是三四轮
        if (chooseObj.intValue() == Constants.ONE && (turn == Constants.THIRD_TURN || turn == Constants.FOURTH_TURN )) {
            if (maxNumber != null && currentNumber != null
                    && currentNumber + courseClass.getThirdWithdrawNumber() + reserveNumber + 1 <= maxNumber)
            {
                return true;
            }
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.limitCount"));

            return false;
        }
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