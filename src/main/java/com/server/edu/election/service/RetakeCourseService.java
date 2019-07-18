package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.RetakeCourseCountVo;

public interface RetakeCourseService {
    void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo);

    void deleteRetakeRules(Long calendarId);

    PageResult<RetakeCourseCountVo> findRetakeCourseCountList(PageCondition<RetakeCourseCountVo> condition);

    void updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);
}
