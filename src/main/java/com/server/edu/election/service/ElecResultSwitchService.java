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
	
}
