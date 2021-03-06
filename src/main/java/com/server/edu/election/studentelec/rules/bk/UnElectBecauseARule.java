package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.election.vo.ElcCouSubsVo;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.util.CollectionUtil;

/**
 * 得优课程不能重修
 * UnElectLessonBecauseA
 */
@Component("UnElectBecauseARule")
public class UnElectBecauseARule extends AbstractElecRuleExceutorBk
{
    @Override
    public int getOrder()
    {
        return RulePriority.FIFTH.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        if (CollectionUtil.isNotEmpty(completedCourses)
            && courseClass.getTeachClassId() != null)
        {
//            List<CompletedCourse> list = completedCourses.stream()
//                .filter(temp -> temp.isExcellent() == true)
//                .collect(Collectors.toList());
//            if (CollectionUtil.isNotEmpty(list))
//            {
//                String courseCode = courseClass.getCourseCode();
//                //还要判断是否有替代得优的课程todo
//                long count = list.stream()
//                    .filter(c -> courseCode.equals(c.getCourse().getCourseCode()))
//                    .count();
//                if (count > 0)
//                {
//                    ElecRespose respose = context.getRespose();
//                    respose.getFailedReasons()
//                        .put(courseClass.getCourseCodeAndClassCode(),
//                            I18nUtil.getMsg("ruleCheck.unElectBecauseA"));
//                    return false;
//                }
//            }
            String courseCode = courseClass.getCourseCode();
            //判断要选的课程是否是替代课程
            Set<ElcCouSubsVo> noGradCouSubsCourses = context.getReplaceCourses();
            ElcCouSubsVo elcCouSubs = null;
            if (CollectionUtil.isNotEmpty(noGradCouSubsCourses)) {
                elcCouSubs =
                        noGradCouSubsCourses.stream()
                                .filter(c -> courseCode.equals(c.getSubCourseCode()))
                                .findFirst()
                                .orElse(null);

            }
            // 如果是替代课程，判断被替代的课程是否得优
            if (elcCouSubs != null) {
                // 获取被替代的课程code
                String origsCourseCode = elcCouSubs.getOrigsCourseCode();
                // 判断被替代的课程是否得优
                List<CompletedCourse> list = completedCourses.stream()
                        .filter(temp -> origsCourseCode.equals(temp.getCourse().getCourseCode()) && temp.isExcellent() == true)
                        .collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(list)) {
                    ElecRespose respose = context.getRespose();
                    respose.getFailedReasons()
                            .put(courseClass.getTeachClassCode() + courseClass.getCourseName(),
                                    I18nUtil.getMsg("ruleCheck.unElectBecauseA"));
                    return false;
                }
            }
            //判断要选的课程是否得优
            List<CompletedCourse> list = completedCourses.stream()
                    .filter(temp -> temp.getCourse().getCourseCode().equals(courseCode) && temp.isExcellent() == true)
                    .collect(Collectors.toList());

            if (CollectionUtil.isNotEmpty(list))
            {
                ElecRespose respose = context.getRespose();
                respose.getFailedReasons()
                        .put(courseClass.getTeachClassCode() + courseClass.getCourseName(),
                                I18nUtil.getMsg("ruleCheck.unElectBecauseA"));
                return false;
            }
        }
        return true;
    }
    
}