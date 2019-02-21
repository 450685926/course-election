package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExemptionCourseRuleDao extends Mapper<ExemptionCourseRule> {


    Page<ExemptionCourseRuleVo> findExemptionCourseRule(ExemptionCourseRule courseRule);

    void deleteExemptionCourseRule(List<Long> list);

    void addExemptionCourseRule(ExemptionCourseRuleVo scoreRule);

    ExemptionCourseRuleVo findScoreOrMaterial(@Param("calendarId") Long calendarId,
                                              @Param("courseCode") String courseCode,
                                              @Param("student") Student student,
                                              @Param("applyType") Integer applyType);

}
