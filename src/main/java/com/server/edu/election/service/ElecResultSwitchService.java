package com.server.edu.election.service;

import com.server.edu.election.entity.ElcResultSwitch;

public interface ElecResultSwitchService {

	/**
	 * 添加选课结果设置开关
	 * @param resultSwitch
	 * @return
	 */
	void add(ElcResultSwitch resultSwitch);

	/**
	 * 查询选课结果设置开关
	 * @param id
	 * @return
	 */
	ElcResultSwitch get(Long id);
	
	/**
	 * 根据学期号查询选课结果设置开关
	 * @param calendarId
	 * @param projectId
	 * @return
	 */
	ElcResultSwitch getSwitch(Long calendarId, String projectId);

	/**
	 * 根据学期号获取选课结果开关状态
	 * @param calendarId
	 * @return
	 */
	boolean getSwitchStatus(Long calendarId,  String projectId);
	
}
