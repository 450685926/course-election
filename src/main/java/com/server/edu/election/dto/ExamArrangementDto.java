package com.server.edu.election.dto;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import com.server.edu.election.entity.ExamArrangement;

public class ExamArrangementDto extends ExamArrangement {
    private static final long serialVersionUID = 1L;
    /**
     * 年级
     */
    @NotNull
    @Column(name = "GRADE_")
    private Integer grade;

    /**
     * 培养层次X_PYCC
     */
    @NotBlank
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学习形式X_XXXS
     */
    @NotBlank
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 学院
     */
    @NotBlank
    @Column(name = "FACULTY_")
    private String faculty;

    /**
     * 专业（编码）
     */
    @Column(name = "PROFESSION_")
    private String profession;

	public Integer getGrade() {
		return grade;
	}

	public void setGrade(Integer grade) {
		this.grade = grade;
	}

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
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
