package com.server.edu.election.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
@Table(name = "student_t")
public class Student implements Serializable
{
    /**
     * 学生号
     */
    @Id
    @Column(name = "STUDENT_CODE_")
    private String studentCode;
    
    /**
     * 姓名
     */
    @Column(name = "NAME_")
    private String name;
    
    /**
     * 性别(根据数据字典统一使用)
     */
    @Code2Text(transformer = "G_XBIE")
    @Column(name = "SEX_")
    private Integer sex;
    
    /**
     * 学习形式(全日制,非全日制,其他)
     */
    @Code2Text(transformer = "G_XXXS")
    @Column(name = "FORM_LEARNING_")
    private String formLearning;
    
    /**
     * 是否留学生 0：否  1：是
     */
    @Column(name = "IS_OVERSEAS_")
    private String isOverseas;
    
    /**
     * 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    @Code2Text(transformer = "X_PYCC")
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;
    
    /**
     * 学院
     */
    @Code2Text(transformer = "X_YX")
    @Column(name = "FACULTY_")
    private String faculty;
    
    /**
     * 专业
     */
    @Code2Text(transformer = "G_ZY")
    @Column(name = "PROFESSION_")
    private String profession;
    
    /**
     * 年级
     */
    @Column(name = "GRADE_")
    private Integer grade;
    
    /**
     * 专项计划
     */
    @Code2Text(transformer = "X_ZXJH")
    @Column(name = "SPCIAL_PLAN_")
    private String spcialPlan;
    
    /**
     * 学籍状态
     */
    @Code2Text(transformer = "G_XJZT")
    @Column(name = "REGISTRATION_STATUS_")
    private String registrationStatus;
    
    /**
     * 入学季节
     */
    @Code2Text(transformer = "X_RXJJ")
    @Column(name = "ENROL_SEASON_")
    private String enrolSeason;
    
    /**
     * 校区
     */
    @Code2Text(transformer = "X_XQ")
    @Column(name = "CAMPUS_")
    private String campus;
    
    /**
     * 培养类别
     */
    @Code2Text(transformer = "X_PYLB")
    @Column(name = "TRAINING_CATEGORY_")
    private String trainingCategory;
    
    /**
     * 学位类别
     */
    @Code2Text(transformer = "X_XWLB")
    @Column(name = "DEGREE_CATEGORY_")
    private String degreeCategory;
    
    /**
     * 研究方向
     */
    @Column(name = "RESEARCH_DIRECTION_")
    private String researchDirection;
    
    /**
     * 学生类别
     */
    @Column(name = "STUDENT_CATEGORY_")
    private String studentCategory;
    
    /**
     * 管理部门id（字典取值）
     */
    @Column(name = "MANAGER_DEPT_ID_")
    private String managerDeptId;
    
    /**
     * 录取类别
     */
    @Column(name = "ENROL_CATEGORY_")
    private String enrolCategory;
    
    /**
     * 学位类型
     */
    @Code2Text(transformer = "X_XWLX")
    @Column(name = "DEGREE_TYPE_")
    private String degreeType;
    
    /**
     * 学位代码
     */
    @Column(name = "DEGREE_CODE_")
    private String degreeCode;
    
    /**
     * 学历代码
     */
    @Column(name = "EDUCATION_CODE_")
    private String educationCode;
    
    /**
     * 录取类别
     */
    @Column(name = "ENROL_METHODS_")
    private String enrolMethods;
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 获取学生号
     *
     * @return STUDENT_CODE_ - 学生号
     */
    public String getStudentCode()
    {
        return studentCode;
    }
    
    /**
     * 设置学生号
     *
     * @param studentCode 学生号
     */
    public void setStudentCode(String studentCode)
    {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }
    
    /**
     * 获取姓名
     *
     * @return NAME_ - 姓名
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name)
    {
        this.name = name == null ? null : name.trim();
    }
    
    /**
     * 获取性别(根据数据字典统一使用)
     *
     * @return SEX_ - 性别(根据数据字典统一使用)
     */
    public Integer getSex()
    {
        return sex;
    }
    
    /**
     * 设置性别(根据数据字典统一使用)
     *
     * @param sex 性别(根据数据字典统一使用)
     */
    public void setSex(Integer sex)
    {
        this.sex = sex;
    }
    
    /**
     * 获取学习形式(全日制,非全日制,其他)
     *
     * @return FORM_LEARNING_ - 学习形式(全日制,非全日制,其他)
     */
    public String getFormLearning()
    {
        return formLearning;
    }
    
