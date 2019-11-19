package com.server.edu.election.service;

import com.server.edu.election.entity.ElcRebuildChargeTimeSet;

public interface ElcRebuildChargeTimeSetService {
	int add(ElcRebuildChargeTimeSet timeSet);
	
	int update(ElcRebuildChargeTimeSet timeSet);
	
	ElcRebuildChargeTimeSet getRebuildChargeTimeSet(Long calendarId,String projId);

	/**
	 * @Description: 检查缴费时间是否在正常缴费时间内
	 * @author kan yuanfeng
	 * @date 2019/11/19 14:56
	 */
    void checkTime(Long calendarId);
}
