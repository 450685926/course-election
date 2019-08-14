package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;

/**
 * 检查学生是否缴费，未缴费的不能进入选课
 * StdPayCostChecker
 */
@Component("StdPayCostCheckerRule")
public class StdPayCostCheckerRule extends AbstractElecRuleExceutorBk
{
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        if (studentInfo != null && courseClass.getTeachClassId() != null)
        {
            if (studentInfo.isPaid())
            {
                return true;
            }
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.stdPayCostChecker"));
            return false;

        }
        return true;
    }
    
}
