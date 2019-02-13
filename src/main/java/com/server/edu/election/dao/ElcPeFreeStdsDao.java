package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElcPeFreeStdsDto;
import com.server.edu.election.entity.ElcPeFreeStds;
import com.server.edu.election.vo.ElcPeFreeStdsVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcPeFreeStdsDao extends Mapper<ElcPeFreeStds> {
	List<ElcPeFreeStdsVo> selectElcPeFreeStds(ElcPeFreeStdsDto dto);
	int batchInsert(List<String> list);
	List<ElcPeFreeStdsVo> selectElcStudents(ElcPeFreeStdsDto dto);
}