package com.server.edu.election.dao;

import java.util.List;

public interface ScoreStudentScoreDao {
    /**查询未通过课程教学班id*/
    List<String> findFailedTeachingClassId(String studentId);
}
