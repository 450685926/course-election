package com.server.edu.election.studentelec.rules.bk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractWithdrwRuleExceutor;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;

/**
 * 只能退本轮选的课
 */
@Component("WithdrawTimeCheckerRule")
public class WithdrawTimeCheckerRule extends AbstractWithdrwRuleExceutor
{
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Override
    public boolean checkRule(ElecContext context,
        SelectedCourse courseClass)
    {
        SelectedCourse course = courseClass;
        
        Long roundId = context.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        // 退课需要校验turn是否与本轮的turn一样
        if (!round.getTurn().equals(course.getTurn()))
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.withdrawTimeCheckerRule"));
            return false;
        }
        return true;
    }
    
}
