package com.server.edu.election.vo;

import com.server.edu.election.entity.CourseOpen;

public class CourseOpenVo extends CourseOpen {
    private static final long serialVersionUID = 1L;

    //选课建议课表开启状态
    private Integer suggestStatus;
    

	public Integer getSuggestStatus() {
		return suggestStatus;
	}

	public void setSuggestStatus(Integer suggestStatus) {
		this.suggestStatus = suggestStatus;
	}
}
