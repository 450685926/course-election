package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;
import tk.mybatis.mapper.common.Mapper;

public interface ExemptionApplyConditionDao extends Mapper<ExemptionApplyGraduteCondition>{

	/**
	 * 查询研究生免修免考申请条件列表
	 * @param applyAuditSwitch
	 * @return
	 */
	Page<ExemptionApplyGraduteCondition> listPage(@Param("condition") ExemptionApplyGraduteCondition condition);
	
	/**
	 * 根据课程编号和学籍信息查询所有符合的申请条件
	 * @param ExemptionApplyGraduteCondition
	 * @return
	 */
	List<ExemptionApplyGraduteCondition> queryApplyConditionByCourseCodeAndStudentId(@Param("condition") ExemptionApplyGraduteConditionDto condition);
}
