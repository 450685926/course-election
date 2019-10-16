package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;

/**
 * 培养计划选课<br>
 */
@Component("PlanCourseGroupCreditsRule")
public class PlanCourseGroupCreditsRule extends AbstractElecRuleExceutorBk
{
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        String courseCode = courseClass.getCourseCode();
        //培养计划课程
        Set<PlanCourse> planCourses = context.getPlanCourses();
        
        for (PlanCourse planCourse : planCourses) {
            if (StringUtils.equalsIgnoreCase(planCourse.getCourseCode(), courseCode)) {
                return true;
            }
        }
        
        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
            .put(courseClass.getCourseCodeAndClassCode(),
                I18nUtil
                    .getMsg("ruleCheck.planCultureLimit"));
        
        return false;
    }
    
/*    public boolean test() {
        String code = courseClass.getCourseCode();
        double credits = courseClass.getCredits();//当前学分
        double hasCredits = 0.0;
        //培养计划通识Label 下课程有没有校验是否公选课
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
                    hasCredits += selectedCourse.getCourse().getCredits();
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
                        .filter(vo -> vo.getCourse().getCourseCode().equals(code))
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
                                if (planCourse.getCourse().getCourseCode()
                                    .equals(selectedCourse.getCourse().getCourseCode()))
                                {
                                    hasCredits += selectedCourse.getCourse().getCredits();
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
    */
}