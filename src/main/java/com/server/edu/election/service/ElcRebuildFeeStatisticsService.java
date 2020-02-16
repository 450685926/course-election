package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.StudentRebuildFeeDto;
import com.server.edu.election.vo.StudentRebuildFeeVo;
import com.server.edu.util.excel.ExcelWriterUtil;

import java.util.List;

public interface ElcRebuildFeeStatisticsService {

    /**
                  * 学生重修缴费信息
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
	PageResult<StudentRebuildFeeVo> getStudentRebuildFeeList(PageCondition<StudentRebuildFeeDto> condition);
	
	/**
	     * 导出
	* 
	* @param page
	* @return
	* @see [类、类#方法、类#成员]
	*/
	ExcelWriterUtil export(StudentRebuildFeeDto studentRebuildFeeDto) throws Exception;

	/**
	* 不收费学生类型
	*/
	List<String>  transNoChargeTypeStudent();
}
