package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 只能退本轮次的课
 */
@Component("OnlyCanWithdrawOfTurnRule")
public class OnlyCanWithdrawOfTurnRule extends AbstractElecRuleExceutorBk {


    @Override
    public boolean checkRule(ElecContextBk context, TeachingClassCache courseClass) {
        ElecRequest request = context.getRequest();
        Integer chooseObj = request.getChooseObj();
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        String courseCode = courseClass.getCourseCode();
        if (StringUtils.isNotBlank(courseCode) && chooseObj != null)
        {
            for (SelectedCourse selectedCours : selectedCourses) {
                TeachingClassCache course = selectedCours.getCourse();
                if (courseCode.equals(course.getCourseCode())) {
                    Integer obj = selectedCours.getChooseObj();
                    if (obj != null && obj.intValue() == chooseObj.intValue()) {
                        return true;
                    }
                }
            }
        }
        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.withdrawTimeCheckerRule"));
        return false;
    }
}
