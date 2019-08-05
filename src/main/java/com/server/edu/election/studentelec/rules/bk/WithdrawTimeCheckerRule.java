package com.server.edu.election.studentelec.rules.bk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractWithdrwRuleExceutorBk;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;

/**
 * 只能退本轮选的课
 * WithdrawTimeChecker
 */
@Component("WithdrawTimeCheckerRule")
public class WithdrawTimeCheckerRule extends AbstractWithdrwRuleExceutorBk
{
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Override
    public boolean checkRule(ElecContextBk context, SelectedCourse course)
    {
        ElecRequest request = context.getRequest();
        
        Long roundId = request.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        // 退课需要校验turn是否与本轮的turn一样
        if (!round.getTurn().equals(course.getTurn()))
        {
            String courseCodeAndClassCode =
                course.getTeachingClass().getCourseCodeAndClassCode();
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseCodeAndClassCode,
                    I18nUtil.getMsg("ruleCheck.withdrawTimeCheckerRule"));
            return false;
        }
        return true;
    }
    
}
