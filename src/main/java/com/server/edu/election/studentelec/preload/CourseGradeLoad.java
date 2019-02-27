package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;

/**
 * 查询学生有成绩的课程
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月25日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class CourseGradeLoad extends DataProLoad
{
    @Override
    public int getOrder()
    {
        return 1;
    }

    @Override
    public void load(ElecContext context)
    {
     // select course_id, passed from course_grade where student_id_ = ? and status = 'PUBLISHED'
        // 1. 查询学生课程成绩
        List<Map<String, Long>> results = new ArrayList<>();//TODO
        List<CompletedCourse> completedCourses = context.getCompletedCourses();
        for (Map<String, Long> map : results)
        {
            Long courseId = map.get("courseCode");
            Long passed = map.get("passed");
            if (passed == 1)
            {
                CompletedCourse lesson = new CompletedCourse();
                lesson.setCourseCode("");
                lesson.setCourseName("");
                completedCourses.add(lesson);
            }
        }
        // 2. 非本学期的选课并且没有成功的
    }
    
}
