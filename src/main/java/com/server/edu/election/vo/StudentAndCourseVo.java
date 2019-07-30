package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.Student;

/**
 * @description: 免修免考返回结果
 * @author: bear
 * @create: 2019-02-02 17:32
 */
@CodeI18n
public class StudentAndCourseVo{

    private Student student;
    
    private String courseName;
    
    private List<ExemptionStudentCourseVo> applyCourse;

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public List<ExemptionStudentCourseVo> getApplyCourse() {
		return applyCourse;
	}

	public void setApplyCourse(List<ExemptionStudentCourseVo> applyCourse) {
		this.applyCourse = applyCourse;
	}
    
}
