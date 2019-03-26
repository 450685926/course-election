package com.server.edu.election.studentelec.rules.bk;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;

/**
 * 重修违纪检查
 */
@Component("RetakeCheatedRule")
public class RetakeCheatedRule extends AbstractElecRuleExceutor
{
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        String courseCode = courseClass.getCourseCode();
        String studentId = context.getStudentInfo().getStudentId();
        ElectionRounds round =
            dataProvider.getRound(context.getRequest().getRoundId());
        Long calendarId = round.getCalendarId();//当前学期
        if (StringUtils.isNotBlank(courseCode)
            && StringUtils.isNotBlank(studentId))
        {
            StudentScore studentScore = ScoreServiceInvoker
                .findViolationStu(studentId, courseCode, calendarId);//上一学期todo
            if (studentScore != null)
            {
                
                if (StringUtils.isBlank(studentScore.getTotalMarkScore()))
                {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                        .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg(studentScore.getRemark()));
                    return false;
                }
            }
        }
        return true;
    }
    
}