package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.util.CollectionUtil;
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

import java.util.Set;
import java.util.stream.Collectors;

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
           /* StudentScore studentScore = ScoreServiceInvoker
                .findViolationStu(studentId, courseCode, calendarId);*///需要上一学期calendarId todo
            Set<CompletedCourse> failedCourse = context.getFailedCourse();//未通过课程
            if(CollectionUtil.isNotEmpty(failedCourse)){
                Set<CompletedCourse> collect = failedCourse.stream().filter(vo -> vo.getCalendarId().longValue() == calendarId.longValue() && vo.getCourseCode().equals(courseCode)).collect(Collectors.toSet());
                if(CollectionUtil.isNotEmpty(collect)){
                    for (CompletedCourse completedCourse : collect) {
                        if (completedCourse.isCheat())//作弊
                        {
                            ElecRespose respose = context.getRespose();
                            respose.getFailedReasons()
                                    .put(courseClass.getCourseCodeAndClassCode(),
                                            I18nUtil.getMsg("ruleCheck.noRetake"));
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
}