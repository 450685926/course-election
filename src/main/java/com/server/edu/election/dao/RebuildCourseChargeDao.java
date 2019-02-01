package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.RebuildCourseCharge;

import java.util.List;

public interface RebuildCourseChargeDao {
    int deleteByPrimaryKey(Long id);

    int insert(RebuildCourseCharge record);

    int insertSelective(RebuildCourseCharge record);

    RebuildCourseCharge selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RebuildCourseCharge record);

    int updateByPrimaryKey(RebuildCourseCharge record);


    Page<RebuildCourseCharge> findCourseCharge(RebuildCourseCharge condition);

    void deleteCourseCharge(List<Long> list);
}
