package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.AddAndRemoveCourseDto;
import com.server.edu.election.dto.ElcStudentDto;
import com.server.edu.election.vo.ElcStudentVo;

public interface ElcCourseUpholdService {
    PageResult<ElcStudentVo> elcStudentInfo(PageCondition<ElcStudentDto> condition);

    PageResult<ElcStudentVo> addCourseList(PageCondition<String> condition);

    PageResult<ElcStudentVo> removedCourseList(PageCondition<String> studentId);

    Integer addCourse(AddAndRemoveCourseDto courseDto);

    Integer removedCourse(AddAndRemoveCourseDto courseDto);
}
