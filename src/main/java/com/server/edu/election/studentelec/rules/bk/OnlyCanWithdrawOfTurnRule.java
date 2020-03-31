package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.AbstractWithdrwRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 只能退本轮次的课
 */
@Component("OnlyCanWithdrawOfTurnRule")
public class OnlyCanWithdrawOfTurnRule extends AbstractWithdrwRuleExceutorBk {

    @Override
    public int getOrder()
    {
        return RulePriority.SECOND.ordinal();
    }

    @Autowired
    private RoundDataProvider dataProvider;

    @Override
    public boolean checkRule(ElecContextBk context, SelectedCourse selectedCourse) {
        ElecRequest request = context.getRequest();

        Long roundId = request.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        // 退课需要校验turn是否与本轮的turn一样
        if (!round.getTurn().equals(selectedCourse.getTurn()))
        {
            TeachingClassCache co = selectedCourse.getCourse();
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(co.getTeachClassCode() + co.getCourseName(),
                            I18nUtil.getMsg("ruleCheck.withdrawTimeCheckerRule"));
            return false;
        }
        return true;
    }
}
