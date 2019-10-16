package com.server.edu.election.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.jackson.JacksonUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dto.StudentRebuildFeeDto;
import com.server.edu.election.service.ElcRebuildFeeStatisticsService;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.StudentRebuildFeeVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ElcRebuildFeeStatisticsServiceImpl implements ElcRebuildFeeStatisticsService{

	@Autowired
	private ElcCourseTakeDao elcCourseTakeDao;
	
    @Autowired
    private DictionaryService dictionaryService;
	@Override
	public PageResult<StudentRebuildFeeVo> getStudentRebuildFeeList(PageCondition<StudentRebuildFeeDto> condition) {
		// TODO Auto-generated method stub
		String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		StudentRebuildFeeDto dto = condition.getCondition();
		int mode = TableIndexUtil.getMode(dto.getCalendarId());
		dto.setMode(mode);
		dto.setManageDptId(dptId);
		Page<StudentRebuildFeeVo> list = (Page<StudentRebuildFeeVo>)elcCourseTakeDao.getStudentRebuildFeeList(condition.getCondition());
		return new PageResult<>(list);
	}

	@Override
	public ExcelWriterUtil export(StudentRebuildFeeDto studentRebuildFeeDto) throws Exception {
		// TODO Auto-generated method stub
		String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
		int mode = TableIndexUtil.getMode(studentRebuildFeeDto.getCalendarId());
		studentRebuildFeeDto.setMode(mode);
		studentRebuildFeeDto.setManageDptId(dptId);
		List<StudentRebuildFeeVo> list = elcCourseTakeDao.getStudentRebuildFeeList(studentRebuildFeeDto);
		GeneralExcelDesigner design = getDesign();
		List<JSONObject> convertList = JacksonUtil.convertList(list);
	    design.setDatas(convertList);
	    ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
		return excelUtil;
	}
	
	private GeneralExcelDesigner getDesign() {
		 GeneralExcelDesigner design = new GeneralExcelDesigner();
		 design.setNullCellValue("");
		 design.addCell("学号","studentId");
		 design.addCell("姓名","studentName");
		 design.addCell("学院", "facultyI18n");
         design.addCell("专业", "professionI18n");
		 design.addCell("课程序号","teachingClassCode");
		 design.addCell("课程名称","courseName");
         design.addCell("课程性质", "nature").setValueHandler(
				 (value, rawData, cell) -> {
					 if("1".equals(value)) {
						 value ="公开课";
					 }else {
						 value ="专业课";
					 }
					 return value;
				 });;
		 design.addCell("应缴金额","amount");
		 design.addCell("已缴金额","pay");
		 design.addCell("是否已缴费","paId").setValueHandler(
				 (value, rawData, cell) -> {
					    if(Constants.PAID.toString().equals(value)) {
					    	value ="是";
					    }else {
					    	value ="否";
						}
	                    return value;
	                });
		 design.addCell("课程学年学期","couCalendarIdI18n");
		 design.addCell("缴费时间","payTime");
		 return design;
	}

}
