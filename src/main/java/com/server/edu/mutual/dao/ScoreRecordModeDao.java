package com.server.edu.mutual.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.server.edu.mutual.vo.Scoresetting;


public interface ScoreRecordModeDao {
	
	//查询成绩设置
	List<Scoresetting> queryRecordMode();
	//按课程获取部门id
    String findPidByCourseCode(@Param("courseCode")String courseCode, @Param("calendarId")String calendarId);

}
