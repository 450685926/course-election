package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExemptionCourseRuleDao extends Mapper<ExemptionCourseRule> {


    Page<ExemptionCourseRuleVo> findExemptionCourseRule(ExemptionCourseRuleVo courseRule);

    void deleteExemptionCourseRule(List<Long> list);

    void addExemptionCourseRule(ExemptionCourseRuleVo scoreRule);

    ExemptionCourseRuleVo findScoreOrMaterial(@Param("calendarId") Long calendarId,
                                              @Param("courseCode") String courseCode,
                                              @Param("student") Student student,
                                              @Param("applyType") Integer applyType);

    /**查询条件下已有课程规则*/
    List<String> findRuleExist(@Param("courseRuleVo") ExemptionCourseRuleVo courseRuleVo,@Param("applyType") Integer applyType);

    List<String> findCourseName(List<String> list);

}
