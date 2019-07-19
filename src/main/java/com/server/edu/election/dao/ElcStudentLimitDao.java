package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElcStudentLimitDto;
import com.server.edu.election.entity.ElcStudentLimit;
import com.server.edu.election.vo.ElcStudentLimitVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcStudentLimitDao extends Mapper<ElcStudentLimit>,MySqlMapper<ElcStudentLimit> {
	List<ElcStudentLimitVo> getLimitStudents(ElcStudentLimitDto elcStudentLimitDto);
}