    /**
     * 设置学习形式(全日制,非全日制,其他)
     *
     * @param formLearning 学习形式(全日制,非全日制,其他)
     */
    public void setFormLearning(String formLearning)
    {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }
    
    /**
     * 获取是否留学生 0：否  1：是
     *
     * @return IS_OVERSEAS_ - 是否留学生 0：否  1：是
     */
    public String getIsOverseas()
    {
        return isOverseas;
    }
    
    /**
     * 设置是否留学生 0：否  1：是
     *
     * @param isOverseas 是否留学生 0：否  1：是
     */
    public void setIsOverseas(String isOverseas)
    {
        this.isOverseas = isOverseas == null ? null : isOverseas.trim();
    }
    
    /**
     * 获取培养层次(专科   本科   硕士   博士    其他    预科)
     *
     * @return TRAINING_LEVEL_ - 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    /**
     * 设置培养层次(专科   本科   硕士   博士    其他    预科)
     *
     * @param trainingLevel 培养层次(专科   本科   硕士   博士    其他    预科)
     */
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel =
            trainingLevel == null ? null : trainingLevel.trim();
    }
    
    /**
     * 获取学院
     *
     * @return FACULTY_ - 学院
     */
    public String getFaculty()
    {
        return faculty;
    }
    
    /**
     * 设置学院
     *
     * @param faculty 学院
     */
    public void setFaculty(String faculty)
    {
        this.faculty = faculty == null ? null : faculty.trim();
    }
    
    /**
     * 获取专业
     *
     * @return PROFESSION_ - 专业
     */
    public String getProfession()
    {
        return profession;
    }
    
    /**
     * 设置专业
     *
     * @param profession 专业
     */
    public void setProfession(String profession)
    {
        this.profession = profession == null ? null : profession.trim();
    }
    
    /**
     * 获取年级
     *
     * @return GRADE_ - 年级
     */
    public Integer getGrade()
    {
        return grade;
    }
    
    /**
     * 设置年级
     *
     * @param grade 年级
     */
    public void setGrade(Integer grade)
    {
        this.grade = grade;
    }
    
    /**
     * 获取专项计划
     *
     * @return SPCIAL_PLAN_ - 专项计划
     */
    public String getSpcialPlan()
    {
        return spcialPlan;
    }
    
    /**
     * 设置专项计划
     *
     * @param spcialPlan 专项计划
     */
    public void setSpcialPlan(String spcialPlan)
    {
        this.spcialPlan = spcialPlan == null ? null : spcialPlan.trim();
    }
    
    /**
     * 获取学籍状态
     *
     * @return REGISTRATION_STATUS_ - 学籍状态
     */
    public String getRegistrationStatus()
    {
        return registrationStatus;
    }
    
    /**
     * 设置学籍状态
     *
     * @param registrationStatus 学籍状态
     */
    public void setRegistrationStatus(String registrationStatus)
    {
        this.registrationStatus =
            registrationStatus == null ? null : registrationStatus.trim();
    }
    
    /**
     * 获取入学季节
     *
     * @return ENROL_SEASON_ - 入学季节
     */
    public String getEnrolSeason()
    {
        return enrolSeason;
    }
    
    /**
     * 设置入学季节
     *
     * @param enrolSeason 入学季节
     */
    public void setEnrolSeason(String enrolSeason)
    {
        this.enrolSeason = enrolSeason == null ? null : enrolSeason.trim();
    }
    
    /**
     * 获取校区
     *
     * @return CAMPUS_ - 校区
     */
    public String getCampus()
    {
        return campus;
    }
    
    /**
     * 设置校区
     *
     * @param campus 校区
     */
    public void setCampus(String campus)
    {
        this.campus = campus == null ? null : campus.trim();
    }
    
    /**
     * 获取培养类别
     *
     * @return TRAINING_CATEGORY_ - 培养类别
     */
    public String getTrainingCategory()
    {
        return trainingCategory;
    }
    
    /**
     * 设置培养类别
     *
     * @param trainingCategory 培养类别
     */
    public void setTrainingCategory(String trainingCategory)
    {
        this.trainingCategory =
            trainingCategory == null ? null : trainingCategory.trim();
    }
    
    /**
     * 获取学位类别
     *
     * @return DEGREE_CATEGORY_ - 学位类别
     */
    public String getDegreeCategory()
    {
        return degreeCategory;
    }
    
    /**
     * 设置学位类别
     *
     * @param degreeCategory 学位类别
     */
    public void setDegreeCategory(String degreeCategory)
    {
        this.degreeCategory =
            degreeCategory == null ? null : degreeCategory.trim();
    }
    
    /**
     * 获取研究方向
     *
     * @return RESEARCH_DIRECTION_ - 研究方向
     */
    public String getResearchDirection()
    {
        return researchDirection;
    }
    
    /**
     * 设置研究方向
     *
     * @param researchDirection 研究方向
     */
    public void setResearchDirection(String researchDirection)
    {
        this.researchDirection =
            researchDirection == null ? null : researchDirection.trim();
    }
    
    /**
     * 获取学生类别
     *
     * @return STUDENT_CATEGORY_ - 学生类别
     */
    public String getStudentCategory()
    {
        return studentCategory;
    }
    
    /**
     * 设置学生类别
     *
     * @param studentCategory 学生类别
     */
    public void setStudentCategory(String studentCategory)
    {
        this.studentCategory =
            studentCategory == null ? null : studentCategory.trim();
    }
    
    /**
     * 获取管理部门id（字典取值）
     *
     * @return MANAGER_DEPT_ID_ - 管理部门id（字典取值）
     */
    public String getManagerDeptId()
    {
        return managerDeptId;
    }
    
    /**
     * 设置管理部门id（字典取值）
     *
     * @param managerDeptId 管理部门id（字典取值）
     */
    public void setManagerDeptId(String managerDeptId)
    {
        this.managerDeptId =
            managerDeptId == null ? null : managerDeptId.trim();
    }
    
    /**
     * 获取录取类别
     *
     * @return ENROL_CATEGORY_ - 录取类别
     */
    public String getEnrolCategory()
    {
        return enrolCategory;
    }
    
    /**
     * 设置录取类别
     *
     * @param enrolCategory 录取类别
     */
    public void setEnrolCategory(String enrolCategory)
    {
        this.enrolCategory =
            enrolCategory == null ? null : enrolCategory.trim();
    }
    
    /**
     * 获取学位类型
     *
     * @return DEGREE_TYPE_ - 学位类型
     */
    public String getDegreeType()
    {
        return degreeType;
    }
    
    /**
     * 设置学位类型
     *
     * @param degreeType 学位类型
     */
    public void setDegreeType(String degreeType)
    {
        this.degreeType = degreeType == null ? null : degreeType.trim();
    }
    
    /**
     * 获取学位代码
     *
     * @return DEGREE_CODE_ - 学位代码
     */
    public String getDegreeCode()
    {
        return degreeCode;
    }
    
    /**
     * 设置学位代码
     *
     * @param degreeCode 学位代码
     */
    public void setDegreeCode(String degreeCode)
    {
        this.degreeCode = degreeCode == null ? null : degreeCode.trim();
    }
    
    /**
     * 获取学历代码
     *
     * @return EDUCATION_CODE_ - 学历代码
     */
    public String getEducationCode()
    {
        return educationCode;
    }
    
    /**
     * 设置学历代码
     *
     * @param educationCode 学历代码
     */
    public void setEducationCode(String educationCode)
    {
        this.educationCode =
            educationCode == null ? null : educationCode.trim();
    }
    
    /**
     * 获取录取类别
     *
     * @return ENROL_METHODS_ - 录取类别
     */
    public String getEnrolMethods()
    {
        return enrolMethods;
    }
    
    /**
     * 设置录取类别
     *
     * @param enrolMethods 录取类别
     */
    public void setEnrolMethods(String enrolMethods)
    {
        this.enrolMethods = enrolMethods == null ? null : enrolMethods.trim();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", studentCode=").append(studentCode);
        sb.append(", name=").append(name);
        sb.append(", sex=").append(sex);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", isOverseas=").append(isOverseas);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", faculty=").append(faculty);
        sb.append(", profession=").append(profession);
        sb.append(", grade=").append(grade);
        sb.append(", spcialPlan=").append(spcialPlan);
        sb.append(", registrationStatus=").append(registrationStatus);
        sb.append(", enrolSeason=").append(enrolSeason);
        sb.append(", campus=").append(campus);
        sb.append(", trainingCategory=").append(trainingCategory);
        sb.append(", degreeCategory=").append(degreeCategory);
        sb.append(", researchDirection=").append(researchDirection);
        sb.append(", studentCategory=").append(studentCategory);
        sb.append(", managerDeptId=").append(managerDeptId);
        sb.append(", enrolCategory=").append(enrolCategory);
        sb.append(", degreeType=").append(degreeType);
        sb.append(", degreeCode=").append(degreeCode);
        sb.append(", educationCode=").append(educationCode);
        sb.append(", enrolMethods=").append(enrolMethods);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}
