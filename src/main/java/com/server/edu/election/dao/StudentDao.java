package com.server.edu.election.dao;

import com.server.edu.election.entity.Student;
import tk.mybatis.mapper.common.Mapper;

public interface StudentDao extends Mapper<Student> {
    Student findStudentByCode(String studentCode);
}


