package com.server.edu.mutual.dao;


import java.util.List;

import com.server.edu.mutual.entity.ElcMutualApplyAuditLogs;
import com.server.edu.mutual.entity.ElcMutualApplyCopyVo;
import com.server.edu.mutual.vo.ElcMutualApplyAuditLogsVo;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ElcMutualApplyAuditLogsDao extends Mapper<ElcMutualApplyAuditLogs> {

	List<ElcMutualApplyAuditLogsVo> queryAuditLogList(ElcMutualApplyAuditLogsVo vo);

	/*根据申请id查询本研互选申请信息*/
	ElcMutualApplyCopyVo getElcMutualApplyById(@Param("muApplyId") Long muApplyId);

	/*查询本研互选课程审核日志*/
	List<ElcMutualApplyAuditLogsVo> queryAuditLogLists(ElcMutualApplyAuditLogsVo vo);
}