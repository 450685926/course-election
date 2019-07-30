package com.server.edu.election.dao;

import com.server.edu.election.vo.ElcRetakeSetVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RetakeCourseSetDao {

    int insertRetakeCourseSet(ElcRetakeSetVo elcRetakeSetVo);

    int updateRetakeCourseSet(ElcRetakeSetVo elcRetakeSetVo);

    Long findRetakeSetId(@Param("calendarId") Long calendarId, @Param("projectId") String projectId);

    int deleteByCalendarId(Long calendarId);

    ElcRetakeSetVo findRetakeSet(@Param("calendarId") Long calendarId, @Param("projectId") String projectId);

    List<Long> findRuleIds(@Param("calendarId") Long calendarId, @Param("projectId") String projectId);
}
