package com.server.edu.exam.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.SchoolCalendarTranslator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "graduate_exam_auth_t")
@CodeI18n
public class GraduateExamAuth implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 学期ID
     */
    @Code2Text(translator = SchoolCalendarTranslator.class)
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 排考开始时间
     */
    @Column(name = "BEGIN_TIME_")
    private Date beginTime;

    /**
     * 排考结束时间
     */
    @Column(name = "END_TIME_")
    private Date endTime;

    /**
     * 培养层次
     */
    @Code2Text(transformer = "X_PYCC")
    @Column(name = "TRAINING_LEVEL_")
    private String trainingLevel;

    /**
     * 课程性质 取数据字典
     */
    @Code2Text(transformer = "X_KCXZ")
    @Column(name = "COURSE_NATURE_")
    private String courseNature;

    /**
     * 排考考试类型 1 期末考试  2 补缓考
     */
    @Column(name = "EXAM_TYPE_")
    private Integer examType;

    /**
     * 培养类别 取数据字典
     */

    @Code2Text(transformer = "X_PYLB")
    @Column(name = "TRAINING_CATEGORY_")
    private String trainingCategory;

    /**
     * 学位类型 取数据字典
     */
    @Code2Text(transformer = "X_XWLX")
    @Column(name = "DEGREE_TYPE_")
    private String degreeType;

    /**
     * 学习形式 取数据字典
     */
    @Code2Text(transformer = "X_XXXS")
    @Column(name = "FORM_LEARNING_")
    private String formLearning;

    /**
     * 开课学院
     */
    @Code2Text(transformer = "X_YX")
    @Column(name = "OPEN_COLLEGE_")
    private String openCollege;

    /**
     * 创建时间
     */
    @Column(name = "CREATE_AT_")
    private Date createAt;

    /**
     * 更新时间
     */
    @Column(name = "UPDATE_AT_")
    private Date updateAt;

    /**
     * 删除 1 已删除 0 为删除
     */
    @Column(name = "DELETE_STATUS_")
    private Integer deleteStatus;

    /**
     * 部门ID,预留在职研究生
     */
    @Column(name = "PROJ_ID_")
    private String projId;

    /**
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     * 备注 1 相同 0 不同
     */
    @Column(name = "TIME_SAME_")
    private Integer timeSame;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键
     *
     * @return ID_ - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取学期ID
     *
     * @return CALENDAR_ID_ - 学期ID
     */
    public Long getCalendarId() {
        return calendarId;
    }

    /**
     * 设置学期ID
     *
     * @param calendarId 学期ID
     */
    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取排考开始时间
     *
     * @return BEGIN_TIME_ - 排考开始时间
     */
    public Date getBeginTime() {
        return beginTime;
    }

    /**
     * 设置排考开始时间
     *
     * @param beginTime 排考开始时间
     */
    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    /**
     * 获取排考结束时间
     *
     * @return END_TIME_ - 排考结束时间
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置排考结束时间
     *
     * @param endTime 排考结束时间
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取培养层次
     *
     * @return TRAINING_LEVEL_ - 培养层次
     */
    public String getTrainingLevel() {
        return trainingLevel;
    }

    /**
     * 设置培养层次
     *
     * @param trainingLevel 培养层次
     */
    public void setTrainingLevel(String trainingLevel) {
        this.trainingLevel = trainingLevel == null ? null : trainingLevel.trim();
    }

    /**
     * 获取课程性质 取数据字典
     *
     * @return COURSE_NATURE_ - 课程性质 取数据字典
     */
    public String getCourseNature() {
        return courseNature;
    }

    /**
     * 设置课程性质 取数据字典
     *
     * @param courseNature 课程性质 取数据字典
     */
    public void setCourseNature(String courseNature) {
        this.courseNature = courseNature == null ? null : courseNature.trim();
    }

    /**
     * 获取排考考试类型 1 期末考试  2 补缓考
     *
     * @return EXAM_TYPE_ - 排考考试类型 1 期末考试  2 补缓考
     */
    public Integer getExamType() {
        return examType;
    }

    /**
     * 设置排考考试类型 1 期末考试  2 补缓考
     *
     * @param examType 排考考试类型 1 期末考试  2 补缓考
     */
    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    /**
     * 获取培养类别 取数据字典
     *
     * @return TRAINING_CATEGORY_ - 培养类别 取数据字典
     */
    public String getTrainingCategory() {
        return trainingCategory;
    }

    /**
     * 设置培养类别 取数据字典
     *
     * @param trainingCategory 培养类别 取数据字典
     */
    public void setTrainingCategory(String trainingCategory) {
        this.trainingCategory = trainingCategory == null ? null : trainingCategory.trim();
    }

    /**
     * 获取学位类型 取数据字典
     *
     * @return DEGREE_TYPE_ - 学位类型 取数据字典
     */
    public String getDegreeType() {
        return degreeType;
    }

    /**
     * 设置学位类型 取数据字典
     *
     * @param degreeType 学位类型 取数据字典
     */
    public void setDegreeType(String degreeType) {
        this.degreeType = degreeType == null ? null : degreeType.trim();
    }

    /**
     * 获取学习形式 取数据字典
     *
     * @return FORM_LEARNING_ - 学习形式 取数据字典
     */
    public String getFormLearning() {
        return formLearning;
    }

    /**
     * 设置学习形式 取数据字典
     *
     * @param formLearning 学习形式 取数据字典
     */
    public void setFormLearning(String formLearning) {
        this.formLearning = formLearning == null ? null : formLearning.trim();
    }

    /**
     * 获取开课学院
     *
     * @return OPEN_COLLEGE_ - 开课学院
     */
    public String getOpenCollege() {
        return openCollege;
    }

    /**
     * 设置开课学院
     *
     * @param openCollege 开课学院
     */
    public void setOpenCollege(String openCollege) {
        this.openCollege = openCollege == null ? null : openCollege.trim();
    }

    /**
     * 获取创建时间
     *
     * @return CREATE_AT_ - 创建时间
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * 设置创建时间
     *
     * @param createAt 创建时间
     */
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    /**
     * 获取更新时间
     *
     * @return UPDATE_AT_ - 更新时间
     */
    public Date getUpdateAt() {
        return updateAt;
    }

    /**
     * 设置更新时间
     *
     * @param updateAt 更新时间
     */
    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }

    /**
     * 获取删除 1 已删除 0 为删除
     *
     * @return DELETE_STATUS_ - 删除 1 已删除 0 为删除
     */
    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * 设置删除 1 已删除 0 为删除
     *
     * @param deleteStatus 删除 1 已删除 0 为删除
     */
    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    /**
     * 获取部门ID,预留在职研究生
     *
     * @return PROJ_ID_ - 部门ID,预留在职研究生
     */
    public String getProjId() {
        return projId;
    }

    /**
     * 设置部门ID,预留在职研究生
     *
     * @param projId 部门ID,预留在职研究生
     */
    public void setProjId(String projId) {
        this.projId = projId == null ? null : projId.trim();
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

    public Integer getTimeSame() {
        return timeSame;
    }

    public void setTimeSame(Integer timeSame) {
        this.timeSame = timeSame;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", beginTime=").append(beginTime);
        sb.append(", endTime=").append(endTime);
        sb.append(", trainingLevel=").append(trainingLevel);
        sb.append(", courseNature=").append(courseNature);
        sb.append(", examType=").append(examType);
        sb.append(", trainingCategory=").append(trainingCategory);
        sb.append(", degreeType=").append(degreeType);
        sb.append(", formLearning=").append(formLearning);
        sb.append(", openCollege=").append(openCollege);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", deleteStatus=").append(deleteStatus);
        sb.append(", projId=").append(projId);
        sb.append(", remark=").append(remark);
        sb.append(", timeSame=").append(timeSame);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}