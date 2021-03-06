package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.util.List;

/**
 * @description: 未选课学生
 * @author: bear
 * @create: 2019-05-22 17:14
 */
@CodeI18n
public class NoSelectCourseStdsDto{
    /** 学年学期 */
	private Long calendarId;
	
	/** 年级 */
    private Integer grade;
    
    /** 学院 */
    @Code2Text(transformer = "X_YX")
    private String faculty;
    
    /** 专业 */
    @Code2Text(transformer = "G_ZY")
    private String major;
    
    /** 学生类别 */
    private String studentCategory;
    
    /** 学生学号 */
    private String studentCode;
    /** 学生学号list */
    private List<String> ListStudentCode;
    
    /** 学生姓名 */
    private String studentName;

    /** 学籍变动信息 */
    @Code2Text(transformer = "G_XJDL")
    private String stdStatusChanges;

    /** 未选课原因 */
    private String noSelectReason;

    /** 用户管理部门ID(MANAGER_DEPT_ID) */
    private String deptId;
    
    /** 学生姓名或者学号 */
    private String keyword;
    
    /** 培养层次 */
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    /** 培养类别 */
    @Code2Text(transformer = "X_PYLB")
    private String trainingCategory;

    /** 入学方式 */
    @Code2Text(transformer = "X_RXFS")
    private String enrolMethods;

    /** 专项计划 */
    @Code2Text(transformer = "X_ZXJH")
    private String specialPlan;

    /** 是否国际学生 */
    private String isOverseas;
    
    /**  学位类型 */
    @Code2Text(transformer = "X_XWLX")
    private String degreeType;
    
    /** 学习形式 */
    @Code2Text(transformer = "X_XXXS")
    private String formLearning;

    /**新老系统不一样，新系统有四种途径*/
    private String studentType;
    
    /** 用户角色 */
    private String role;
    
    /** 登录人ID */
    private String uId;

    private List<String> faculties;

    /*部门id*/
    private String projId;

    public String getProjId() {
        return projId;
    }

    public void setProjId(String projId) {
        this.projId = projId;
    }

    public List<String> getFaculties() {
        return faculties;
    }

    public void setFaculties(List<String> faculties) {
        this.faculties = faculties;
    }

    public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getStudentCategory() {
        return studentCategory;
    }

    public void setStudentCategory(String studentCategory) {
        this.studentCategory = studentCategory;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStdStatusChanges() {
        return stdStatusChanges;
    }

    public void setStdStatusChanges(String stdStatusChanges) {
        this.stdStatusChanges = stdStatusChanges;
    }

    public String getNoSelectReason() {
        return noSelectReason;
    }

    public void setNoSelectReason(String noSelectReason) {
        this.noSelectReason = noSelectReason;
    }

	public String getTrainingLevel() {
		return trainingLevel;
	}

	public void setTrainingLevel(String trainingLevel) {
		this.trainingLevel = trainingLevel;
	}

	public String getTrainingCategory() {
		return trainingCategory;
	}

	public void setTrainingCategory(String trainingCategory) {
		this.trainingCategory = trainingCategory;
	}

	public String getDegreeType() {
		return degreeType;
	}

	public void setDegreeType(String degreeType) {
		this.degreeType = degreeType;
	}

	public String getFormLearning() {
		return formLearning;
	}

	public void setFormLearning(String formLearning) {
		this.formLearning = formLearning;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

    public List<String> getListStudentCode() {
        return ListStudentCode;
    }

    public void setListStudentCode(List<String> listStudentCode) {
        ListStudentCode = listStudentCode;
    }

    public String getEnrolMethods() {
        return enrolMethods;
    }

    public void setEnrolMethods(String enrolMethods) {
        this.enrolMethods = enrolMethods;
    }

    public String getSpecialPlan() {
        return specialPlan;
    }

    public void setSpecialPlan(String specialPlan) {
        this.specialPlan = specialPlan;
    }

    public String getIsOverseas() {
        return isOverseas;
    }

    public void setIsOverseas(String isOverseas) {
        this.isOverseas = isOverseas;
    }

    public String getStudentType() {
        return studentType;
    }

    public void setStudentType(String studentType) {
        this.studentType = studentType;
    }

    @Override
	public String toString() {
		return "NoSelectCourseStdsDto [calendarId=" + calendarId + ", grade=" + grade + ", faculty=" + faculty
				+ ", major=" + major + ", studentCategory=" + studentCategory + ", studentCode=" + studentCode
				+ ", studentName=" + studentName + ", stdStatusChanges=" + stdStatusChanges + ", noSelectReason="
				+ noSelectReason + ", deptId=" + deptId + ", keyword=" + keyword + ", trainingLevel=" + trainingLevel
				+ ", trainingCategory=" + trainingCategory + ", degreeType=" + degreeType + ", formLearning="
				+ formLearning + ", role=" + role + ", uId=" + uId + "]";
	}
    
}
