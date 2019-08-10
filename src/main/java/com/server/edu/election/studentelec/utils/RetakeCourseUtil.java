package com.server.edu.election.studentelec.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;
import com.server.edu.util.CollectionUtil;

/**
 * @description: 是否重修课
 * @author: bear
 * @create: 2019-05-07 11:39
 */
public class RetakeCourseUtil {

	/**
	 * 
	 * 
	 * @param context
	 * @param courseCode
	 * @return true是重修，false不是
	 * @see [类、类#方法、类#成员]
	 */
	public static boolean isRetakeCourse(ElecContext context, String courseCode) {
		// 替代课程待做
		Set<CompletedCourse> completedCourses = context.getCompletedCourses();
		Set<CompletedCourse> failedCourse = context.getFailedCourse();
		/** 培养计划课程 */
		Set<PlanCourse> planCourses = context.getPlanCourses();
		List<CompletedCourse> list = new ArrayList<>();
		list.addAll(completedCourses);
		list.addAll(failedCourse);
		long count = 0L;
		if (CollectionUtil.isNotEmpty(list)) {
				if (CollectionUtil.isNotEmpty(planCourses)) {
					List<PlanCourse> subCourseCodes = planCourses.stream()
							.filter(c -> StringUtils.isNotBlank(c.getSubCourseCode())).collect(Collectors.toList());
					if (CollectionUtil.isNotEmpty(subCourseCodes)) {
						List<String> subCourses = subCourseCodes.stream()
								.filter(c -> courseCode.equals(c.getSubCourseCode())).map(PlanCourse::getCourseCode)
								.collect(Collectors.toList());
						if (CollectionUtil.isNotEmpty(subCourses)) {
							count = list.stream().filter(vo -> vo.getCourseCode().equals(courseCode))
									.filter(vo -> subCourses.contains(vo.getCourseCode())).count();
						}
					}
				}
		}

		return count > 0;
	}
	
	/**
     * 
     * 
     * @param context
     * @param courseCode
     * @return true是重修，false不是
     * @see [类、类#方法、类#成员]
     */
    public static boolean isRetakeCourseBk(ElecContextBk context, String courseCode) {
        // 替代课程待做
        Set<com.server.edu.election.studentelec.context.bk.CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<com.server.edu.election.studentelec.context.bk.CompletedCourse> failedCourse = context.getFailedCourse();
        List<com.server.edu.election.studentelec.context.bk.CompletedCourse> list = new ArrayList<>();
        list.addAll(completedCourses);
        list.addAll(failedCourse);
        
        Set<ElcNoGradCouSubsVo> subsCourses = context.getReplaceCourses();
        ElcNoGradCouSubs elcNoGradCouSubs = subsCourses.stream()
                .filter(c -> courseCode.equals(c.getSubCourseId())).findFirst().orElse(null);
        long count = 0L;
        if (CollectionUtil.isNotEmpty(list)) {
            if (elcNoGradCouSubs != null) {
                count = list.stream()
                        .filter(vo -> vo.getCourseCode().equals(courseCode))
                        .filter(c -> elcNoGradCouSubs.getOrigsCourseId().equals(c.getCourseCode())).count();
            }
        }

        return count > 0;
    }

}
