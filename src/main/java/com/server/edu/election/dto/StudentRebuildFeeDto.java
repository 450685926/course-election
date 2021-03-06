package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.RebuildCourseNoChargeType;

import java.util.ArrayList;
import java.util.List;

public class StudentRebuildFeeDto extends ElcCourseTake {
    private static final long serialVersionUID = 1L;
    private String courseName;
    //课程学期
    private Long couCalendarId;
    
    private String teachingClassCode;
    
    private String keyword;
    
    private String faculty;
    
    private String profession;
    
    private String nature;
    
    private Integer mode;
    
    private List<Long> ids;

    /**
     * @Description: 管理部门id
     * @author kan yuanfeng
     * @date 2019/10/15 14:39
     */
    private String manageDptId;

    /**
	 * 重修不收费学生类型
	 */
    private List<RebuildCourseNoChargeType> noPayStudentType = new ArrayList<>();

    private Long abnormalStatuStartTime;

	private Long abnormalStatuEndTime;

	public Long getAbnormalStatuStartTime() {
		return abnormalStatuStartTime;
	}

	public void setAbnormalStatuStartTime(Long abnormalStatuStartTime) {
		this.abnormalStatuStartTime = abnormalStatuStartTime;
	}

	public Long getAbnormalStatuEndTime() {
		return abnormalStatuEndTime;
	}

	public void setAbnormalStatuEndTime(Long abnormalStatuEndTime) {
		this.abnormalStatuEndTime = abnormalStatuEndTime;
	}

	public List<RebuildCourseNoChargeType> getNoPayStudentType() {
		return noPayStudentType;
	}

	public void setNoPayStudentType(List<RebuildCourseNoChargeType> noPayStudentType) {
		this.noPayStudentType = noPayStudentType;
	}

	public String getManageDptId() {
		return manageDptId;
	}

	public void setManageDptId(String manageDptId) {
		this.manageDptId = manageDptId;
	}

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
