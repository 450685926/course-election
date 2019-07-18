package com.server.edu.election.dao;

import org.apache.ibatis.annotations.Param;
import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionApplyAuditSwitch;
import tk.mybatis.mapper.common.Mapper;

public interface ExemptionAuditSwitchDao extends Mapper<ExemptionApplyAuditSwitch>{

	/**
	 * 分页查询免修免考申请审核开关
	 * @param condition
	 * @return
	 */
	Page<ExemptionApplyAuditSwitch> listPage(@Param("condition") ExemptionApplyAuditSwitch condition);
	
	
}
