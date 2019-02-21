package com.server.edu.election.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@CodeI18n
@Table(name = "exemption_rule_t")
public class ExemptionCourseRule implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 培养层次X_PYCC(专科,本科,硕士,博士,其他,预科)
     */
    @Code2Text(transformer = "X_PYCC")
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
     * 创建时间
     */
    @Column(name = "CREATED_AT_")
    private Date createdAt;

    /**
     * 培养类别
     */

    @Column(name = "TRAINING_CATEGORY_")
    private String trainingCategory;

    /**
     * 申请类别 0 入学成绩 1 材料申请
     */
    @Column(name = "APPLY_TYPE_")
    private Integer applyType;

    /**
     * 年级
     */
    @Column(name = "GRADE_")
    private Integer grade;

    /**
     * 截至分数线
     */
    @Column(name = "MINIMUM_PASS_SCORE_")
    private Double minimumPassScore;

    /**
     * 截至范围
     */
    @Column(name = "PENCENT_")
    private Integer pencent;

    /**
     * 学生种类 0 非推免生 1 推免生
     */
    @Column(name = "STUDENT_TYPE_AUDITOR_")
    private String studentTypeAuditor;

    /**
     * 学生人数
     */
    @Column(name = "NUMBER_")
    private Integer number;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 审核人，多个以逗号隔开
     */
    @Column(name = "AUDITOR_")
    private String auditor;

    /**
     * 申请类型说明
     */
    @Column(name = "APPLY_DESCRIPTION_")
    private String applyDescription;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 课程名称
     */
    @Column(name = "COURSE_NAME")
    private String courseName;

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
     * 获取校历ID
     *
     * @return CALENDAR_ID_ - 校历ID
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置校历ID
     *
     * @param calendarId 校历ID
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

    /**
     * 获取培养类别
     *
     * @return TRAINING_CATEGORY_ - 培养类别
     */
    public String getTrainingCategory() {
        return trainingCategory;
    }

    /**
     * 设置培养类别
     *
     * @param trainingCategory 培养类别
     */
    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory == null ? null : trainingCategory.trim();
    }

    /**
     * 获取申请类别 0 入学成绩 1 材料申请
     *
     * @return APPLY_TYPE_ - 申请类别 0 入学成绩 1 材料申请
     */
    public Integer getApplyType() {
        return applyType;
    }

    /**
     * 设置申请类别 0 入学成绩 1 材料申请
     *
     * @param applyType 申请类别 0 入学成绩 1 材料申请
     */
    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    /**
     * 获取年级
     *
     * @return GRADE_ - 年级
     */
    public Integer getGrade() {
        return grade;
    }

    /**
     * 设置年级
     *
     * @param grade 年级
     */
    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    /**
     * 获取截至分数线
     *
     * @return MINIMUM_PASS_SCORE_ - 截至分数线
     */
    public Double getMinimumPassScore() {
        return minimumPassScore;
    }

    /**
     * 设置截至分数线
     *
     * @param minimumPassScore 截至分数线
     */
    public void setMinimumPassScore(Double minimumPassScore) {
        this.minimumPassScore = minimumPassScore;
    }

    /**
     * 获取截至范围
     *
     * @return PENCENT_ - 截至范围
     */
    public Integer getPencent() {
        return pencent;
    }

    /**
     * 设置截至范围
     *
     * @param pencent 截至范围
     */
    public void setPencent(Integer pencent) {
        this.pencent = pencent;
    }

    /**
     * 获取学生种类 0 非推免生 1 推免生
     *
     * @return STUDENT_TYPE_AUDITOR_ - 学生种类 0 非推免生 1 推免生
     */
    public String getStudentTypeAuditor() {
        return studentTypeAuditor;
    }

    /**
     * 设置学生种类 0 非推免生 1 推免生
     *
     * @param studentTypeAuditor 学生种类 0 非推免生 1 推免生
     */
    public void setStudentTypeAuditor(String studentTypeAuditor) {
        this.studentTypeAuditor = studentTypeAuditor == null ? null : studentTypeAuditor.trim();
    }

    /**
     * 获取学生人数
     *
     * @return NUMBER_ - 学生人数
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 设置学生人数
     *
     * @param number 学生人数
     */
    public void setNumber(Integer number) {
        this.number = number;
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
     * 获取审核人，多个以逗号隔开
     *
     * @return AUDITOR_ - 审核人，多个以逗号隔开
     */
    public String getAuditor() {
        return auditor;
    }

    /**
     * 设置审核人，多个以逗号隔开
     *
     * @param auditor 审核人，多个以逗号隔开
     */
    public void setAuditor(String auditor) {
        this.auditor = auditor == null ? null : auditor.trim();
    }

    /**
     * 获取申请类型说明
     *
     * @return APPLY_DESCRIPTION_ - 申请类型说明
     */
    public String getApplyDescription() {
        return applyDescription;
    }

    /**
     * 设置申请类型说明
     *
     * @param applyDescription 申请类型说明
     */
    public void setApplyDescription(String applyDescription) {
        this.applyDescription = applyDescription == null ? null : applyDescription.trim();
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
     * 获取课程名称
     *
     * @return COURSE_NAME - 课程名称
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 设置课程名称
     *
     * @param courseName 课程名称
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName == null ? null : courseName.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", faculty=").append(faculty);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", trainingCategory=").append(trainingCategory);
        sb.append(", applyType=").append(applyType);
        sb.append(", grade=").append(grade);
        sb.append(", minimumPassScore=").append(minimumPassScore);
        sb.append(", pencent=").append(pencent);
        sb.append(", studentTypeAuditor=").append(studentTypeAuditor);
        sb.append(", number=").append(number);
        sb.append(", remark=").append(remark);
        sb.append(", auditor=").append(auditor);
        sb.append(", applyDescription=").append(applyDescription);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", courseName=").append(courseName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}