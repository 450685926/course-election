package com.server.edu.election.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ScoreStudentScoreDao {
    /**查询成功通过的课程代码*/
    List<String> findSuccessedCourseCode(String studentId);

    /**查询成功通过的课程教学班id*/
    List<String> findFailedTeachingClassId(@Param("list") List<String> list, @Param("studentId") String studentId);
}
