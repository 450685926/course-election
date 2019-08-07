package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ExportPreCondition;
import com.server.edu.election.dto.PreViewRollDto;
import com.server.edu.election.dto.PreviewRollBookList;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.RollBookConditionDto;
import com.server.edu.election.dto.StudentSelectCourseList;
import com.server.edu.election.dto.StudnetTimeTable;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.election.vo.TimeTable;
import com.server.edu.util.excel.export.ExcelResult;

public interface ReportManagementService { 

    /**预览点名册 */
    PreviewRollBookList findPreviewRollBookList(RollBookList bookList);

    /**查询学生课表*/
    StudentSchoolTimetabVo findSchoolTimetab(Long calendarId, String studentCode);

    StudentSchoolTimetabVo findSchoolTimetab2(Long calendarId, String studentCode);

    /**查询所有学生课表*/
    PageResult<StudentVo> findAllSchoolTimetab(PageCondition<ReportManagementCondition> condition);

    /**根据用户角色查询学生课表*/
    PageResult<StudentVo> findStudentTimeTableByRole(PageCondition<ReportManagementCondition> condition);

    /**代选课*/
    String otherSelectCourse(StudentSelectCourseList studentSelectCourseList);

    /**导出点名册*/
    ExcelResult exportRollBookList(RollBookConditionDto condition) throws Exception;

    /**导出研究生点名册
     * @param condition*/
    RestResult<String> exportGraduteRollBookList(RollBookConditionDto condition) throws Exception;

    PageResult<RollBookList> findRollBookList(PageCondition<RollBookConditionDto> condition);

    PreViewRollDto findPreviewRollBookListById(Long teachingClassId,Long calendarId);

    PreViewRollDto previewGraduteRollBook(Long teachingClassId);

    List<StudnetTimeTable> findStudentTimetab(Long calendarId, String studentCode);

    List<TimeTable> getStudentTimetab(Long calendarId, String studentCode, Integer week);

    String exportPreRollBookList(ExportPreCondition condition) throws Exception;


	/**
	 * 导出学生课表pdf--研究生
	 * @param calendarId
	 * @param calendarName
	 * @param studentCode
	 * @param studentName
	 * @return
	 */
	RestResult<String> exportStudentTimetabPdf(Long calendarId, String calendarName, String studentCode,String studentName) throws Exception;

}
