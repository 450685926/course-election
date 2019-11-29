package com.server.edu.mutual.dao;


import java.util.List;

import com.server.edu.mutual.entity.ElcMutualApplyAuditLogs;
import com.server.edu.mutual.vo.ElcMutualApplyAuditLogsVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcMutualApplyAuditLogsDao extends Mapper<ElcMutualApplyAuditLogs> {

	List<ElcMutualApplyAuditLogsVo> queryAuditLogList(ElcMutualApplyAuditLogsVo vo);
}