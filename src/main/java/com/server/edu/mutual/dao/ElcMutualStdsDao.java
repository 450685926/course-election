package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcMutualStds;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcMutualStdsDao extends Mapper<ElcMutualStds>,MySqlMapper<ElcMutualStds> {
	List<ElcMutualCrossStuVo> getMutualStds(ElcMutualCrossStuDto dto);
	
	
	/**
	   *  判断学生是否在本研互选学生名单中
	 * @param dto
	 * @return
	 */
	ElcMutualCrossStuVo isInElcMutualStdList(ElcMutualCrossStuDto dto);
}