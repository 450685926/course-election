package com.server.edu.election.dao;

import com.server.edu.election.entity.Student;

public interface StudentDao {
    Student findStudentByCode(String studentCode);
    int deleteByPrimaryKey(Long id);

    int insert(Student record);

    int insertSelective(Student record);

    Student selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Student record);

    int updateByPrimaryKey(Student record);
}
