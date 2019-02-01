package com.server.edu.election.service.rule.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.server.edu.election.dto.LessonDto;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

/**
 * 学生成绩准备
 */
@Component
public class CourseGradePrepare extends AbstractElectRuleExecutor
{
    @Override
    public void prepare(ElectState state)
    {
        List<LessonDto> courseGradePassedMap = new ArrayList<>();
        
        // select course_id, passed from course_grade where student_id_ = ? and status = 'PUBLISHED'
        // 查询学生课程成绩
        List<Map<String, Long>> results = new ArrayList<>();//TODO
        for (Map<String, Long> map : results)
        {
            Long courseId = map.get("courseId");
            Long passed = map.get("passed");
            if (passed == 1)
            {
                LessonDto lesson = new LessonDto();
                lesson.setCourseId(courseId);
                lesson.setHisCoursePassed(true);
                lesson.setHisFlag(true);
                
                courseGradePassedMap.add(lesson);
            }
        }
        state.getElectedCourses().addAll(courseGradePassedMap);
    }
    
}