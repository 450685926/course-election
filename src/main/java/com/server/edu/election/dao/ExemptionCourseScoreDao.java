package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionCourseScore;
import com.server.edu.election.vo.ExemptionCourseScoreVo;

public interface ExemptionCourseScoreDao {
    int deleteByPrimaryKey(Long id);

    int insert(ExemptionCourseScore record);

    int insertSelective(ExemptionCourseScore record);

    ExemptionCourseScore selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExemptionCourseScore record);

    int updateByPrimaryKey(ExemptionCourseScore record);

    Page<ExemptionCourseScoreVo> findExemptionScore(ExemptionCourseScoreDto scoreDto);
}
