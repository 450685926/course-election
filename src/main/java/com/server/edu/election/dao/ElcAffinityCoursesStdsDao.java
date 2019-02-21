package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.ElcAffinityCoursesStds;

import tk.mybatis.mapper.common.Mapper;

public interface ElcAffinityCoursesStdsDao
    extends Mapper<ElcAffinityCoursesStds>
{
    int batchInsert(List<ElcAffinityCoursesStds> list);
    
    /**
     * 查询所有优先课程与优先学生
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<ElcAffinityCoursesStds> selectStuAndCourse();
}