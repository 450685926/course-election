package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ClassCodeToTeacher;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.ExportPreCondition;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.dto.PreViewRollDto;
import com.server.edu.election.dto.PreviewRollBookList;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.RollBookConditionDto;
import com.server.edu.election.dto.StudentSelectCourseList;
import com.server.edu.election.dto.StudnetTimeTable;
import com.server.edu.election.dto.TeacherTimeTable;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.util.excel.export.ExcelResult;

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
    StudentSchoolTimetabVo findTeacherTimetable2(Long calendarId,String teacherCode);

    /**查询选课名单*/
    PageResult<NoSelectCourseStdsDto> findElectCourseList(PageCondition<NoSelectCourseStdsDto> condition);

    /**未选课原因*/
    String addNoSelectReason(ElcNoSelectReasonVo noSelectReason);

    /**查找未选课原因*/
    ElcNoSelectReason findNoSelectReason(Long calendarId, String studentCode);


    /**代选课*/
    String otherSelectCourse(StudentSelectCourseList studentSelectCourseList);

    /**本科生导出未选课学生名单*/
    String exportStudentNoCourseList(NoSelectCourseStdsDto condition) throws Exception;

    /**导出点名册*/
    ExcelResult exportRollBookList(RollBookConditionDto condition) throws Exception;

    /**导出研究生点名册*/
    ExcelResult exportGraduteRollBookList(RollBookConditionDto condition) throws Exception;

    PageResult<RollBookList> findRollBookList(PageCondition<RollBookConditionDto> condition);

    PreViewRollDto findPreviewRollBookListById(Long teachingClassId,Long calendarId);

    List<StudnetTimeTable> findStudentTimetab(Long calendarId, String studentCode);

    PageResult<ClassCodeToTeacher> findAllTeacherTimeTable(PageCondition<ClassCodeToTeacher> condition);

    List<TimeTable> getStudentTimetab(Long calendarId, String studentCode, Integer week);

    List<TeacherTimeTable> findTeacherTimetable(Long calendarId, String teacherCode);

    List<TimeTable> getTeacherTimetable(Long calendarId, String teacherCode, Integer week);

    ExcelResult export(NoSelectCourseStdsDto condition);

    ExcelResult exportTeacher(ClassCodeToTeacher condition);

    String exportPreRollBookList(ExportPreCondition condition) throws Exception;
    
    /**研究生导出未选课学生名单*/
	String exportStudentNoCourseListGradute(NoSelectCourseStdsDto condition) throws Exception;

}
