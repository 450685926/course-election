package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.entity.RebuildCourseNoChargeList;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.vo.StudentVo;

import java.util.List;

public interface RebuildCourseChargeService {
    PageResult<RebuildCourseCharge> findCourseCharge(PageCondition<RebuildCourseCharge> condition);

    String deleteCourseCharge(List<Long> ids);

    String editCourseCharge(RebuildCourseCharge courseCharge);

    String addCourseCharge(RebuildCourseCharge courseCharge);

    PageResult<RebuildCourseNoChargeType> findCourseNoChargeType(PageCondition<RebuildCourseNoChargeType> condition);

    String addCourseNoChargeType(RebuildCourseNoChargeType noChargeType);

    String deleteCourseNoChargeType(List<Long> ids);

    String editCourseNoChargeType(RebuildCourseNoChargeType courseNoCharge);

    //查询未缴费课程名单
    PageResult<RebuildCourseNoChargeList> findCourseNoChargeList(PageCondition<RebuildCourseNoChargeType> condition);

    //查询学生未缴费课程门数
    PageResult<StudentVo> findCourseNoChargeStudentList(PageCondition<RebuildCourseNoChargeType> condition);
}

