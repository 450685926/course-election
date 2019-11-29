package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.server.edu.election.entity.ElcAffinityCoursesStds;
import com.server.edu.election.vo.ElcAffinityCoursesStdsVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcAffinityCoursesStdsDao
    extends Mapper<ElcAffinityCoursesStds>
{
    int batchInsert(List<ElcAffinityCoursesStds> list);
    
    List<ElcAffinityCoursesStdsVo> getStudentByCourseId(@Param("courseCode") String courseCode);
    
}