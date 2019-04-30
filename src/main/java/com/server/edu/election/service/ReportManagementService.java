package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;

import java.util.List;

public interface ReportManagementService {
    /**点名册*/
    PageResult<RollBookList> findRollBookList2(PageCondition<ReportManagementCondition> condition);

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
    StudentSchoolTimetabVo findTeacherTimetable(Long calendarId,String teacherCode);

    /**
     * 查询选课日志
     * */
    PageResult<ElcLogVo> findCourseLog(
            PageCondition<ElcLogVo> condition);

    /**查询选课名单*/
    PageResult<StudentSelectCourseList> findElectCourseList(PageCondition<ReportManagementCondition> condition);

    /**未选课原因*/
    String addNoSelectReason(ElcNoSelectReason noSelectReason);

    /**查找未选课原因*/
    ElcNoSelectReason findNoSelectReason(Long calendarId, String studentCode);


    /**代选课*/
    String otherSelectCourse(StudentSelectCourseList studentSelectCourseList);

    /**导出未选课学生名单*/
    String exportStudentNoCourseList(ReportManagementCondition condition) throws Exception;

    /**导出点名册*/
    String exportRollBookList(ReportManagementCondition condition) throws Exception;

    PageResult<RollBookList> findRollBookList(PageCondition<RollBookConditionDto> condition);

    PreViewRollDto findPreviewRollBookListById(Long teachingClassId,Long calendarId);

    List<StudnetTimeTable> findStudentTimetab(Long calendarId, String studentCode);
}
