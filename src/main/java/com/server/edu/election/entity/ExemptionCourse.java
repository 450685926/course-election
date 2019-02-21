package com.server.edu.election.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@CodeI18n
@Table(name = "exemption_course_t")
public class ExemptionCourse implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 课程名称
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 培养层次X_PYCC(专科,本科,硕士,博士,其他,预科)
     */
    @Code2Text(transformer="X_PYCC")
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 学习形式X_XXXS
     */
    @Code2Text(transformer = "X_XXXS")
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 学院
     */
    @Code2Text(transformer="X_YX")
    @Column(name = "FACULTY_")
    private String faculty;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键（自增）
     *
     * @return ID_ - 主键（自增）
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键（自增）
     *
     * @param id 主键（自增）
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取课程名称
     *
     * @return NAME_ - 课程名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置课程名称
     *
     * @param name 课程名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取课程代码
     *
     * @return COURSE_CODE_ - 课程代码
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * 设置课程代码
     *
     * @param courseCode 课程代码
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }

    /**
     * 获取校历ID（学年学期）
     *
     * @return CALENDAR_ID_ - 校历ID（学年学期）
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置校历ID（学年学期）
     *
     * @param calendarId 校历ID（学年学期）
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取培养层次X_PYCC(专科,本科,硕士,博士,其他,预科)
     *
     * @return TRAINING_LEVEL_ - 培养层次X_PYCC(专科,本科,硕士,博士,其他,预科)
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次X_PYCC(专科,本科,硕士,博士,其他,预科)
     *
     * @param trainingLevel 培养层次X_PYCC(专科,本科,硕士,博士,其他,预科)
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    /**
     * 获取学习形式X_XXXS
     *
     * @return FORM_LEARNING_ - 学习形式X_XXXS
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式X_XXXS
     *
     * @param formLearning 学习形式X_XXXS
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    /**
     * 获取学院
     *
     * @return FACULTY_ - 学院
     */
    public String getFaculty() {
        return faculty;
    }

    /**
     * 设置学院
     *
     * @param faculty 学院
     */
    public void setFaculty(String faculty) {
        this.faculty = faculty == null ? null : faculty.trim();
    }

    /**
     * 获取备注
     *
     * @return REMARK_ - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 获取创建时间
     *
     * @return CREATED_AT_ - 创建时间
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * 设置创建时间
     *
     * @param createdAt 创建时间
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", name=").append(name);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", faculty=").append(faculty);
        sb.append(", remark=").append(remark);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}