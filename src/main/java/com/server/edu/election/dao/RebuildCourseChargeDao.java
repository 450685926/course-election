package com.server.edu.election.dao;

import java.util.List;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.RebuildCoursePaymentCondition;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.vo.RebuildCourseNoChargeList;

import tk.mybatis.mapper.common.Mapper;

public interface RebuildCourseChargeDao extends Mapper<RebuildCourseCharge> {


    Page<RebuildCourseCharge> findCourseCharge(RebuildCourseCharge condition);

    void deleteCourseCharge(List<Long> list);

    /**移动到回收站*/
    void addCourseStudentToRecycle( List<RebuildCourseNoChargeList> list);

    /**查询回收站*/
    Page<RebuildCourseNoChargeList> findRecycleCourse(RebuildCourseDto condition);

    /**从回收站回复数据*/

    void recoveryDataFromRecycleCourse(List<RebuildCourseNoChargeList> list);
}
