package com.server.edu.election.studentelec.cache;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * 学生信息缓存对象
 */
@CodeI18n
public class StudentInfoCache
{
    private String studentId;
    
    private String studentName;
    
    @Code2Text(DictTypeEnum.G_XBIE)
    private Integer sex;
    
    /**校区*/
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    
    /**学院*/
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    /**专业*/
    @Code2Text(DictTypeEnum.G_ZY)
    private String major;
    
    /**年级*/
    private Integer grade;
    
    @Code2Text(DictTypeEnum.X_PYCC)
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

    /**专项计划*/
    @Code2Text(DictTypeEnum.X_ZXJH)
    private String spcialPlan;
    
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
    
    public boolean isGraduate()
    {
        return isGraduate;
    }
    
    public void setGraduate(boolean isGraduate)
    {
        this.isGraduate = isGraduate;
    }

    public String getSpcialPlan() {
        return spcialPlan;
    }

    public void setSpcialPlan(String spcialPlan) {
        this.spcialPlan = spcialPlan;
    }
}
