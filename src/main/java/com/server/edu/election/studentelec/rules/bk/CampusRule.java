package com.server.edu.election.studentelec.rules.bk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * filter，过滤掉学生不能选的任务（校区不对），使得学生在界面上看不见
 *
 */
@Component("CampusRule")
public class CampusRule extends AbstractRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.SECOND.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        if (StringUtils.isBlank(courseClass.getCampus()))
        {
            return true;
        }
        else
        {
            if (courseClass.getCampus().equals(studentInfo.getCampus()))
            {
                return true;
            }
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.campus"));
            return false;
        }
    }
    
}