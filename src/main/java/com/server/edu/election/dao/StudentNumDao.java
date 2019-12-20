package com.server.edu.election.dao;

import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.entity.StudentNum;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface StudentNumDao extends Mapper<StudentNum> {
	/**
	 * 本科生选课成绩数据
	 * @param dto
	 * @return
	 */
	List<ScoreStudentResultVo> getStudentScoreList(StudentScoreDto dto);
}