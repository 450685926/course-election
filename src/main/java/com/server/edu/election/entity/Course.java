package com.server.edu.election.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
@CodeI18n
@Table(name = "course_t")
public class Course implements Serializable
{
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 课程代码
     */
    @Column(name = "CODE_")
    private String code;
    
    /**
     * 学分
     */
    @Column(name = "CREDITS_")
    private Double credits;
    
    /**
     * 课程使用状态(1是0否)
     */
    @Column(name = "ENABLED_")
    private Integer enabled;
    
    /**
     * 名称
     */
    @Column(name = "NAME_")
    private String name;
    
    /**
     * 英文名称
     */
    @Column(name = "NAME_EN_")
    private String nameEn;
    
    /**
     * 周数
     */
    @Column(name = "WEEKS_")
    private Integer weeks;
    
    /**
     * 周课时
     */
    @Column(name = "WEEK_HOUR_")
    private Double weekHour;
    
    /**
     * 课时
     */
    @Column(name = "PERIOD_")
    private Double period;
    
    /**
     * 是否跨学期(1是0否)
     */
    @Column(name = "CROSS_TERM_")
    private Integer crossTerm;
    
    /**
     * 开课学期（字典取值）
     */
    @Column(name = "TERM_")
    private String term;
    
    /**
     * 培养层次
     */
    @Code2Text(transformer = "X_PYCC")
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;
    
    /**
     * 学习形式(显示名称为课程类型)
     */
    @Column(name = "FORM_LEARNING_")
    private String formLearning;
    
    /**
     * 是否为公共选修课(1:是，0：否)
     */
    @Code2Text(transformer="K_BKKCXZ")
    @Column(name = "IS_ELECTIVE_")
    private Integer isElective;
    
    /**
     * 所属学院
     */
    @Code2Text(transformer="X_YX")
    @Column(name = "COLLEGE_")
    private String college;
    
    /**
     * 课程性质(字典取值X_KCXZ)
     */
    @Column(name = "NATURE_")
    private String nature;
    
    /**
     * 课程分类（研究生，字典取值X_KCFL）
     */
    @Column(name = "LABEL_")
    private String label;
    
    /**
     * 授课语言
     */
    @Column(name = "TEACHING_LANGUAGE_")
    private String teachingLanguage;
    
    /**
     * 成绩记录方式（从字典取值 X_CJJL）
     */
    @Column(name = "SCORE_TYPE_")
    private String scoreType;
    
    /**
     * 考核方式（从字典取值 X_KSLX）
     */
    @Column(name = "ASSESSMENT_MODE_")
    private String assessmentMode;
    
    /**
     * 课程负责人
     */
    @Column(name = "HEAD_TEACHER_")
    private String headTeacher;
    
    /**
     * 其他讲课人
     */
    @Column(name = "TEACHERS_")
    private String teachers;
    
    /**
     * 管理部门id（字典取值）
     */
    @Column(name = "MANAGER_DEPT_ID_")
    private String managerDeptId;
    
    /**
     * 状态(1待提交，2待审核，3通过，0禁用)
     */
    @Column(name = "STATUS_")
    private String status;
    
    /**
     * 更新时间
     */
    @Column(name = "UPDATE_TIME_")
    private Date updateTime;
    
    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;
    
