package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.entity.ElcMutualApply;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcMutualApplyDao extends Mapper<ElcMutualApply>,MySqlMapper<ElcMutualApply> {
	List<ElcMutualApplyVo> getElcMutualApplyList(ElcMutualApplyDto dto);
	
	/**
	 * 行政学院审核课程视图
	 * @param dto
	 * @return
	 */
	List<ElcMutualApplyVo> collegeApplyCourseList(ElcMutualApplyDto dto);
	
	/**
	 * 行政学院审核学生视图
	 * @param dto
	 * @return
	 */
	List<ElcMutualApplyVo> collegeApplyStuList(ElcMutualApplyDto dto);
	
	/**
	 * 开课学院审核课程视图
	 * @param dto
	 * @return
	 */
	List<ElcMutualApplyVo> openCollegeApplyCourseList(ElcMutualApplyDto dto);
	
	List<ElcMutualApplyVo> getElcMutualCoursesForStu(ElcMutualApplyDto dto);
	
	/**
	 * 开课学院审核学生视图列表
	 * @param condition
	 * @return
	 */
	List<ElcMutualApplyVo> openCollegeApplyStuList(ElcMutualApplyDto dto);

	/**
	 * 查询开课学院审核通过的课程列表
	 * @param dto
	 * @return
	 */
	List<ElcMutualApplyVo> getOpenCollegeAuditList(ElcMutualApplyDto dto); 
}