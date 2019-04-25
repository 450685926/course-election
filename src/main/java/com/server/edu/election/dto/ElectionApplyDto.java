package com.server.edu.election.dto;

import com.server.edu.election.entity.ElectionApply;

public class ElectionApplyDto extends  ElectionApply{
    private static final long serialVersionUID = 1L;
    private Integer projectId;
	public Integer getProjectId() {
		return projectId;
	}
	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}
    
}
