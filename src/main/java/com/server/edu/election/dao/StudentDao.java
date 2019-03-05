package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.Student;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface StudentDao extends Mapper<Student> {
    //通过学号获取学生信息
    Student findStudentByCode(String studentCode);
    List<Student> selectElcStudents(Student student);
    List<Student> selectUnElcStudents(Student student);
    List<Student> selectElcInvincibleStds(Student student);
    List<Student> selectUnElcInvincibleStds(Student student);


    /**根据轮次查询学生信息*/
    Student findStuRound(@Param("roundId") Long roundId, @Param("studentId")String studentId);
}


