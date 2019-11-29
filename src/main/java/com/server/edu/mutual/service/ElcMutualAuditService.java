package com.server.edu.mutual.service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.mutual.dto.AgentApplyDto;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.vo.ElcMutualApplyAuditLogsVo;
import com.server.edu.mutual.vo.ElcMutualApplyVo;

public interface ElcMutualAuditService {
	/**
	 * 行政学院审核课程视图
	 * @param dto
	 * @return
	 */
	PageInfo<ElcMutualApplyVo> collegeApplyCourseList(PageCondition<ElcMutualApplyDto> dto);
	
	/**
	 * 行政学院审核学生视图
	 * @param dto
	 * @return
	 */
	PageInfo<ElcMutualApplyVo> collegeApplyStuList(PageCondition<ElcMutualApplyDto> dto);

	/**
	 * 开课学院审核课程视图
	 * @param dto
	 * @return
	 */
	PageInfo<ElcMutualApplyVo> openCollegeApplyCourseList(PageCondition<ElcMutualApplyDto> dto);
	
	int aduit(ElcMutualApplyDto dto);
	
	int agentApply(AgentApplyDto dto);

	/**
	 * 开课学院审核学生视图
	 * @param dto
	 * @return
	 */
	PageInfo<ElcMutualApplyVo> openCollegeApplyStuList(PageCondition<ElcMutualApplyDto> dto);

	/**
	 * 查询审核日志
	 * @param id
	 * @return
	 */ 
	List<ElcMutualApplyAuditLogsVo> queryAuditLogList (ElcMutualApplyAuditLogsVo vo) throws IllegalAccessException, InvocationTargetException;
	
}
