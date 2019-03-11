package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 结业生通过课程不能重修
 */
@Component("UnElectLessonByPassedFornoGraduate")
public class UnElectLessonByPassedFornoGraduate extends AbstractRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        if (courseClass.getTeachClassId() != null && studentInfo != null)
        {
            if (studentInfo.isGraduate())
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getTeachClassId().toString(),
                        I18nUtil.getMsg(
                            "ruleCheck.unElectLessonByPassedFornoGraduate"));
                return false;
            }
        }
        return true;
    }
    
}