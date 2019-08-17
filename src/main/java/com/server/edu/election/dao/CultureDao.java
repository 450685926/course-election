package com.server.edu.election.dao;

import java.util.List;


public interface CultureDao {

    List<String>  getStudentScoreByStudentId(String studentId);
}
