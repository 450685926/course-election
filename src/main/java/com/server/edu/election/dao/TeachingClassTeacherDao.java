package com.server.edu.election.dao;

import com.server.edu.election.entity.TeachingClassTeacher;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface TeachingClassTeacherDao extends Mapper<TeachingClassTeacher> {
    String findTeacherName(String teacherCode);

    List<String> findTeacherNames(@Param("teacherCodes") List<String> teacherCodes);
}