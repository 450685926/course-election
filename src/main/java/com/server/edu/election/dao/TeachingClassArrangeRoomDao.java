package com.server.edu.election.dao;

import com.server.edu.election.dto.TimeTableMessage;

import java.util.List;

public interface TeachingClassArrangeRoomDao {
    /**根据教学安排id查询教师和周次*/
    List<TimeTableMessage> findWeekAndTeacherCode(Long arrangeTimeId);
}
