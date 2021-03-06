package com.server.edu.election.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

import com.server.edu.election.entity.Student;

public class StudentDto extends Student
{
    private static final long serialVersionUID = 1L;
    
    private String courseCode;

    private Long courseId;
    
    private Long calendarId;
    
    private String studentId;
    
    private List<String> studentIds;
    
    private String projectId;

    private String keyword;

    private List<String> faculties;

    private Long teachingClassId;

    private String inSchool;

    private String graduateStudent;

    private String internationalGraduates;


    public String getInSchool() {
        return inSchool;
    }

    public void setInSchool(String inSchool) {
        this.inSchool = inSchool;
    }

    public String getGraduateStudent() {
        return graduateStudent;
    }

    public void setGraduateStudent(String graduateStudent) {
        this.graduateStudent = graduateStudent;
    }

    public String getInternationalGraduates() {
        return internationalGraduates;
    }

    public void setInternationalGraduates(String internationalGraduates) {
        this.internationalGraduates = internationalGraduates;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<String> getFaculties() {
        return faculties;
    }

    public void setFaculties(List<String> faculties) {
        this.faculties = faculties;
    }
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }
    
    public Long getCalendarId()
    {
        return calendarId;
    }
    
    public void setCalendarId(Long calendarId)
    {
        this.calendarId = calendarId;
    }
    
    public Long getCourseId()
    {
        return courseId;
    }
    
    public void setCourseId(Long courseId)
    {
        this.courseId = courseId;
    }
    
    public String getCourseCode()
    {
        return courseCode;
    }
    
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }
    
    public String getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

	public List<String> getStudentIds() {
		return studentIds;
	}

	public void setStudentIds(List<String> studentIds) {
		this.studentIds = studentIds;
	}


    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }
}
