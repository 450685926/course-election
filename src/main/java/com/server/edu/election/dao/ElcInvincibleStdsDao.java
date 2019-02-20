package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.ElcInvincibleStds;

import tk.mybatis.mapper.common.Mapper;

public interface ElcInvincibleStdsDao extends Mapper<ElcInvincibleStds> {
	int batchInsert(List<ElcInvincibleStds> list);
}