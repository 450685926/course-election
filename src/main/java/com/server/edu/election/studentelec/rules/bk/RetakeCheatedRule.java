package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;
import com.server.edu.util.CollectionUtil;

/**
 * 重修违纪检查
 * RetakeCheatedChecker
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
        boolean count = RetakeCourseUtil.isRetakeCourse(context, courseCode);
        if (count)//重修
        {
            Long roundId = context.getRequest().getRoundId();
            String id = dataProvider.getPreSemester(roundId);//上一学期
            Set<CompletedCourse> failedCourse = context.getFailedCourse();//未通过课程
            if (CollectionUtil.isNotEmpty(failedCourse))
            {
                Set<CompletedCourse> collect = failedCourse.stream()
                    .filter(vo -> Long.toString(vo.getCalendarId()).equals(id)
                        && vo.getCourseCode().equals(courseCode))
                    .collect(Collectors.toSet());
                if (CollectionUtil.isNotEmpty(collect))
                {
                    for (CompletedCourse completedCourse : collect)
                    {
                        if (completedCourse.isCheat())//作弊
                        {
                            ElecRespose respose = context.getRespose();
                            respose.getFailedReasons()
                                .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil.getMsg("ruleCheck.noCheat"));
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    
}