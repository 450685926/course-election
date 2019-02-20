package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ExamArrangementDto;
import com.server.edu.election.entity.ExamArrangement;

import tk.mybatis.mapper.common.Mapper;

public interface ExamArrangementDao extends Mapper<ExamArrangement> {
	List<ExamArrangement> selectExamArrangements(ExamArrangementDto dto);
	int batchInsert(List<ExamArrangement> list);
}