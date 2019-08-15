package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.election.entity.ElcCourseTake;

public class StudentRebuildFeeDto extends ElcCourseTake {
    private static final long serialVersionUID = 1L;
    private String courseName;
    //是否缴费
    private Integer paId;
    //课程学期
    private Long couCalendarId;
    
    private String teachingClassCode;
    
    private String keyword;
    
    private String faculty;
    
    private String profession;
    
    private String nature;
    
    private Integer mode;
    
    private List<Long> ids;
    
    
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
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
	
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
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
