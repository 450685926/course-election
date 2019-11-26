package com.server.edu.election.dao;

import com.server.edu.election.entity.TeachingClassSuggestStudent;

import java.util.List;

public interface TeachingClassSuggestStudentDao {

    int insertSelective(TeachingClassSuggestStudent record);

    void deleteByClassId(Long classId);
}