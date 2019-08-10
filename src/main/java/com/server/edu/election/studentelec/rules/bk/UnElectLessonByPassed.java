package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;
import com.server.edu.util.CollectionUtil;

/**
 * 考试通过的课程不能选择
 * UnElectLessonByPassed
 */
@Component("UnElectLessonByPassed")
public class UnElectLessonByPassed extends AbstractElecRuleExceutorBk
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
        /** 学生信息 */
        StudentInfoCache studentInfo = context.getStudentInfo();
        Set<ElcNoGradCouSubsVo> noGradCouSubsCourses = context.getReplaceCourses();
        /** 已完成课程 */
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        /**培养计划课程 */
        Set<PlanCourse> planCourses = context.getPlanCourses();
        /**学生信息 */
        long count = 0;
        if (courseClass.getTeachClassId() != null
            && CollectionUtil.isNotEmpty(completedCourses))
        {
            String courseCode = courseClass.getCourseCode();
            if (StringUtils.isNotBlank(courseCode))
            {
                //还要判断是否优替代的通过课程todo
                List<CompletedCourse> list = completedCourses.stream()
                    .filter(temp -> courseCode.equals(temp.getCourse().getCourseCode()))
                    .collect(Collectors.toList());
                if (studentInfo.isGraduate())
                {
                    ElcNoGradCouSubs elcNoGradCouSubs =
                        noGradCouSubsCourses.stream()
                            .filter(c -> courseCode.equals(c.getSubCourseId()))
                            .findFirst()
                            .orElse(null);
                    if (elcNoGradCouSubs != null)
                    {
                        count = completedCourses.stream()
                            .filter(c -> elcNoGradCouSubs.getOrigsCourseId()
                                .equals(c.getCourse().getCourseCode()))
                            .count();
                    }
                }
                else
                {
                    if (CollectionUtil.isNotEmpty(planCourses))
                    {
                        List<PlanCourse> subCourseCodes = planCourses.stream()
                            .filter(c -> StringUtils
                                .isNotBlank(c.getSubCourseCode()))
                            .collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(subCourseCodes))
                        {
                            List<String> subCourses = subCourseCodes.stream()
                                .filter(c -> courseCode
                                    .equals(c.getSubCourseCode()))
                                .map(UnElectLessonByPassed::courseCode)
                                .collect(Collectors.toList());
                            if (CollectionUtil.isNotEmpty(subCourses))
                            {
                                count = completedCourses.stream()
                                    .filter(c -> subCourses
                                        .contains(c.getCourse().getCourseCode()))
                                    .count();
                            }
                        }
                    }
                }
                if (CollectionUtil.isEmpty(list) || count == 0)
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
    
    static String courseCode(PlanCourse planc)
    {
        return planc.getCourse().getCourseCode();
    }
}