package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.RebuildCourseNoChargeType;

import java.util.List;

public interface RebuildCourseNoChargeTypeDao {
    int deleteByPrimaryKey(Long id);

    int insert(RebuildCourseNoChargeType record);

    int insertSelective(RebuildCourseNoChargeType record);

    RebuildCourseNoChargeType selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RebuildCourseNoChargeType record);

    int updateByPrimaryKey(RebuildCourseNoChargeType record);

    Page<RebuildCourseNoChargeType> findCourseNoChargeType(RebuildCourseNoChargeType condition);

    void deleteRebuildCourseNoChargeType(List<Long> list);
}
