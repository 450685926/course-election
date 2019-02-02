package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import com.server.edu.election.vo.ExemptionCourseVo;

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
}
