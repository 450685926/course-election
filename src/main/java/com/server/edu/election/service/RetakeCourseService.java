package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.FailedCourseVo;
import com.server.edu.election.vo.RebuildCourseVo;
import com.server.edu.election.vo.RetakeCourseCountVo;
import com.server.edu.session.util.entity.Session;

import java.util.List;

public interface RetakeCourseService {
    void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo);

    PageResult<RetakeCourseCountVo> findRetakeCourseCountList(PageCondition<RetakeCourseCountVo> condition);

    void updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);

    void deleteRetakeCourseCount(List<Long> retakeCourseCountIds);

    ElcRetakeSetVo getRetakeRule(Long calendarId, String projectId);

    List<FailedCourseVo> failedCourseList(Long calendarId);

    List<RebuildCourseVo> findRebuildCourseList(Long calendarId, String keyWord);

    void updateRebuildCourse(RebuildCourseVo rebuildCourseVo);
}
