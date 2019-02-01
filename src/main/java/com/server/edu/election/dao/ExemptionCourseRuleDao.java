package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.vo.ExemptionCourseRuleVo;

import java.util.List;

public interface ExemptionCourseRuleDao {
    int deleteByPrimaryKey(Long id);

    int insert(ExemptionCourseRule record);

    int insertSelective(ExemptionCourseRule record);

    ExemptionCourseRule selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExemptionCourseRule record);

    int updateByPrimaryKey(ExemptionCourseRule record);

    Page<ExemptionCourseRuleVo> findExemptionCourseRule(ExemptionCourseRule courseRule);

    void deleteExemptionCourseRule(List<Long> list);
}
