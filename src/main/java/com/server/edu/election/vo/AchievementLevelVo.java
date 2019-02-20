package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.entity.AchievementLevel;
import com.server.edu.election.entity.Course;

public class AchievementLevelVo extends AchievementLevel {
    private static final long serialVersionUID = 1L;
    private String courses;
    private List<Course> list;
    
	public List<Course> getList() {
		return list;
	}
	public void setList(List<Course> list) {
		this.list = list;
	}
	public String getCourses() {
		return courses;
	}
	public void setCourses(String courses) {
		this.courses = courses;
	}
    
}
