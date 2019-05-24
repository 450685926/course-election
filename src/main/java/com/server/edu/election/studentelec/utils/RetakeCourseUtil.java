package com.server.edu.election.studentelec.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.util.CollectionUtil;

/**
 * @description: 是否重修课
 * @author: bear
 * @create: 2019-05-07 11:39
 */
public class RetakeCourseUtil
{
    
    /**
     * 
     * 
     * @param context
     * @param courseCode
     * @return true是重修，false不是
     * @see [类、类#方法、类#成员]
     */
    public static boolean isRetakeCourse(ElecContext context, String courseCode)
    {
        //替代课程待做
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();
        List<CompletedCourse> list = new ArrayList<>();
        list.addAll(completedCourses);
        list.addAll(failedCourse);
        long count = 0L;
        if (CollectionUtil.isNotEmpty(list))
        {
            count = list.stream()
                .filter(vo -> vo.getCourseCode().equals(courseCode))
                .count();
        }
        return count > 0;
    }
    
}
