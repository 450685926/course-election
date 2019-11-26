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

        Integer chooseObj = request.getChooseObj();
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        String courseCode = selectedCourse.getCourse().getCourseCode();
        if (StringUtils.isNotBlank(courseCode) && chooseObj != null)
        {
            for (SelectedCourse selectedCours : selectedCourses) {
                TeachingClassCache course = selectedCours.getCourse();
                if (courseCode.equals(course.getCourseCode())) {
                    Integer obj = selectedCours.getChooseObj();
                    if (obj != null && obj.intValue() == chooseObj.intValue()) {
                        if (round.getTurn() != null && selectedCours.getTurn() != null && round.getTurn().intValue() == selectedCours.getTurn()){
                            return true;
                        }
                    }
                }
            }
        }
        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
                .put(courseCode,
                        I18nUtil.getMsg("ruleCheck.withdrawTimeCheckerRule"));
        return false;
    }
}
