package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.entity.ExemptionApplyAuditSwitch;

public interface ExemptionAuditSwitchService {
	/**
	 * 研究生添加免修免考申请审核开关
	 * @param applyAuditSwitch
	 */
	void addExemptionAuditSwitch(ExemptionApplyAuditSwitch applyAuditSwitch);
	
	/**
	 * 研究生查询免修免考申请审核开关
	 * @param condition
	 * @return 
	 */
	PageResult<ExemptionApplyAuditSwitch> queryExemptionAuditSwitch(PageCondition<ExemptionApplyAuditSwitch> condition);

	/**
	 * 根据ID查询免修免考申请审核开关
	 * @param id
	 * @return
	 */
	ExemptionApplyAuditSwitch findExemptionAuditSwitchById(Long id);

	/**
	 * 修改免修免考申请审核开关
	 * @param applyAuditSwitch
	 */
	void updateExemptionAuditSwitch(ExemptionApplyAuditSwitch applyAuditSwitch);

	/**
	 * 删除免修免考申请审核开关
	 * @param ids
	 */
	void deleteExemptionAuditSwitch(List<Long> ids);
}
