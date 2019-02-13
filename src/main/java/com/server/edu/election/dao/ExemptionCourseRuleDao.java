package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import org.apache.ibatis.annotations.Param;

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

    void addExemptionCourseRule(ExemptionCourseRuleVo scoreRule);

    ExemptionCourseRuleVo findScoreOrMaterial(@Param("calendarId") Long calendarId,
                                              @Param("courseCode") String courseCode,
                                              @Param("student") Student student,
                                              @Param("applyType") Integer applyType);

}
