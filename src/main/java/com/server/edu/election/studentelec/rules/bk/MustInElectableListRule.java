package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.ElecContextLogin;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.AbstractLoginRuleExceutorBk;
import com.server.edu.election.studentelec.service.cache.RoundCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * filter，必须在可选名单才能选课
 *MustInElectableListRule
 */
@Component("MustInElectableListRule")
public class MustInElectableListRule extends AbstractLoginRuleExceutorBk {
    @Autowired
    private RoundCacheService roundCacheService;

    @Override
    public boolean checkRule(ElecContextLogin context, TeachingClassCache courseClass) {
        String studentId = context.getRequest().getStudentId();
        Long roundId = context.getRequest().getRoundId();
        boolean containsStu = roundCacheService.containsStu(roundId, studentId);
        if (containsStu) {
            return true;
        }
        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
                .put("03",
                        I18nUtil.getMsg("ruleCheck.isNotElcList"));
        return false;
    }

}