    /**
     * 教学方式（教学形式）字典取值
     */
    @Column(name = "TEACH_MODE_")
    private String teachMode;
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 获取主键
     *
     * @return ID_ - 主键
     */
    public Long getId()
    {
        return id;
    }
    
    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id)
    {
        this.id = id;
    }
    
    /**
     * 获取课程代码
     *
     * @return CODE_ - 课程代码
     */
    public String getCode()
    {
        return code;
    }
    
    /**
     * 设置课程代码
     *
     * @param code 课程代码
     */
    public void setCode(String code)
    {
        this.code = code == null ? null : code.trim();
    }
    
    /**
     * 获取学分
     *
     * @return CREDITS_ - 学分
     */
    public Double getCredits()
    {
        return credits;
    }
    
    /**
     * 设置学分
     *
     * @param credits 学分
     */
    public void setCredits(Double credits)
    {
        this.credits = credits;
    }
    
    /**
     * 获取课程使用状态(1是0否)
     *
     * @return ENABLED_ - 课程使用状态(1是0否)
     */
    public Integer getEnabled()
    {
        return enabled;
    }
    
    /**
     * 设置课程使用状态(1是0否)
     *
     * @param enabled 课程使用状态(1是0否)
     */
    public void setEnabled(Integer enabled)
    {
        this.enabled = enabled;
    }
    
    /**
     * 获取名称
     *
     * @return NAME_ - 名称
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name)
    {
        this.name = name == null ? null : name.trim();
    }
    
    /**
     * 获取英文名称
     *
     * @return NAME_EN_ - 英文名称
     */
    public String getNameEn()
    {
        return nameEn;
    }
    
    /**
     * 设置英文名称
     *
     * @param nameEn 英文名称
     */
    public void setNameEn(String nameEn)
    {
        this.nameEn = nameEn == null ? null : nameEn.trim();
    }
    
    /**
     * 获取周数
     *
     * @return WEEKS_ - 周数
     */
    public Integer getWeeks()
    {
        return weeks;
    }
    
    /**
     * 设置周数
     *
     * @param weeks 周数
     */
    public void setWeeks(Integer weeks)
    {
        this.weeks = weeks;
    }
    
    /**
     * 获取周课时
     *
     * @return WEEK_HOUR_ - 周课时
     */
    public Double getWeekHour()
    {
        return weekHour;
    }
    
    /**
     * 设置周课时
     *
     * @param weekHour 周课时
     */
    public void setWeekHour(Double weekHour)
    {
        this.weekHour = weekHour;
    }
    
    /**
     * 获取课时
     *
     * @return PERIOD_ - 课时
     */
    public Double getPeriod()
    {
        return period;
    }
    
    /**
     * 设置课时
     *
     * @param period 课时
     */
    public void setPeriod(Double period)
    {
        this.period = period;
    }
    
    /**
     * 获取是否跨学期(1是0否)
     *
     * @return CROSS_TERM_ - 是否跨学期(1是0否)
     */
    public Integer getCrossTerm()
    {
        return crossTerm;
    }
    
    /**
     * 设置是否跨学期(1是0否)
     *
     * @param crossTerm 是否跨学期(1是0否)
     */
    public void setCrossTerm(Integer crossTerm)
    {
        this.crossTerm = crossTerm;
    }
    
    /**
     * 获取开课学期（字典取值）
     *
     * @return TERM_ - 开课学期（字典取值）
     */
    public String getTerm()
    {
        return term;
    }
    
    /**
     * 设置开课学期（字典取值）
     *
     * @param term 开课学期（字典取值）
     */
    public void setTerm(String term)
    {
        this.term = term == null ? null : term.trim();
    }
    
    /**
     * 获取培养层次
     *
     * @return TRAINING_LEVEL_ - 培养层次
     */
    public String getTrainingLevel()
    {
        return trainingLevel;
    }
    
    /**
     * 设置培养层次
     *
     * @param trainingLevel 培养层次
     */
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel =
            trainingLevel == null ? null : trainingLevel.trim();
    }
    
    /**
     * 获取学习形式(显示名称为课程类型)
     *
     * @return FORM_LEARNING_ - 学习形式(显示名称为课程类型)
     */
    public String getFormLearning()
    {
        return formLearning;
    }
    
    /**
     * 设置学习形式(显示名称为课程类型)
     *
     * @param formLearning 学习形式(显示名称为课程类型)
     */
    public void setFormLearning(String formLearning)
    {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }
    
    /**
     * 获取是否为公共选修课(1:是，0：否)
     *
     * @return IS_ELECTIVE_ - 是否为公共选修课(1:是，0：否)
     */
    public Integer getIsElective()
    {
        return isElective;
    }
    
    /**
     * 设置是否为公共选修课(1:是，0：否)
     *
     * @param isElective 是否为公共选修课(1:是，0：否)
     */
    public void setIsElective(Integer isElective)
    {
        this.isElective = isElective;
    }
    
    /**
     * 获取所属学院
     *
     * @return COLLEGE_ - 所属学院
     */
    public String getCollege()
    {
        return college;
    }
    
    /**
     * 设置所属学院
     *
     * @param college 所属学院
     */
    public void setCollege(String college)
    {
        this.college = college == null ? null : college.trim();
    }
    
    /**
     * 获取课程性质(字典取值X_KCXZ)
     *
     * @return NATURE_ - 课程性质(字典取值X_KCXZ)
     */
    public String getNature()
    {
        return nature;
    }
    
    /**
     * 设置课程性质(字典取值X_KCXZ)
     *
     * @param nature 课程性质(字典取值X_KCXZ)
     */
    public void setNature(String nature)
    {
        this.nature = nature == null ? null : nature.trim();
    }
    
    /**
     * 获取课程分类（研究生，字典取值X_KCFL）
     *
     * @return LABEL_ - 课程分类（研究生，字典取值X_KCFL）
     */
    public String getLabel()
    {
        return label;
    }
    
    /**
     * 设置课程分类（研究生，字典取值X_KCFL）
     *
     * @param label 课程分类（研究生，字典取值X_KCFL）
     */
    public void setLabel(String label)
    {
        this.label = label == null ? null : label.trim();
    }
    
    /**
     * 获取授课语言
     *
     * @return TEACHING_LANGUAGE_ - 授课语言
     */
    public String getTeachingLanguage()
    {
        return teachingLanguage;
    }
    
    /**
     * 设置授课语言
     *
     * @param teachingLanguage 授课语言
     */
    public void setTeachingLanguage(String teachingLanguage)
    {
        this.teachingLanguage =
            teachingLanguage == null ? null : teachingLanguage.trim();
    }
    
    /**
     * 获取成绩记录方式（从字典取值 X_CJJL）
     *
     * @return SCORE_TYPE_ - 成绩记录方式（从字典取值 X_CJJL）
     */
    public String getScoreType()
    {
        return scoreType;
    }
    
    /**
     * 设置成绩记录方式（从字典取值 X_CJJL）
     *
     * @param scoreType 成绩记录方式（从字典取值 X_CJJL）
     */
    public void setScoreType(String scoreType)
    {
        this.scoreType = scoreType == null ? null : scoreType.trim();
    }
    
    /**
     * 获取考核方式（从字典取值 X_KSLX）
     *
     * @return ASSESSMENT_MODE_ - 考核方式（从字典取值 X_KSLX）
     */
    public String getAssessmentMode()
    {
        return assessmentMode;
    }
    
    /**
     * 设置考核方式（从字典取值 X_KSLX）
     *
     * @param assessmentMode 考核方式（从字典取值 X_KSLX）
     */
    public void setAssessmentMode(String assessmentMode)
    {
        this.assessmentMode =
            assessmentMode == null ? null : assessmentMode.trim();
    }
    
    /**
     * 获取课程负责人
     *
     * @return HEAD_TEACHER_ - 课程负责人
     */
    public String getHeadTeacher()
    {
        return headTeacher;
    }
    
    /**
     * 设置课程负责人
     *
     * @param headTeacher 课程负责人
     */
    public void setHeadTeacher(String headTeacher)
    {
        this.headTeacher = headTeacher == null ? null : headTeacher.trim();
    }
    
    /**
     * 获取其他讲课人
     *
     * @return TEACHERS_ - 其他讲课人
     */
    public String getTeachers()
    {
        return teachers;
    }
    
    /**
     * 设置其他讲课人
     *
     * @param teachers 其他讲课人
     */
    public void setTeachers(String teachers)
    {
        this.teachers = teachers == null ? null : teachers.trim();
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
     * 获取状态(1待提交，2待审核，3通过，0禁用)
     *
     * @return STATUS_ - 状态(1待提交，2待审核，3通过，0禁用)
     */
    public String getStatus()
    {
        return status;
    }
    
    /**
     * 设置状态(1待提交，2待审核，3通过，0禁用)
     *
     * @param status 状态(1待提交，2待审核，3通过，0禁用)
     */
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    /**
     * 获取更新时间
     *
     * @return UPDATE_TIME_ - 更新时间
     */
    public Date getUpdateTime()
    {
        return updateTime;
    }
    
    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime)
    {
        this.updateTime = updateTime;
    }
    
    /**
     * 获取备注
     *
     * @return REMARK_ - 备注
     */
    public String getRemark()
    {
        return remark;
    }
    
    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark)
    {
        this.remark = remark == null ? null : remark.trim();
    }
    
    /**
     * 获取教学方式（教学形式）字典取值
     *
     * @return TEACH_MODE_ - 教学方式（教学形式）字典取值
     */
    public String getTeachMode()
    {
        return teachMode;
    }
    
    /**
     * 设置教学方式（教学形式）字典取值
     *
     * @param teachMode 教学方式（教学形式）字典取值
     */
    public void setTeachMode(String teachMode)
    {
        this.teachMode = teachMode == null ? null : teachMode.trim();
    }
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", code=").append(code);
        sb.append(", credits=").append(credits);
        sb.append(", enabled=").append(enabled);
        sb.append(", name=").append(name);
        sb.append(", nameEn=").append(nameEn);
        sb.append(", weeks=").append(weeks);
        sb.append(", weekHour=").append(weekHour);
        sb.append(", period=").append(period);
        sb.append(", crossTerm=").append(crossTerm);
        sb.append(", term=").append(term);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", isElective=").append(isElective);
        sb.append(", college=").append(college);
        sb.append(", nature=").append(nature);
        sb.append(", label=").append(label);
        sb.append(", teachingLanguage=").append(teachingLanguage);
        sb.append(", scoreType=").append(scoreType);
        sb.append(", assessmentMode=").append(assessmentMode);
        sb.append(", headTeacher=").append(headTeacher);
        sb.append(", teachers=").append(teachers);
        sb.append(", managerDeptId=").append(managerDeptId);
        sb.append(", status=").append(status);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", remark=").append(remark);
        sb.append(", teachMode=").append(teachMode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}