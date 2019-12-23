package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component("CampusRuleButCanSeeRule")
public class TransCampusRule extends AbstractElecRuleExceutorBk {

    @Override
    public int getOrder()
    {
        return RulePriority.SECOND.ordinal();
    }

    @Override
    public boolean checkRule(ElecContextBk context, TeachingClassCache courseClass) {
        StudentInfoCache studentInfo = context.getStudentInfo();
        String campus = courseClass.getCampus();
        if (StringUtils.isBlank(campus))
        {
            return true;
        }
        else
        {
            if (campus.equals(studentInfo.getCampus()))
            {
                return true;
            }
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getTeachClassCode() + courseClass.getCourseName(),
                            I18nUtil.getMsg("ruleCheck.campus"));
            return false;
        }
    }
}
