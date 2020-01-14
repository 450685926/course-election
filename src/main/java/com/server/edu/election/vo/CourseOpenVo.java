package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.CourseOpen;

@CodeI18n
public class CourseOpenVo extends CourseOpen {
    private static final long serialVersionUID = 1L;

    //选课建议课表开启状态
    private Integer suggestStatus;
    
    private Long courseId;

    private Long teachingClassId;
    
    
    

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public Integer getSuggestStatus() {
		return suggestStatus;
	}

	public void setSuggestStatus(Integer suggestStatus) {
		this.suggestStatus = suggestStatus;
	}

	public Long getTeachingClassId() {
		return teachingClassId;
	}

	public void setTeachingClassId(Long teachingClassId) {
		this.teachingClassId = teachingClassId;
	}
}
