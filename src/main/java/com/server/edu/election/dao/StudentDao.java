package com.server.edu.election.dao;

import com.server.edu.election.entity.Student;
import tk.mybatis.mapper.common.Mapper;

public interface StudentDao extends Mapper<Student> {
    //通过学号获取学生信息
    Student findStudentByCode(String studentCode);
}


