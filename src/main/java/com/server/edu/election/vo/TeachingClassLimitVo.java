package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.entity.TeachingClassElectiveRestrictProfession;
import com.server.edu.election.entity.TeachingClassSuggestStudent;

import java.util.List;

@CodeI18n
public class TeachingClassLimitVo
{

	/**
	 * 选课限制专业
	 */
	private List<TeachingClassElectiveRestrictProfession> lstElectiveProf;

	/**
	 * 选课限制
	 */
	private TeachingClassElectiveRestrictAttr electiveRestrictAttr;

	/**
	 * 选课专业限制学生
	 */
	private List<TeachingClassSuggestStudent> lstSuggestStud;

	private List<String> student;

	private List<SuggestProfessionDto> electiveProf;

	public List<String> getStudent() {
		return student;
	}

	public void setStudent(List<String> student) {
		this.student = student;
	}

	public List<SuggestProfessionDto> getElectiveProf() {
		return electiveProf;
	}

	public void setElectiveProf(List<SuggestProfessionDto> electiveProf) {
		this.electiveProf = electiveProf;
	}

	public List<TeachingClassSuggestStudent> getLstSuggestStud() {
		return lstSuggestStud;
	}

	public void setLstSuggestStud(List<TeachingClassSuggestStudent> lstSuggestStud) {
		this.lstSuggestStud = lstSuggestStud;
	}

	public List<TeachingClassElectiveRestrictProfession> getLstElectiveProf() {
		return lstElectiveProf;
	}

	public void setLstElectiveProf(List<TeachingClassElectiveRestrictProfession> lstElectiveProf) {
		this.lstElectiveProf = lstElectiveProf;
	}

	public TeachingClassElectiveRestrictAttr getElectiveRestrictAttr() {
		return electiveRestrictAttr;
	}

	public void setElectiveRestrictAttr(TeachingClassElectiveRestrictAttr electiveRestrictAttr) {
		this.electiveRestrictAttr = electiveRestrictAttr;
	}
}
