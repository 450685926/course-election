package com.server.edu.election.dto;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * 选课服务冗余的学生信息
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月2日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@CodeI18n
public class Student4Elc
{
    
    private String studentId;
    
    private String name;
    
    @Code2Text(transformer="G_XBIE")
    private Integer sex;
    
    /**学院*/
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    
    /**专业*/
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;
    
    /**培养层次*/
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;
    
    /**是否留学生*/
    private String isOverseas;
    
    /**年级*/
    private String grade;
    
    /**学籍状态*/
    @Code2Text(transformer = "X_PYCC")
    private String registrationStatus;
    
    /**注册状态*/
    private String registerStatus;
    
    /**入学季节*/
    private String enrolSeason;
    
    /**是否已缴费*/
    private String paymentStatus;
    
    /**培养类别*/
    private String trainingCategory;
    
    /**学位类型*/
    @Code2Text(transformer = "X_XWLX")
    private String degreeType;
    
    /**学位类别*/
    @Code2Text(transformer = "X_XWLB")
    private String degreeCategory;
    
    /**学习形式*/
    @Code2Text(transformer = "X_XXXS")
    private String formLearning;
    
    /**专项计划*/
    private String spcialPlan;
    
    /**入学时间*/
    private Long enrolDate;
    
    /**在读状态*/
    private String leaveSchool;
    
    /**校区*/
    @Code2Text(transformer = "X_XQ")
    private String campus;
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public String getLeaveSchool()
    {
        return leaveSchool;
    }
    
    public void setLeaveSchool(String leaveSchool)
    {
        this.leaveSchool = leaveSchool;
    }
    
    public Long getEnrolDate()
    {
        return enrolDate;
    }
    
    public void setEnrolDate(Long enrolDate)
    {
        this.enrolDate = enrolDate;
    }
    
    public String getPaymentStatus()
    {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus)
    {
        this.paymentStatus = paymentStatus;
    }
    
    public String getTrainingCategory()
    {
        return trainingCategory;
    }
    
    public void setTrainingCategory(String trainingCategory)
    {
        this.trainingCategory = trainingCategory;
    }
    
    public String getDegreeType()
    {
        return degreeType;
    }
    
    public void setDegreeType(String degreeType)
    {
        this.degreeType = degreeType;
    }
    
    public String getDegreeCategory()
    {
        return degreeCategory;
    }
    
    public void setDegreeCategory(String degreeCategory)
    {
        this.degreeCategory = degreeCategory;
    }
    
    public String getFormLearning()
    {
        return formLearning;
    }
    
    public void setFormLearning(String formLearning)
    {
        this.formLearning = formLearning;
    }
    
    public String getSpcialPlan()
    {
        return spcialPlan;
    }
    
    public void setSpcialPlan(String spcialPlan)
    {
        this.spcialPlan = spcialPlan;
    }
    
    public String getRegistrationStatus()
    {
        return registrationStatus;
    }
    
    public void setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus = registrationStatus;
    }
    
    public String getEnrolSeason()
    {
        return enrolSeason;
    }
    
    public void setEnrolSeason(String enrolSeason)
    {
        this.enrolSeason = enrolSeason;
    }
    
    public String getRegisterStatus()
    {
        return registerStatus;
    }
    
    public void setRegisterStatus(String registerStatus)
    {
        this.registerStatus = registerStatus;
    }
    
    public String getStudentId()
    {
        return studentId;
    }
    
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }
    
    public String getName()
    {
        return name;
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public Integer getSex()
    {
        return sex;
    }
    
    public void setSex(Integer sex)
    {
        this.sex = sex;
    }
    
    public String getFaculty()
    {
        return faculty;
    }
    
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }
    
    public String getProfession()
    {
        return profession;
    }
    
    public void setProfession(String profession)
    {
        this.profession = profession;
    }
    
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
    }
    
    public String getGrade()
    {
        return grade;
    }
    
    public void setGrade(String grade)
    {
        this.grade = grade;
    }
    
    public String getIsOverseas()
    {
        return isOverseas;
    }
    
    public void setIsOverseas(String isOverseas)
    {
        this.isOverseas = isOverseas;
    }
    
}
