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
@Table(name = "course_open_t")
public class CourseOpen implements Serializable {
    /**
     * 主键（自增）
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID（学年学期）
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    @Column(name = "COURSE_NAME_")
    private String courseName;

    /**
     * 课程英文名称(开课课程属性冗余)
     */
    @Column(name = "COURSE_NAME_EN_")
    private String courseNameEn;

    /**
     * 是否为公共选修课(开课课程属性冗余 1:是，0：否)
     */
    @Column(name = "IS_ELECTIVE_")
    private Integer isElective;

    /**
     * 开课学院(开课课程属性冗余)
     */
    @Code2Text(transformer="X_YX")
    @Column(name = "FACULTY_")
    private String faculty;

    /**
     * 针对研究生的开课学期X_KKXQ（开课课程属性冗余 春秋季）
     */
    @Code2Text(transformer="X_KKXQ")
    @Column(name = "TERM_")
    private String term;

    /**
     * 是否跨学期(1是0否)
     */
    @Column(name = "CROSS_TERM_")
    private Integer crossTerm;

    /**
     * 培养层次X_PYCC(开课课程属性冗余)
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
     * 课程性质(开课课程属性冗余)
     */
    @Code2Text(transformer = "X_KCXZ")
    @Column(name = "NATURE_")
    private String nature;

    /**
     * 课时(开课课程属性冗余)
     */
    @Column(name = "PERIOD_")
    private Double period;

    /**
     * 学分(开课课程属性冗余)
     */
    @Column(name = "CREDITS_")
    private Double credits;

    /**
     * 开课人数（公选课直接指定；非公选课汇总培养专业对应的专业人数）
     */
    @Column(name = "NUMBER_")
    private Integer number;

    /**
     * 是否已开课(1:是，0：否)
     */
    @Column(name = "IS_OPEN_")
    private Integer isOpen;

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

