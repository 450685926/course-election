package com.server.edu.election.dao;

import com.server.edu.election.entity.RebuildCourseRecycle;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface RebuildCourseRecycleDao extends Mapper<RebuildCourseRecycle>,MySqlMapper<RebuildCourseRecycle> {
    List<String> selectScreenLabelName(@Param("calendarId") Long calendarId);
}