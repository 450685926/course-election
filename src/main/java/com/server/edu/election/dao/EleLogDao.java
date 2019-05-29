package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.common.entity.LogEntity;

public interface EleLogDao {

	void addOperateLog(List<LogEntity> entityList);

	List<LogEntity> getAgingLog(long overDueTime);

	void deleteAgingLog(List<LogEntity> list);

}
