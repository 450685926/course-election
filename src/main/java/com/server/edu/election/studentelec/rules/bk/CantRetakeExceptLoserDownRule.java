package com.server.edu.election.studentelec.rules.bk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;

/**
 * 不能重修，但是留降级学生除外
 */
@Component("CantRetakeExceptLoserDownRule")
public class CantRetakeExceptLoserDownRule extends AbstractElecRuleExceutor {
    @Override
    public boolean checkRule(ElecContext context,
                             TeachingClassCache courseClass) {
        StudentInfoCache studentInfo = context.getStudentInfo();

        if (StringUtils.isNotBlank(courseClass.getTeachClassType())) {
            if (Constants.REBUILD_CALSS
                    .equals(courseClass.getTeachClassType())) {
                if (studentInfo.isRepeater()) {
                    return true;
                }

                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                        .put(courseClass.getCourseCodeAndClassCode(),
                                I18nUtil.getMsg(
                                        "ruleCheck.cantRetakeExceptLoserDown"));
                return false;

            }
        }

        return true;
    }

}