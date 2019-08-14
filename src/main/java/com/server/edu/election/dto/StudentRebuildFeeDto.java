package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcCourseTake;

public class StudentRebuildFeeDto extends ElcCourseTake {
    private static final long serialVersionUID = 1L;
    private String courseCode;
    private String courseName;
    //是否缴费
    private Integer paId;
    //课程学期
    private Long couCalendarId;
    
    private String teachingClassCode;
    
    private String keyCode;
    
    private String faculty;
    
    private String profession;
    
    private String nature;
    
    private Integer mode;
    
	@Override
	public Integer getMode() {
		return mode;
	}
	@Override
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public Long getCouCalendarId() {
		return couCalendarId;
	}
	public void setCouCalendarId(Long couCalendarId) {
		this.couCalendarId = couCalendarId;
	}
	public String getTeachingClassCode() {
		return teachingClassCode;
	}
	public void setTeachingClassCode(String teachingClassCode) {
		this.teachingClassCode = teachingClassCode;
	}
	public String getKeyCode() {
		return keyCode;
	}
	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}
	@Override
	public String getCourseCode() {
		return courseCode;
	}
	@Override
	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getNature() {
		return nature;
	}
	public void setNature(String nature) {
		this.nature = nature;
	}
	public Integer getPaId() {
		return paId;
	}
	public void setPaId(Integer paId) {
		this.paId = paId;
	}
	public String getFaculty() {
		return faculty;
	}
	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	
	
    
    
}
