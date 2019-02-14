package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.ElcMedWithdrawApplyLog;

import tk.mybatis.mapper.common.Mapper;

public interface ElcMedWithdrawApplyLogDao extends Mapper<ElcMedWithdrawApplyLog> {
	int batchInsert(List<ElcMedWithdrawApplyLog> list);
}