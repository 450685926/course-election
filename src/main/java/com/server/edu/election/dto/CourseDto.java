package com.server.edu.election.dto;

import com.server.edu.election.entity.Course;

public class CourseDto extends Course {
    /**
     * 模式：1正常，2英语 3体育
     */
    private Integer mode;
    
    private String projectId;
    
    
    
    public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public Integer getMode() {
		return mode;
	}

	public void setMode(Integer mode) {
		this.mode = mode;
	}

	private static final long serialVersionUID = 1L;
}
