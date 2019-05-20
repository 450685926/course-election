package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.Student;

import tk.mybatis.mapper.common.Mapper;

public interface StudentDao extends Mapper<Student> {
    //通过学号获取学生信息
    Student findStudentByCode(String studentCode);
    List<Student> selectElcStudents(StudentDto student);
    List<Student> selectUnElcStudents(StudentDto student);
    List<Student> selectElcInvincibleStds(Student student);
    List<Student> selectUnElcInvincibleStds(Student student);


    /**根据轮次查询学生信息*/
    Student findStuRound(@Param("roundId") Long roundId, @Param("studentId")String studentId);

    /**是否是预警学生*/
    Student isLoserStu(@Param("roundId") Long roundId, @Param("studentId")String studentId);
}


