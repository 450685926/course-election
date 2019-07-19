package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.entity.ElcStudentLimit;

public class ElcStudentLimitVo extends ElcStudentLimit {
    private static final long serialVersionUID = 1L;
    
    private String name;
    
    /**学院*/
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    /**专业*/
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;
    
    /**培养层次*/
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;
    
    /**年级*/
    private String grade;
    
    private String courseCode;
    
    private Long teachingClassId;
    
    private String studentCategory;
    
    private String researchDirection;
    
    private double selectedCredits;
    
    private int selectedRebuild;
    
	public double getSelectedCredits() {
		return selectedCredits;
	}

	public void setSelectedCredits(double selectedCredits) {
		this.selectedCredits = selectedCredits;
	}

	public int getSelectedRebuild() {
		return selectedRebuild;
	}

	public void setSelectedRebuild(int selectedRebuild) {
		this.selectedRebuild = selectedRebuild;
	}

	public String getStudentCategory() {
		return studentCategory;
	}

	public void setStudentCategory(String studentCategory) {
		this.studentCategory = studentCategory;
	}

	public String getResearchDirection() {
		return researchDirection;
	}

	public void setResearchDirection(String researchDirection) {
		this.researchDirection = researchDirection;
	}

	public Long getTeachingClassId() {
		return teachingClassId;
	}

	public void setTeachingClassId(Long teachingClassId) {
		this.teachingClassId = teachingClassId;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}

	public String getGrade() {
		return grade;
	}

	public void setGrade(String grade) {
		this.grade = grade;
	}
    
    

}
