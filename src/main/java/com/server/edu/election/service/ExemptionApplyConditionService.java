package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ExemptionApplyGraduteConditionDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ExemptionApplyGraduteCondition;

public interface ExemptionApplyConditionService {

	/**
	 * 研究生添加免修免考申请条件
	 * @param applyCondition
	 */
	void addExemptionApplyCondition(ExemptionApplyGraduteCondition applyCondition);

	/**
	 * 查询研究生免修免考申请条件列表
	 * @param applyCondition
	 * @return
	 */
	PageResult<ExemptionApplyGraduteCondition> queryExemptionApplyCondition(
			PageCondition<ExemptionApplyGraduteConditionDto> applyCondition);

	/**
	 * 根据ID查询免修免考申请条件
	 * @param id
	 * @return
	 */
	ExemptionApplyGraduteCondition findExemptionAuditConditionById(Long id);

	/**
	 * 修改免修免考申请条件
	 * @param applyCondition
	 */
	void updateExemptionApplyCondition(ExemptionApplyGraduteCondition applyCondition);

	/**
	 * 删除免修免考申请条件
	 * @param ids
	 */
	void deleteExemptionApplyCondition(List<Long> ids);

	/**
	 * 根据课程编号查询名称和培养层次
	 * @param courseCode
	 * @return
	 */
	CourseOpen queryNameAndTrainingLevelByCode(String courseCode);

	/**
	 * 根据课程编号和学籍信息查询所有符合的申请条件
	 * @param courseCode
	 * @param studentCode
	 * @return
	 */
	List<ExemptionApplyGraduteCondition> queryApplyConditionByCourseCodeAndStudentId(String courseCode,String studentCode);
	
	/**
	 * 根据课程code或者名称查询课程信息
	 * @param course
	 * @return
	 */
	Integer getCourseByCodeOrName(Course course);
}
