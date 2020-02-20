package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.vo.ElcCouSubsVo;
import com.server.edu.util.CollectionUtil;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 只允许选重修课
 * ElectableLessonOnlyRetakeFilter
 */
@Component("OnlyRetakeFilter")
public class OnlyRetakeFilter extends AbstractElecRuleExceutorBk
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIRST.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
//        boolean count = RetakeCourseUtil.isRetakeCourseBk(context,
//            courseClass.getCourseCode());
//        if (count)//重修
//        {
//            return true;
//        }

        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();
        Set<String> pass = completedCourses.stream().
                map(s -> s.getCourse().getCourseCode()).collect(Collectors.toSet());
        Set<String> fail = failedCourse.stream().
                map(s -> s.getCourse().getCourseCode()).collect(Collectors.toSet());
        String courseCode = courseClass.getCourseCode();
        Set<String> set = new HashSet<>();
        set.addAll(pass);
        set.addAll(fail);
        // 替代课程未考虑，以后再说
        if (set.contains(courseCode)) {
            return true;
        }

        Set<ElcCouSubsVo> replaceCourses = context.getReplaceCourses();
        if(CollectionUtil.isNotEmpty(replaceCourses)){
            List<String> collect = replaceCourses.stream().filter(vo -> vo.getSubCourseCode().equals(courseCode)).
                    map(ElcCouSubsVo::getOrigsCourseCode).collect(Collectors.toList());
            if(CollectionUtil.isNotEmpty(collect)){
                for (String s : collect) {
                    if(set.contains(s)){
                        return true;
                    }
                }
            }
        }

        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
            .put(courseClass.getTeachClassCode() + courseClass.getCourseName(),
                I18nUtil.getMsg("ruleCheck.onlyRetakeFilter"));
        return false;
    }
    
}