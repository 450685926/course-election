package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcCrossStds;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcCrossStdsDao extends Mapper<ElcCrossStds>,MySqlMapper<ElcCrossStds> {
	List<ElcMutualCrossStuVo> getCrossStds(ElcMutualCrossStuDto dto);
}