package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcCrossStds;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcCrossStdsDao extends Mapper<ElcCrossStds>,MySqlMapper<ElcCrossStds> {
	List<ElcMutualCrossStuVo> getCrossStds(ElcMutualCrossStuDto dto);

	//返回单个po对象在切换学期时代码报错（切换未上送学生id所以返回多条记录），故统一使用list接收
	List<ElcMutualCrossStuVo> isInElcMutualStdList(ElcMutualCrossStuDto dto);
}