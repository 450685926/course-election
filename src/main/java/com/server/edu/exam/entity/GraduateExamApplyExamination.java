package com.server.edu.exam.entity;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.SchoolCalendarTranslator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "graduate_exam_apply_examination_t")
@CodeI18n
public class GraduateExamApplyExamination implements Serializable {
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
     * 申请来源 1 学生申请 2 代理申请
     */
    @Column(name = "APPLY_SOURCE_")
    private Integer applySource;

    /**
     * 学号
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 教学班
     */
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 申请类型  2 缓考 3补考
     */
    @Column(name = "APPLY_TYPE_")
    private Integer applyType;

    /**
     * 审核状态 0 待审核 1 学院审核未通过 2 学院审核通过 3学校审核未通过 4学校审核通过
     */
    @Column(name = "APPLY_STATUS_")
    private Integer applyStatus;

    /**
     * 申请理由
     */
    @Column(name = "APPLY_REASON_")
    private String applyReason;

    /**
     * 审核意见
     */
    @Column(name = "ADUIT_OPINIONS_")
    private String aduitOpinions;

    /**
     * 部门ID,预留在职研究生
     */
    @Column(name = "PROJ_ID_")
    private String projId;


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
     * 备注
     */
    @Column(name = "REMARK_")
    private String remark;

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
     * 获取申请来源 1 学生申请 2 代理申请
     *
     * @return APPLY_SOURCE_ - 申请来源 1 学生申请 2 代理申请
     */
    public Integer getApplySource() {
        return applySource;
    }

    /**
     * 设置申请来源 1 学生申请 2 代理申请
     *
     * @param applySource 申请来源 1 学生申请 2 代理申请
     */
    public void setApplySource(Integer applySource) {
        this.applySource = applySource;
    }

    /**
     * 获取学号
     *
     * @return STUDENT_CODE_ - 学号
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * 设置学号
     *
     * @param studentCode 学号
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
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
     * 获取申请类型 1补考 2 缓考
     *
     * @return APPLY_TYPE_ - 申请类型 1补考 2 缓考
     */
    public Integer getApplyType() {
        return applyType;
    }

    /**
     * 设置申请类型 1补考 2 缓考
     *
     * @param applyType 申请类型 1补考 2 缓考
     */
    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    public Integer getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(Integer applyStatus) {
        this.applyStatus = applyStatus;
    }

    /**
     * 获取申请理由
     *
     * @return APPLY_REASON_ - 申请理由
     */
    public String getApplyReason() {
        return applyReason;
    }

    /**
     * 设置申请理由
     *
     * @param applyReason 申请理由
     */
    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason == null ? null : applyReason.trim();
    }

    /**
     * 获取审核意见
     *
     * @return ADUIT_OPINIONS_ - 审核意见
     */
    public String getAduitOpinions() {
        return aduitOpinions;
    }

    /**
     * 设置审核意见
     *
     * @param aduitOpinions 审核意见
     */
    public void setAduitOpinions(String aduitOpinions) {
        this.aduitOpinions = aduitOpinions == null ? null : aduitOpinions.trim();
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

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", applySource=").append(applySource);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", applyType=").append(applyType);
        sb.append(", applyStatus=").append(applyStatus);
        sb.append(", applyReason=").append(applyReason);
        sb.append(", aduitOpinions=").append(aduitOpinions);
        sb.append(", projId=").append(projId);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}