package com.server.edu.mutual.studentelec.rule;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;

/**
 * filter，过滤掉学生不能选的任务（校区不对），使得学生在界面上看不见
 *CampusFilter
 */
@Component("mutualCampusRule")
public class CampusRule extends AbstractMutualElecRuleExceutor   
{
    @Override
    public int getOrder()
    {
        return RulePriority.SECOND.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextMutualBk context,
        TeachingClassCache courseClass)
    {
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
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.campus"));
            return false;
        }
    }
    
}