package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.FailedCourseVo;
import com.server.edu.election.vo.RebuildCourseVo;
import com.server.edu.election.vo.RetakeCourseCountVo;

import java.util.List;

public interface RetakeCourseService {
    void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo);

    PageResult<RetakeCourseCountVo> findRetakeCourseCountList(PageCondition<RetakeCourseCountVo> condition);

    void updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);

    void deleteRetakeCourseCount(List<Long> retakeCourseCountIds);

    Boolean getRetakeRule(Long calendarId, String projectId);

    ElcRetakeSetVo getRetakeSet(Long calendarId, String projectId);
    List<ElcRetakeSetVo> getRetakeSet2(Long calendarId, String projectId);

    List<FailedCourseVo> failedCourseList(Long calendarId);

    PageResult<RebuildCourseVo> findRebuildCourseList(PageCondition<RebuildCourseDto> condition);

    void updateRebuildCourse(RebuildCourseVo rebuildCourseVo);
}
