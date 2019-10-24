package com.server.edu.exam.dao;

import com.server.edu.exam.entity.GraduateExamMonitorTeacher;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface GraduateExamMonitorTeacherDao extends Mapper<GraduateExamMonitorTeacher> {
    List<GraduateExamMonitorTeacher> checkNum(GraduateExamMonitorTeacher monitorTeacher);

    List<GraduateExamMonitorTeacher> checkTeacherNumber(@Param("roomCapacity") Integer roomCapacity, @Param("dptId") String dptId);
}