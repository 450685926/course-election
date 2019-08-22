package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "first_language_contrast_t_v")
public class FirstLanguageContrast implements Serializable
{
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    private Long id;
    
    @Column(name = "LANGUAGE_CODE_")
    private String languageCode;
    
    @Column(name = "LANGUAGE_NAME_")
    private String languageName;
    
    @Column(name = "COURSE_CODE_")
    private String courseCode;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLanguageName() {
		return languageName;
	}

	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}
    
}