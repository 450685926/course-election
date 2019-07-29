package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcStudentLimitDto;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.ElcStudentLimitVo;
import com.server.edu.util.excel.export.ExcelResult;

public interface ElcStudentLimitService {
	
	PageInfo<Student> getUnLimitStudents(PageCondition<StudentDto> condition);
	
	int add(ElcStudentLimitDto elcStudentLimitDto);
	
	PageInfo<ElcStudentLimitVo> getLimitStudents(PageCondition<ElcStudentLimitDto> condition);
	
	int update(ElcStudentLimitDto elcStudentLimitDto);
	
	ElcStudentLimitVo getElcStudentLimit(Long calendarId,Long id);
	
	int delete(List<Long> ids);
	
	int deleteAll(ElcStudentLimitDto elcStudentLimitDto);
	
	ExcelResult export(ElcStudentLimitDto elcStudentLimitDto) throws Exception;
	
	
	

}
