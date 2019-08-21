package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.entity.ExemptionCourseScore;
import com.server.edu.election.entity.ExemptionCourseScoreGradute;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExemptionCourseScoreGraduteDao extends Mapper<ExemptionCourseScoreGradute> {

}
