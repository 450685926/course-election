package com.server.edu.election.dao;

import com.server.edu.election.vo.ElcRetakeSetVo;
import org.apache.ibatis.annotations.Param;

public interface RetakeCourseSetDao {

    int saveRetakeCourseSet(@Param("retakeSet") ElcRetakeSetVo elcRetakeSetVo);

    Long findRetakeSetId(Long calendarId);

    int deleteByCalendarId(Long calendarId);
}
