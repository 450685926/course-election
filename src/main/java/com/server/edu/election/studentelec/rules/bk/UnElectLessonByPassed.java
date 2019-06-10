package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.studentelec.service.cache.RoundCacheService;
import com.server.edu.util.CollectionUtil;

/**
 * 考试通过的课程不能选择
 * UnElectLessonByPassed
 */
@Component("UnElectLessonByPassed")
public class UnElectLessonByPassed extends AbstractElecRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    @Autowired
    private RoundCacheService roundCacheService;
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
    	Set<ElcNoGradCouSubs> noGradCouSubsCourses = context.getNoGradCouSubsCourses();
        /** 已完成课程 */
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        if (courseClass.getTeachClassId() != null
            && CollectionUtil.isNotEmpty(completedCourses))
        {
            if (StringUtils.isNotBlank(courseClass.getCourseCode()))
            {
                //还要判断是否优替代的通过课程todo
                List<CompletedCourse> list = completedCourses.stream()
                    .filter(temp -> courseClass.getCourseCode()
                        .equals(temp.getCourseCode()))
                    .collect(Collectors.toList());
                Long roundId = context.getRequest().getRoundId();
                Long calendarId = roundCacheService.getRound(roundId).getCalendarId();
                ElcNoGradCouSubs  elcNoGradCouSubs=  noGradCouSubsCourses.stream()
                		.filter(c->calendarId.equals(c.getCalendarId()))
                		.filter(c->courseClass.getCourseCode().equals(c.getSubCourseId()))
                		.findFirst().orElse(new ElcNoGradCouSubs());
                long count = 0;
                if(elcNoGradCouSubs!=null) {
                    count = completedCourses.stream().filter(c->elcNoGradCouSubs.getOrigsCourseId().equals(c.getCourseCode())).count();
                }
                if (CollectionUtil.isEmpty(list)||count ==0)
                {
                    return true;
                }
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.unElectLessonByPassed"));
                return false;
            }
        }
        
        return true;
    }
    
}