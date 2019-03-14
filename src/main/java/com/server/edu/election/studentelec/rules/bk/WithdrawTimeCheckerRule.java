package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.util.CollectionUtil;

/**
 * 只能退本轮选的课
 */
@Component("WithdrawTimeCheckerRule")
public class WithdrawTimeCheckerRule extends AbstractRuleExceutor {
    @Autowired
    private RoundDataProvider dataProvider;

    @Override
    public boolean checkRule(ElecContext context,
                             TeachingClassCache courseClass) {
        Long roundId = context.getRoundId();
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        String courseCode = courseClass.getCourseCode();
        if (courseClass.getTeachClassId() != null
                && CollectionUtil.isNotEmpty(selectedCourses)
                && StringUtils.isNotBlank(courseCode)) {
            ElectionRounds round = dataProvider.getRound(roundId);
            // 退课需要校验turn是否与本轮的turn一样
            List<SelectedCourse> list = selectedCourses.stream()
                    .filter(
                            c -> courseCode.equals(c.getCourseCode())
                                    && round.getTurn().equals(c.getTurn()))
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(list)) {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                        .put(courseClass.getCourseCodeAndClassCode(),
                                I18nUtil.getMsg("ruleCheck.withdrawTimeCheckerRule"));
                return false;
            }
        }
        return true;
    }

}
