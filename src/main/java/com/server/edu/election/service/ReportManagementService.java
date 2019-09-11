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
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

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

    PageResult<RollBookList> findRollBookList(PageCondition<RollBookConditionDto> condition);

    PageResult<RollBookList> findGraduteRollBookList(PageCondition<RollBookConditionDto> condition);

    List<RollBookList> getExportGraduteRollBookList(List<String> ids);

    PreViewRollDto findPreviewRollBookListById(Long teachingClassId,Long calendarId);

    PreViewRollDto previewGraduteRollBook(Long teachingClassId);

    List<StudnetTimeTable> findStudentTimetab(Long calendarId, String studentCode);

    List<TimeTable> getStudentTimetab(Long calendarId, String studentCode, Integer week);

    String exportPreRollBookList(ExportPreCondition condition) throws Exception;

    String exportGraduteRollBook(ExportPreCondition condition) throws Exception;

	/**
	 * 导出学生课表pdf--研究生
	 * @param calendarId
	 * @param studentCode
	 * @return
	 */
	RestResult<String> exportStudentTimetabPdf(Long calendarId, String studentCode) throws Exception;

	/**
	 * 研究生批量导出点名册压缩包
	 * @param ids
	 * @param fileName
	 * @return
	 */
	String exportGraduteRollBookZipList(List<String> ids, StringBuffer fileName) throws Exception ;
	
	/**
	 * 研究生批量导出点名册压缩包2
	 * @param ids
	 * @param RestResult
	 * @return
	 */
	RestResult<String> exportGraduteRollBookZipList2(List<String> ids, StringBuffer fileName) throws Exception ;
	
	/**
	 * 批量下载点名册，导出并打压缩包(生成key)
	 * @param ids
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	ExcelResult exportGraduteRollBookZipList3(List<String> ids) ;

}
