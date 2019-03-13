package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 不能退教务员选的课
 * 
 */
@Component("AssignedWithdrawRule")
public class AssignedWithdrawRule extends AbstractRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        if (courseClass.getTeachClassId() != null
            && StringUtils.isNotBlank(courseClass.getCourseCode())
            && CollectionUtil.isNotEmpty(selectedCourses))
        {
            List<SelectedCourse> list = selectedCourses.stream()
                .filter(c -> ChooseObj.STU.type() != c.getChooseObj())
                .filter(
                    c -> courseClass.getCourseCode().equals(c.getCourseCode()))
                .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(list))
            {
                return true;
            }
            else
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getTeachClassId().toString(),
                        I18nUtil.getMsg("ruleCheck.assignedWithdrawRule"));
                return false;
            }
        }
        return true;
    }
    
}