    /**
     * 修改时间
     */
    @Column(name = "UPDATED_AT_")
    private Date updatedAt;

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
     * @return COURSE_NAME_
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * @param courseName
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName == null ? null : courseName.trim();
    }

    /**
     * 获取课程英文名称(开课课程属性冗余)
     *
     * @return COURSE_NAME_EN_ - 课程英文名称(开课课程属性冗余)
     */
    public String getCourseNameEn() {
        return courseNameEn;
    }

    /**
     * 设置课程英文名称(开课课程属性冗余)
     *
     * @param courseNameEn 课程英文名称(开课课程属性冗余)
     */
    public void setCourseNameEn(String courseNameEn) {
        this.courseNameEn = courseNameEn == null ? null : courseNameEn.trim();
    }

    /**
     * 获取是否为公共选修课(开课课程属性冗余 1:是，0：否)
     *
     * @return IS_ELECTIVE_ - 是否为公共选修课(开课课程属性冗余 1:是，0：否)
     */
    public Integer getIsElective() {
        return isElective;
    }

    /**
     * 设置是否为公共选修课(开课课程属性冗余 1:是，0：否)
     *
     * @param isElective 是否为公共选修课(开课课程属性冗余 1:是，0：否)
     */
    public void setIsElective(Integer isElective) {
        this.isElective = isElective;
    }

    /**
     * 获取开课学院(开课课程属性冗余)
     *
     * @return FACULTY_ - 开课学院(开课课程属性冗余)
     */
    public String getFaculty() {
        return faculty;
    }

    /**
     * 设置开课学院(开课课程属性冗余)
     *
     * @param faculty 开课学院(开课课程属性冗余)
     */
    public void setFaculty(String faculty) {
        this.faculty = faculty == null ? null : faculty.trim();
    }

    /**
     * 获取针对研究生的开课学期X_KKXQ（开课课程属性冗余 春秋季）
     *
     * @return TERM_ - 针对研究生的开课学期X_KKXQ（开课课程属性冗余 春秋季）
     */
    public String getTerm() {
        return term;
    }

    /**
     * 设置针对研究生的开课学期X_KKXQ（开课课程属性冗余 春秋季）
     *
     * @param term 针对研究生的开课学期X_KKXQ（开课课程属性冗余 春秋季）
     */
    public void setTerm(String term) {
        this.term = term == null ? null : term.trim();
    }

    /**
     * 获取是否跨学期(1是0否)
     *
     * @return CROSS_TERM_ - 是否跨学期(1是0否)
     */
    public Integer getCrossTerm() {
        return crossTerm;
    }

    /**
     * 设置是否跨学期(1是0否)
     *
     * @param crossTerm 是否跨学期(1是0否)
     */
    public void setCrossTerm(Integer crossTerm) {
        this.crossTerm = crossTerm;
    }

    /**
     * 获取培养层次X_PYCC(开课课程属性冗余)
     *
     * @return TRAINING_LEVEL_ - 培养层次X_PYCC(开课课程属性冗余)
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次X_PYCC(开课课程属性冗余)
     *
     * @param trainingLevel 培养层次X_PYCC(开课课程属性冗余)
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
     * 获取课程性质(开课课程属性冗余)
     *
     * @return NATURE_ - 课程性质(开课课程属性冗余)
     */
    public String getNature() {
        return nature;
    }

    /**
     * 设置课程性质(开课课程属性冗余)
     *
     * @param nature 课程性质(开课课程属性冗余)
     */
    public void setNature(String nature) {
        this.nature = nature == null ? null : nature.trim();
    }

    /**
     * 获取课时(开课课程属性冗余)
     *
     * @return PERIOD_ - 课时(开课课程属性冗余)
     */
    public Double getPeriod() {
        return period;
    }

    /**
     * 设置课时(开课课程属性冗余)
     *
     * @param period 课时(开课课程属性冗余)
     */
    public void setPeriod(Double period) {
        this.period = period;
    }

    /**
     * 获取学分(开课课程属性冗余)
     *
     * @return CREDITS_ - 学分(开课课程属性冗余)
     */
    public Double getCredits() {
        return credits;
    }

    /**
     * 设置学分(开课课程属性冗余)
     *
     * @param credits 学分(开课课程属性冗余)
     */
    public void setCredits(Double credits) {
        this.credits = credits;
    }

    /**
     * 获取开课人数（公选课直接指定；非公选课汇总培养专业对应的专业人数）
     *
     * @return NUMBER_ - 开课人数（公选课直接指定；非公选课汇总培养专业对应的专业人数）
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * 设置开课人数（公选课直接指定；非公选课汇总培养专业对应的专业人数）
     *
     * @param number 开课人数（公选课直接指定；非公选课汇总培养专业对应的专业人数）
     */
    public void setNumber(Integer number) {
        this.number = number;
    }

    /**
     * 获取是否已开课(1:是，0：否)
     *
     * @return IS_OPEN_ - 是否已开课(1:是，0：否)
     */
    public Integer getIsOpen() {
        return isOpen;
    }

    /**
     * 设置是否已开课(1:是，0：否)
     *
     * @param isOpen 是否已开课(1:是，0：否)
     */
    public void setIsOpen(Integer isOpen) {
        this.isOpen = isOpen;
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

    /**
     * 获取修改时间
     *
     * @return UPDATED_AT_ - 修改时间
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * 设置修改时间
     *
     * @param updatedAt 修改时间
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", courseName=").append(courseName);
        sb.append(", courseNameEn=").append(courseNameEn);
        sb.append(", isElective=").append(isElective);
        sb.append(", faculty=").append(faculty);
        sb.append(", term=").append(term);
        sb.append(", crossTerm=").append(crossTerm);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", nature=").append(nature);
        sb.append(", period=").append(period);
        sb.append(", credits=").append(credits);
        sb.append(", number=").append(number);
        sb.append(", isOpen=").append(isOpen);
        sb.append(", remark=").append(remark);
        sb.append(", createdAt=").append(createdAt);
        sb.append(", updatedAt=").append(updatedAt);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}