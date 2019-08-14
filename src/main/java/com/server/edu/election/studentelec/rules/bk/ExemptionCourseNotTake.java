package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.util.CollectionUtil;

/**
 * 免修申请期间不能选课
 * 
 */
@Component("ExemptionCourseNotTake")
public class ExemptionCourseNotTake extends AbstractElecRuleExceutorBk
{
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        // 免修申请课程
        Set<ElecCourse> applyForDropCourses = context.getApplyForDropCourses();
        String courseCode = courseClass.getCourseCode();
        if (StringUtils.isNotBlank(courseCode)
            && CollectionUtil.isNotEmpty(applyForDropCourses))
        {
            List<ElecCourse> list = applyForDropCourses.stream()
                .filter(
                    elecCourse -> courseCode.equals(elecCourse.getCourseCode()))
                .collect(Collectors.toList());
            
            if (CollectionUtil.isEmpty(list))
            {
                return true;
            }
            else
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.exemptionCourseNotTake"));
                return false;
            }
        }
        return true;
    }
    
}
