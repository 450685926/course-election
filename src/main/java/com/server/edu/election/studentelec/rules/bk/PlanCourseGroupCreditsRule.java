package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 培养计划课程组学分限制<br>
 * 被RetakeCourseBuildInPrepare这个ElectBuildInPrepare调用，因此不论用户页面有没有选这个规则，都会调用它的prepare
 */
@Component("PlanCourseGroupCreditsRule")
public class PlanCourseGroupCreditsRule extends AbstractElecRuleExceutor
{
    
    // protected PlanCreditLimitPrepare planCreditLimitPrepare;
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        String code = courseClass.getCourseCode();
        double credits = courseClass.getCredits();//当前学分
        double hasCredits = 0.0;
        Boolean publicElective = courseClass.isPublicElec();//是否通识选修课
        Set<CourseGroup> courseGroups = context.getCourseGroups();//获取课程组
        if (CollectionUtil.isEmpty(courseGroups))
        {
            return true;
        }
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();//已选择课程
        if (publicElective)
        {//通识选修
            Set<CourseGroup> collect = courseGroups.stream()
                .filter(vo -> "1".equals(vo.getLimitType()))
                .collect(Collectors.toSet());
            if (CollectionUtil.isEmpty(collect))
            {
                return true;
            }
            double limit = 0.0;
            for (CourseGroup courseGroup : collect)
            {
                limit += courseGroup.getCrrdits();//总学分限制
            }
            
            if (CollectionUtil.isNotEmpty(selectedCourses))
            {
                Set<SelectedCourse> electiveCourse = selectedCourses.stream()
                    .filter(
                        vo -> CourseTakeType.RETAKE.eq(vo.getCourseTakeType()))
                    .collect(Collectors.toSet());
                for (SelectedCourse selectedCourse : electiveCourse)
                {
                    hasCredits += selectedCourse.getCredits();
                }
            }
            
            if (credits + hasCredits <= limit)
            {
                return true;
            }
            else
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                        I18nUtil.getMsg("ruleCheck.planCultureLimit"));
                return false;
            }
            
        }
        else
        {//非通识选修
            if (CollectionUtil.isNotEmpty(selectedCourses))
            {
                
                List<SelectedCourse> unElectiveCourse = selectedCourses.stream()
                    .filter(
                        vo -> !CourseTakeType.RETAKE.eq(vo.getCourseTakeType()))
                    .collect(Collectors.toList());
                Set<PlanCourse> planCourses = context.getPlanCourses();
                if (CollectionUtil.isNotEmpty(planCourses))
                {
                    List<PlanCourse> collect = planCourses.stream()
                        .filter(vo -> vo.getCourseCode().equals(code))
                        .distinct()
                        .collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(collect))
                    {
                        Long label = collect.get(0).getLabel();
                        List<CourseGroup> groupList = courseGroups.stream()
                            .filter(vo -> vo.getLabel().longValue() == label
                                .longValue())
                            .collect(Collectors.toList());
                        Double crrditsLimit = groupList.get(0).getCrrdits();
                        List<PlanCourse> planCourseList = planCourses.stream()
                            .filter(vv -> vv.getLabel().longValue() == label
                                .longValue())
                            .collect(Collectors.toList());
                        for (PlanCourse planCourse : planCourseList)
                        {
                            for (SelectedCourse selectedCourse : unElectiveCourse)
                            {
                                if (planCourse.getCourseCode()
                                    .equals(selectedCourse.getCourseCode()))
                                {
                                    hasCredits += selectedCourse.getCredits();
                                }
                            }
                        }
                        if (credits + hasCredits <= crrditsLimit)
                        {
                            return true;
                        }
                        else
                        {
                            ElecRespose respose = context.getRespose();
                            respose.getFailedReasons()
                                .put(courseClass.getCourseCodeAndClassCode(),
                                    I18nUtil
                                        .getMsg("ruleCheck.planCultureLimit"));
                            return false;
                        }
                        
                    }
                }
            }
        }
        return true;
    }
    
}