package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.entity.RebuildCourseCharge;

import java.util.List;

public interface RebuildCourseChargeService {
    PageResult<RebuildCourseCharge> findCourseCharge(PageCondition<RebuildCourseCharge> condition);

    String deleteCourseCharge(List<Long> ids);

    String editCourseCharge(RebuildCourseCharge courseCharge);

    String addCourseCharge(RebuildCourseCharge courseCharge);
}
