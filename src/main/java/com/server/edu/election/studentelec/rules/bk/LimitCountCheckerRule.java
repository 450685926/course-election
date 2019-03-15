package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 人数上限检查
 */
@Component("LimitCountCheckerRule")
public class LimitCountCheckerRule extends AbstractRuleExceutor {

    public LimitCountCheckerRule() {
        super();
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean checkRule(ElecContext context,
                             TeachingClassCache courseClass) {

        Integer maxNumber = courseClass.getMaxNumber();
        Integer currentNumber = courseClass.getCurrentNumber();
        if (maxNumber != null && currentNumber != null
                && currentNumber+1 <= maxNumber) {
            return true;
        }
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.limitCount"));

        return false;
    }
}