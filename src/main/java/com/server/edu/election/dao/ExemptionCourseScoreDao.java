package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.entity.ExemptionCourseScore;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExemptionCourseScoreDao extends Mapper<ExemptionCourseScore> {

    Page<ExemptionCourseScoreVo> findExemptionScore(ExemptionCourseScoreDto scoreDto);

    //推免生待澄清
    List<ExemptionCourseScore> findCourseScore(@Param("courseRule")ExemptionCourseRule courseRule,
                                               @Param("list") List<String> list);
    ExemptionCourseScore findStudentScore(@Param("calendarId") Long calendarId,@Param("studentCode") String studentCode,@Param("courseCode") String courseCode);



}
