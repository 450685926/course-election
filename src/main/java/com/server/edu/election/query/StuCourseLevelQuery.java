package com.server.edu.election.query;

/**
 * 学生课程能力
 *
 */
public class StuCourseLevelQuery
{
    /** 学号 */
    private String studentId;
    
    /** 姓名 */
    private String studentName;
    
    /**培养层次*/
    private String trainingLevel;
    
    /** 学院 */
    private String faculty;
    
    /** 专业 */
    private String major;
    
    /** 课程等级分类ID*/
    private Long courseCategoryId;
    
    private String keyword;
    
    private String projectId;
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
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
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getMajor()
    {
        return major;
    }
    
    public void setMajor(String major)
    {
        this.major = major;
    }
    
    public Long getCourseCategoryId()
    {
        return courseCategoryId;
    }
    
    public void setCourseCategoryId(Long courseCategoryId)
    {
        this.courseCategoryId = courseCategoryId;
    }
    
    public String getKeyword()
    {
        return keyword;
    }
    
    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
    public String getProjectId()
    {
        return projectId;
    }
    
    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }
    
}
