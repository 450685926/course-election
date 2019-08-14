package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractWithdrwRuleExceutorBk;
import com.server.edu.util.CollectionUtil;

/**
 * 不能退教务员选的课，只要不是自选的都不能退课
 * AssignedWithdrawPrepare
 */
@Component("AssignedWithdrawRule")
public class AssignedWithdrawRule extends AbstractWithdrwRuleExceutorBk
{
    
    @Override
    public boolean checkRule(ElecContextBk context, SelectedCourse course)
    {
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        TeachingClassCache tClass = course.getCourse();
        String courseCode = tClass.getCourseCode();
        if (tClass.getTeachClassId() != null
            && StringUtils.isNotBlank(courseCode)
            && CollectionUtil.isNotEmpty(selectedCourses))
        {
            List<SelectedCourse> list = selectedCourses.stream()
                .filter(c -> ChooseObj.STU.type() != c.getChooseObj())
                .filter(c -> courseCode.equals(c.getCourse().getCourseCode()))
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(list))
            {
                return true;
            }
            else
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(tClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.assignedWithdrawRule"));
                return false;
            }
        }
        return true;
    }
    
}
