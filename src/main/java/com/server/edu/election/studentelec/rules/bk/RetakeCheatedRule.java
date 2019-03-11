package com.server.edu.election.studentelec.rules.bk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 重修违纪检查
 *
 */
@Component("RetakeCheatedRule")
public class RetakeCheatedRule extends AbstractRuleExceutor
{
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        String courseCode = courseClass.getCourseCode();
        String studentId = context.getStudentInfo().getStudentId();
        if (StringUtils.isNotBlank(courseCode)
            && StringUtils.isNotBlank(studentId))
        {
            StudentScore studentScore =
                ScoreServiceInvoker.findViolationStu(studentId, courseCode);
            if (studentScore != null)
            {
                if (studentScore.getTotalMarkScore() != null)
                {
                    return true;
                }
                else
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getTeachClassId().toString(),
                            I18nUtil.getMsg(studentScore.getRemark()));
                }
            }
            else
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getTeachClassId().toString(),
                        I18nUtil.getMsg("ruleCheck.scoreNotExist"));
            }
        }
        return false;
    }
    
}