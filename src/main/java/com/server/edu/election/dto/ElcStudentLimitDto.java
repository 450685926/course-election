package com.server.edu.election.dto;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.entity.ElcStudentLimit;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

public class ElcStudentLimitDto extends ElcStudentLimit {
	private static final long serialVersionUID = 1L;
	@NotEmpty
	private List<String> studentCodes;
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
    
    private double minLimitCredits;
    
    private double maxLimitCredits;
    
    private double minSelectedCredits;
    
    private double maxSelectedCredits;
    
    private int minRebuild;
    
    private int maxRebuild;
    
    private int minSelectedRebuild;
    
    private int maxSelectedRebuild;
    
    private double selectedCredits;
    
    private int selectedRebuild;
    
    private List<Long> ids;
    
    private Integer mode;

	/**分库分表标识*/
	private Integer index;

	private List<String> faculties;

	public List<String> getFaculties() {
		return faculties;
	}

	public void setFaculties(List<String> faculties) {
		this.faculties = faculties;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	public Integer getMode() {
		return mode;
	}
	public void setMode(Integer mode) {
		this.mode = mode;
	}
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
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
	public int getMinRebuild() {
		return minRebuild;
	}
	public void setMinRebuild(int minRebuild) {
		this.minRebuild = minRebuild;
	}
	public int getMaxRebuild() {
		return maxRebuild;
	}
	public void setMaxRebuild(int maxRebuild) {
		this.maxRebuild = maxRebuild;
	}
	public int getMinSelectedRebuild() {
		return minSelectedRebuild;
	}
	public void setMinSelectedRebuild(int minSelectedRebuild) {
		this.minSelectedRebuild = minSelectedRebuild;
	}
	public int getMaxSelectedRebuild() {
		return maxSelectedRebuild;
	}
	public void setMaxSelectedRebuild(int maxSelectedRebuild) {
		this.maxSelectedRebuild = maxSelectedRebuild;
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
	public double getMinLimitCredits() {
		return minLimitCredits;
	}
	public void setMinLimitCredits(double minLimitCredits) {
		this.minLimitCredits = minLimitCredits;
	}
	public double getMaxLimitCredits() {
		return maxLimitCredits;
	}
	public void setMaxLimitCredits(double maxLimitCredits) {
		this.maxLimitCredits = maxLimitCredits;
	}
	public double getMinSelectedCredits() {
		return minSelectedCredits;
	}
	public void setMinSelectedCredits(double minSelectedCredits) {
		this.minSelectedCredits = minSelectedCredits;
	}
	public double getMaxSelectedCredits() {
		return maxSelectedCredits;
	}
	public void setMaxSelectedCredits(double maxSelectedCredits) {
		this.maxSelectedCredits = maxSelectedCredits;
	}
	public List<String> getStudentCodes() {
		return studentCodes;
	}
	public void setStudentCodes(List<String> studentCodes) {
		this.studentCodes = studentCodes;
	}
	
}
