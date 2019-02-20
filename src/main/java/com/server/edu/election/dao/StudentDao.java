package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.Student;

import tk.mybatis.mapper.common.Mapper;

public interface StudentDao extends Mapper<Student> {
    //通过学号获取学生信息
    Student findStudentByCode(String studentCode);
    List<Student> selectElcStudents(Student student);
    List<Student> selectUnElcStudents(Student student);
    List<Student> selectElcInvincibleStds(Student student);
}


