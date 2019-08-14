package com.server.edu.election.studentelec.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.vo.ElcCouSubsVo;
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
	    List<CompletedCourse> list = new ArrayList<>();
	    list.addAll(context.getCompletedCourses());
	    list.addAll(context.getFailedCourse());
		/** 培养计划课程 */
		Set<PlanCourse> planCourses = context.getPlanCourses();
		long count = 0L;
		if (CollectionUtil.isNotEmpty(list)) {
		    // 判断课程是否是已经考过试的成绩
		    count = list.stream().filter(vo -> courseCode.equals(vo.getCourseCode())).count();
			if (count == 0 && CollectionUtil.isNotEmpty(planCourses)) {
			    // 判断是否为替代课程并且考过试
				List<PlanCourse> subCourseCodes = planCourses.stream()
						.filter(c -> StringUtils.isNotBlank(c.getSubCourseCode()))
						.collect(Collectors.toList());
				if (CollectionUtil.isNotEmpty(subCourseCodes)) {
					List<String> subCourses = subCourseCodes.stream()
							.filter(c -> courseCode.equals(c.getSubCourseCode()))
							.map(PlanCourse::getCourseCode)
							.collect(Collectors.toList());
					if (CollectionUtil.isNotEmpty(subCourses)) {
						count = list.stream()
								.filter(vo -> subCourses.contains(vo.getCourseCode()))
								.count();
					}
				}
			}
		}

		return count > 0;
	}
	
	/**
     * 判断本科生选课课程是否是重修
     * 
     * @param context
     * @param courseCode
     * @return true是重修，false不是
     */
    public static boolean isRetakeCourseBk(ElecContextBk context, String courseCode) {
        List<com.server.edu.election.studentelec.context.bk.CompletedCourse> list = new ArrayList<>();
        list.addAll(context.getCompletedCourses());
        list.addAll(context.getFailedCourse());
        
        long count = 0L;
        if (CollectionUtil.isNotEmpty(list)) {
            // 判断课程是否是已经考过试的成绩
            count = list.stream().filter(vo -> courseCode.equals(vo.getCourseCode())).count();
            if (count == 0) {
                // 判断是否为替代课程
                Set<ElcCouSubsVo> subsCourses = context.getReplaceCourses();
                ElcCouSubsVo couSub = subsCourses.stream()
                        .filter(c -> courseCode.equals(c.getSubCourseCode()))
                        .findFirst()
                        .orElse(null);
                if(couSub != null) {
                    count = list.stream()
                        .filter(c -> c.getCourseCode().equals(couSub.getOrigsCourseCode()))
                        .count();
                }
            }
        }

        return count > 0;
    }

}
