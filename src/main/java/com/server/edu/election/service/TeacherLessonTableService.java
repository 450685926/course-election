package com.server.edu.election.service;

import java.io.IOException;
import java.util.List;

import com.itextpdf.text.DocumentException;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ClassCodeToTeacher;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.TeacherTimeTable;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.util.excel.export.ExcelResult;

/**
 * 教师课表
 */
public interface TeacherLessonTableService
{
    PageResult<ClassCodeToTeacher> findAllTeacherTimeTable(PageCondition<ClassCodeToTeacher> condition);

    PageResult<ClassCodeToTeacher> findTeacherTimeTableByRole(PageCondition<ClassCodeToTeacher> condition);

    /**查询教师课表*/
    StudentSchoolTimetabVo findTeacherTimetable2(Long calendarId,String teacherCode);

    List<TeacherTimeTable> findTeacherTimetable(Long calendarId, String teacherCode);

    List<TimeTable> getTeacherTimetable(Long calendarId, String teacherCode, Integer week);

    ExcelResult exportTeacher(ClassCodeToTeacher condition);
    
    /**
     * 导出教师课表pdf--研究生
     * @param calendarId
     * @param teacherCode
     * @return
     */
    RestResult<String> exportTeacherTimetabPdf(Long calendarId,  String teacherCode) throws DocumentException, IOException;
    
    /**查询课表对应教师详细信息*/
    List<ClassTeacherDto> findStudentAndTeacherTime( Long teachingClassId);

    /**查询教学班对应老师信息*/
    PageResult<ClassCodeToTeacher> findAllClassTeacher(PageCondition<ClassCodeToTeacher> condition);
}
