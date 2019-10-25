package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.entity.StudentUndergraduateScoreInfo;

import tk.mybatis.mapper.common.Mapper;

public interface StudentUndergraduateScoreInfoDao extends Mapper<StudentUndergraduateScoreInfo> {
	/**
	 * 本科生选课成绩数据
	 * @param dto
	 * @return
	 */
	List<ScoreStudentResultVo> getStudentScoreList(StudentScoreDto dto); 
}