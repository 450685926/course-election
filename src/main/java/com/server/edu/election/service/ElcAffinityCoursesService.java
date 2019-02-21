package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcAffinityCoursesDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.ElcAffinityCoursesVo;

public interface ElcAffinityCoursesService
{
    PageInfo<ElcAffinityCoursesVo> list(
        PageCondition<ElcAffinityCoursesDto> condition);
    
    int delete(List<Long> ids);
    
    PageInfo<CourseOpen> courseList(PageCondition<CourseOpen> condition);
    
    int addCourse(List<String> ids);
    
    PageInfo<Student> studentList(PageCondition<Student> condition);
    
    PageInfo<Student> getStudents(PageCondition<Student> condition);
    
    int addStudent(ElcAffinityCoursesVo elcAffinityCoursesVo);
    
    int batchAddStudent(String courseCode);
    
    int deleteStudent(ElcAffinityCoursesVo elcAffinityCoursesVo);
    
    int batchDeleteStudent(String courseCode);
}
