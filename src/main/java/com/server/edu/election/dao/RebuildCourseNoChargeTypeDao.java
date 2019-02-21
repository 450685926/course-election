package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RebuildCourseNoChargeTypeDao extends Mapper<RebuildCourseNoChargeType> {


    Page<RebuildCourseNoChargeType> findCourseNoChargeType(RebuildCourseNoChargeType condition);

    void deleteRebuildCourseNoChargeType(List<Long> list);
}
