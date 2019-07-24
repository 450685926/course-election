package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.FailedCourseVo;
import com.server.edu.election.vo.RebuildCourseVo;
import com.server.edu.election.vo.RetakeCourseCountVo;

import java.util.List;

public interface RetakeCourseService {
    void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo);

    PageResult<RetakeCourseCountVo> findRetakeCourseCountList(PageCondition<RetakeCourseCountVo> condition);

    void updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);

    void deleteRetakeCourseCount(Long retakeCourseCountId);

    ElcRetakeSetVo getRetakeRul(Long calendarId, String projectId);

    List<FailedCourseVo> failedCourseList(String studentId, Long calendarId);

    List<RebuildCourseVo> findRebuildCourseList(String studentId, Long calendarId, String keyWord, String currentManageDptId);
}
