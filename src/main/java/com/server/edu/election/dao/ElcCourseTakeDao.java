package com.server.edu.election.dao;

import com.server.edu.election.entity.ElcCourseTake;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ElcCourseTakeDao extends Mapper<ElcCourseTake> {
    //判断申请免修免考课程是否已经选课
    int findIsEletionCourse(@Param("studentCode") String studentCode,
                            @Param("calendarId") Long calendarId,@Param("courseCode") String courseCode);

}