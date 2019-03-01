package com.server.edu.election.studentelec.cache;

public class StudentInfoCache
{
    private String studentId;
    
    private String studentName;
    
    private Integer sex;
    
    /**校区*/
    private String campus;
    
    /**专业*/
    private String major;
    
    /**年级*/
    private Integer grade;
    
    private String trainingLevel;
    
    private String englishLevel;
    
    /** 是否留学生 */
    private boolean isAboard;
    
    /** 是否缴费 */
    private boolean isPaid;
    
    /** 是否留级降级 */
    private boolean isRepeater;
    
    /** 是否结业生 */
    private boolean isGraduate;
    
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
    
    public Integer getSex()
    {
        return sex;
    }
    
    public void setSex(Integer sex)
    {
        this.sex = sex;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public String getMajor()
    {
        return major;
    }
    
    public void setMajor(String major)
    {
        this.major = major;
    }
    
    public Integer getGrade()
    {
        return grade;
    }
    
    public void setGrade(Integer grade)
    {
        this.grade = grade;
    }
    
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
    }
    
    public String getEnglishLevel()
    {
        return englishLevel;
    }
    
    public void setEnglishLevel(String englishLevel)
    {
        this.englishLevel = englishLevel;
    }
    
    public boolean isAboard()
    {
        return isAboard;
    }
    
    public void setAboard(boolean aboard)
    {
        isAboard = aboard;
    }
    
    public boolean isPaid()
    {
        return isPaid;
    }
    
    public void setPaid(boolean paid)
    {
        isPaid = paid;
    }
    
    public boolean isRepeater()
    {
        return isRepeater;
    }
    
    public void setRepeater(boolean repeater)
    {
        isRepeater = repeater;
    }

	public boolean isGraduate() {
		return isGraduate;
	}

	public void setGraduate(boolean isGraduate) {
		this.isGraduate = isGraduate;
	}
    
    
}
