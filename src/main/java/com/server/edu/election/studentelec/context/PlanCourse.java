package com.server.edu.election.studentelec.context;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class PlanCourse extends ElecCourse
{

    /**课程分类*/
	@Code2Text(DictTypeEnum.X_KCFL)
    private Long label;
    
    /**开课学院*/
    private String faculty;

    /**实践周*/
    private Integer weekType;

    /**学期周学时*/
    private String semester;
    /**个人替代课程*/
    private String subCourseCode;

	public String getSubCourseCode() {
		return subCourseCode;
	}

	public void setSubCourseCode(String subCourseCode) {
		this.subCourseCode = subCourseCode;
	}

	public Integer getWeekType() {
        return weekType;
    }

    public void setWeekType(Integer weekType) {
        this.weekType = weekType;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Long getLabel() {
        return label;
    }

    public void setLabel(Long label) {
        this.label = label;
    }

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

}
