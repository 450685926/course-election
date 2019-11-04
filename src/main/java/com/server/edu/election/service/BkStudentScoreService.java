package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.election.dto.StudentScoreDto;

public interface BkStudentScoreService {
	/**
	 * 本科生选课成绩数据
	 * @param dto
	 * @return
	 */
	List<ScoreStudentResultVo> getStudentScoreList(StudentScoreDto dto); 

}
