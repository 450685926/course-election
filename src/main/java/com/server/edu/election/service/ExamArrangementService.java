package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ExamArrangementDto;
import com.server.edu.election.entity.ExamArrangement;

public interface ExamArrangementService {
	PageInfo<ExamArrangement> list(PageCondition<ExamArrangementDto> condition);
	
	int update(ExamArrangement examArrangement);
	
	int delete(List<Long> ids);
	
	String addByExcel(List<ExamArrangement> datas);

}
