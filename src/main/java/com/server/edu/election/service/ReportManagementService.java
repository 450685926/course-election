package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.RollBookList;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;

import java.util.List;

public interface ReportManagementService {
    /**点名册*/
    PageResult<RollBookList> findRollBookList(PageCondition<ReportManagementCondition> condition);

    /**预览点名册 */
    PreviewRollBookList findPreviewRollBookList(RollBookList bookList);

    /**查询学生课表*/
    StudentSchoolTimetabVo findSchoolTimetab(Long calendarId, String studentCode);

    /**查询所有学生课表*/
    PageResult<StudentVo> findAllSchoolTimetab(PageCondition<ReportManagementCondition> condition);

    /**查询课表对应教师详细信息*/
    List<ClassTeacherDto> findStudentAndTeacherTime( Long teachingClassId);

    /**查询教学班对应老师信息*/
    PageResult<ClassCodeToTeacher> findAllClassTeacher(PageCondition<ClassCodeToTeacher> condition);

    /**查询教师课表*/
    List<ClassTeacherDto> findTeacherTimetable(Long calendarId,String teacherCode);

    /**
     * 查询选课日志
     * */
    PageResult<ElcLogVo> findCourseLog(
            PageCondition<ElcLogVo> condition);
}
