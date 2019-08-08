package com.server.edu.election.studentelec.context;

public class ReplaceCourse {
    /**
     * 原课程代码
     */
    private String origsCourseId;
    /**
     * 原课程名称
     */
    private String origsCourseName;
    
    /**
     * 替代课程代码
     */
    private String subCourseId;
    /**
     *替代课程名称
     */
    private String subCourseName;
	public String getOrigsCourseId() {
		return origsCourseId;
	}
	public void setOrigsCourseId(String origsCourseId) {
		this.origsCourseId = origsCourseId;
	}
	public String getOrigsCourseName() {
		return origsCourseName;
	}
	public void setOrigsCourseName(String origsCourseName) {
		this.origsCourseName = origsCourseName;
	}
	public String getSubCourseId() {
		return subCourseId;
	}
	public void setSubCourseId(String subCourseId) {
		this.subCourseId = subCourseId;
	}
	public String getSubCourseName() {
		return subCourseName;
	}
	public void setSubCourseName(String subCourseName) {
		this.subCourseName = subCourseName;
	}
    
    
    
}
