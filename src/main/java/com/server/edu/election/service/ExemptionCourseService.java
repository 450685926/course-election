package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.entity.ExemptionCourseScore;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ExemptionQuery;
import com.server.edu.election.vo.ExemptionApplyManageVo;
import com.server.edu.election.vo.ExemptionCourseMaterialVo;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import com.server.edu.election.vo.ExemptionCourseVo;
import com.server.edu.election.vo.ExemptionStudentCountVo;
import com.server.edu.election.vo.StudentAndCourseVo;

public interface ExemptionCourseService {
    PageResult<ExemptionCourseVo> findExemptionCourse(PageCondition<ExemptionCourseVo> condition);
    String deleteExemptionCourse(List<Long> ids);

    String addExemptionCourse(ExemptionCourse exemptionCourse);

    String updateExemptionCourse(ExemptionCourse exemptionCourse);

    PageResult<ExemptionCourseScoreVo> findExemptionScore(PageCondition<ExemptionCourseScoreDto> courseScoreDto);

    PageResult<ExemptionCourseRuleVo> findExemptionRule(PageCondition<ExemptionCourseRuleVo> rulePageCondition);

    String deleteExemptionCourseRule(List<Long> ids,Integer applyType);

    String addExemptionCourseRule(ExemptionCourseRuleVo courseRuleVo);

    PageResult<ExemptionApplyManageVo> findExemptionApply(PageCondition<ExemptionApplyCondition> condition);

    String addExemptionApply(ExemptionApplyManage applyManage);

    RestResult<ExemptionCourseMaterialVo> addExemptionApplyConditionLimit(ExemptionApplyManage applyManage);
    RestResult<Student> findStudentMessage(String studentCode);

    //批量删除
    String deleteExemptionApply(List<Long> ids);

    //批量审批
    String approvalExemptionApply(List<Long> ids,Integer status,String auditor);

    //编辑
    String editExemptionApply(ExemptionApplyManage applyManage);

    /**免修免考申请管理导出*/
    String export(ExemptionApplyCondition condition) throws Exception;

    /**导入入学成绩*/
    String addExcel(List<ExemptionCourseScore> datas, Long calendarId);

    /**导入免修申请*/
    String addExcelApply(List<ExemptionApplyManage> datas, Long calendarId);

    /**免修新增规则下拉取值*/
    RestResult<List<ExemptionCourseVo>> filterCourseCode(ExemptionCourseRuleVo courseRuleVo, Integer applyType);

    /**编辑申请规则*/
    String editExemptionCourseRule(ExemptionCourseRuleVo courseRuleVo);
	/** 免修免考结果统计*/
    PageResult<ExemptionStudentCountVo> exemptionCount(PageCondition<ExemptionQuery> page);
	
    /**免修免考结果导出 */
    RestResult<String> exemptionCountExport(ExemptionQuery page);
	
    /**
     * 研究生免修免考审批
     * @param ids
     * @param status
     * @return
     */
    String approvalGraduateExemptionApply(List<Long> ids, Integer status);
    
    /**
     * 研究生免修免考申请管理
     * @param condition
     * @return
     */
	PageResult<ExemptionApplyManageVo> findGraduateExemptionApply(PageCondition<ExemptionQuery> condition);
	
	/**
	 * 研究生选课申请管理列表导出
	 * @param page
	 * @return
	 */
	RestResult<String> findGraduateExemptionApplyExport(ExemptionQuery page);
	
	/**
	 * 根据学生Id与课程编码找出学生课程
	 * @param studentCode
	 * @param calendarId 
	 * @return
	 */
	StudentAndCourseVo findCourseCode(String studentCode, Long calendarId);
	
	/**
	 * 研究生免修免考课程列表
	 * @param studentId
	 * @param calendarId 
	 * @return
	 */
	StudentAndCourseVo findStudentApplyCourse(String studentId, Long calendarId);
	
	/**
	 * 管理员添加免修免考
	 * @param applyManage
	 * @return
	 */
	String adminAddApply(ExemptionApplyManage applyManage);
	
	String addGraduateExemptionApply(ExemptionApplyManage applyManage);
}

