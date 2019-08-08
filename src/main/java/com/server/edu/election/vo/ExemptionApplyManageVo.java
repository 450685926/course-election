package com.server.edu.election.vo;

import javax.persistence.Column;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ExemptionApplyManage;

/**
 * @description: 免修免考返回结果
 * @author: bear
 * @create: 2019-02-02 17:32
 */
@CodeI18n
public class ExemptionApplyManageVo extends ExemptionApplyManage{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;

    @Code2Text(transformer="G_ZY")
    private String profession;
    
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;

    private String calendarName;

    private String examineResultStr;
    
    private String applyCourse;
    
    private String studentId;

    private String studentName;
    
    @Code2Text(transformer = "X_SHZT")
    private String result;

    public String getExamineResultStr() {
        return examineResultStr;
    }

    public void setExamineResultStr(String examineResultStr) {
        this.examineResultStr = examineResultStr;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
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

	public String getApplyCourse() {
		return applyCourse;
	}

	public void setApplyCourse(String applyCourse) {
		this.applyCourse = applyCourse;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public String getStudentName() {
		return studentName;
	}

	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
    
}
