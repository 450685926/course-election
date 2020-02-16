package com.server.edu.election.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.jackson.JacksonUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.config.DoubleHandler;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.RebuildCourseNoChargeTypeDao;
import com.server.edu.election.dto.StudentRebuildFeeDto;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.service.ElcRebuildFeeStatisticsService;
import com.server.edu.election.service.RebuildCourseChargeService;
import com.server.edu.election.vo.RebuildCourseNoChargeTypeVo;
import com.server.edu.election.vo.StudentRebuildFeeVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ElcRebuildFeeStatisticsServiceImpl implements ElcRebuildFeeStatisticsService{

	@Autowired
	private ElcCourseTakeDao elcCourseTakeDao;
	
    @Autowired
    private DictionaryService dictionaryService;


	@Autowired
	private RebuildCourseChargeService noChargeTypeservice;

	@Override
	public PageResult<StudentRebuildFeeVo> getStudentRebuildFeeList(PageCondition<StudentRebuildFeeDto> condition) {
		// TODO Auto-generated method stub
		String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();

		StudentRebuildFeeDto dto = condition.getCondition();
		/*int mode = TableIndexUtil.getMode(dto.getCalendarId());
		dto.setMode(mode);*/
		dto.setManageDptId(dptId);
		List<String> noStuPay = this.transNoChargeTypeStudent();
		dto.setNoPayStudentType(noStuPay);
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		Page<StudentRebuildFeeVo> list = elcCourseTakeDao.getStudentRebuildFeeList(condition.getCondition());
		return new PageResult<>(list);
	}

	@Override
	public List<String> transNoChargeTypeStudent(){
		Set<String> noTypeStudent = new HashSet<>();
		PageCondition<RebuildCourseNoChargeTypeVo> condition = new PageCondition<>();
		condition.setCondition(new RebuildCourseNoChargeTypeVo());
		condition.setPageNum_(0);
		condition.setPageSize_(0);
		PageResult<RebuildCourseNoChargeType> noChargeType = noChargeTypeservice.findCourseNoChargeType(condition);
		if(noChargeType != null && CollectionUtil.isNotEmpty(noChargeType.getList())){
			List<RebuildCourseNoChargeType> list = noChargeType.getList();
			for (RebuildCourseNoChargeType noChargeStudent : list) {
				noTypeStudent.add(noChargeStudent.getTrainingLevel() + noChargeStudent.getTrainingCategory()
				+ noChargeStudent.getEnrolMethods() + noChargeStudent.getSpcialPlan() + noChargeStudent.getIsOverseas()
				+ noChargeStudent.getRegistrationStatus());
			}
		}

		return new ArrayList<>(noTypeStudent);
	}

	@Override
	public ExcelWriterUtil export(StudentRebuildFeeDto studentRebuildFeeDto) throws Exception {
		// TODO Auto-generated method stub
		String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
		studentRebuildFeeDto.setManageDptId(dptId);
		List<StudentRebuildFeeVo> list = elcCourseTakeDao.getStudentRebuildFeeList(studentRebuildFeeDto);
		GeneralExcelDesigner design = getDesign();
		List<JSONObject> convertList = JacksonUtil.convertList(list);
	    design.setDatas(convertList);
	    ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
		return excelUtil;
	}
	
	private GeneralExcelDesigner getDesign() {
		 DoubleHandler doubleHandler = new DoubleHandler();
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
				 });
        design.addCell("学分", "credits");
	 	design.addCell("应缴金额","amount").setValueHandler(doubleHandler);
	 	design.addCell("已缴金额","pay").setValueHandler(doubleHandler);
	 	design.addCell("是否已缴费","paid").setValueHandler(
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
