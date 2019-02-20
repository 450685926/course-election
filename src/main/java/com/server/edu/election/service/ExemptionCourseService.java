package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.*;

import java.util.List;

public interface ExemptionCourseService {
    PageResult<ExemptionCourseVo> findExemptionCourse(PageCondition<ExemptionCourse> condition);
    String deleteExemptionCourse(List<Long> ids);

    String addExemptionCourse(ExemptionCourse exemptionCourse);

    String updateExemptionCourse(ExemptionCourse exemptionCourse);

    PageResult<ExemptionCourseScoreVo> findExemptionScore(PageCondition<ExemptionCourseScoreDto> courseScoreDto);

    PageResult<ExemptionCourseRuleVo> findExemptionRule(PageCondition<ExemptionCourseRule> rulePageCondition);

    String deleteExemptionCourseRule(List<Long> ids,Integer applyType);

    String addExemptionCourseRule(ExemptionCourseRuleVo courseRuleVo,Integer applyType);

    PageResult<ExemptionApplyManageVo> findExemptionApply(PageCondition<ExemptionApplyCondition> condition);

    String addExemptionApply(ExemptionApplyManage applyManage);

    RestResult<ExemptionCourseMaterialVo> addExemptionApplyConditionLimit(ExemptionApplyManage applyManage);
    RestResult<Student> findStudentMessage(String studentCode);

    //批量删除
    String deleteExemptionApply(List<Long> ids);

    //批量审批
    String approvalExemptionApply(List<Long> ids,Integer status);

    //编辑
    String editExemptionApply(ExemptionApplyManage applyManage);

    /**免修免考申请管理导出*/
    String export(ExemptionApplyCondition condition) throws Exception;
}

