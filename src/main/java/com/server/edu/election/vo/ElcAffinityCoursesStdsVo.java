package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ElcAffinityCoursesStds;

@CodeI18n
public class ElcAffinityCoursesStdsVo extends ElcAffinityCoursesStds
{
    private static final long serialVersionUID = 1L;
    
    private String studentName;
    
    @Code2Text(transformer = "X_PYCC")
    private String trainingLevel;
    
    /**
     * 专业
     */
    @Code2Text(transformer = "G_ZY")
    private String profession;
    
    /**
     * 学生类别
     */
    private String studentCategory;
    
    /**
     * 研究方向
     */
    private String researchDirection;
    
    private String courseCode;
    
    private String courseName;
    
    public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getCourseCode() {
		return courseCode;
	}

	public void setCourseCode(String courseCode) {
		this.courseCode = courseCode;
	}

	public String getStudentName()
    {
        return studentName;
    }
    
    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }
    
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
    }
    
    public String getProfession()
    {
        return profession;
    }
    
    public void setProfession(String profession)
    {
        this.profession = profession;
    }
    
    public String getStudentCategory()
    {
        return studentCategory;
    }
    
    public void setStudentCategory(String studentCategory)
    {
        this.studentCategory = studentCategory;
    }
    
    public String getResearchDirection()
    {
        return researchDirection;
    }
    
    public void setResearchDirection(String researchDirection)
    {
        this.researchDirection = researchDirection;
    }
    
}
