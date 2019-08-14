package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
@Table(name = "exemption_apply_manage_t")
public class ExemptionApplyManage implements Serializable {
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
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 课程名称
     */
    @Column(name = "COURSE_NAME_")
    private String courseName;

    /**
     * 学生学号
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 学生姓名
     */
    @Column(name = "NAME_")
    private String name;

    /**
     * 校历ID
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 申请类别
     */
    @Code2Text(transformer = "X_MXSQLX")
    @Column(name = "APPLY_TYPE_")
    private Integer applyType;

    /**
     * 成绩
     */
    @Column(name = "SCORE_")
    private String score;

    /**
     * 审核结果 0 待审批 1审批通过 2 审批未通过
     */
    @Code2Text(transformer = "X_SHZT")
    @Column(name = "EXAMINE_RESULT_")
    private Integer examineResult;

    /**
     * 审核人
     */
    @Column(name = "AUDITOR_")
    private String auditor;

    /**
     * 申请免修类型
     */
    @Column(name = "EXEMPTION_TYPE_")
    private String exemptionType;

    /**
     * 上传材料地址
     */
    @Column(name = "MATERIAL_IP_")
    private String materialIp;

    /**
     * 入学季节
     */
    @Column(name = "ENROL_SEASON_")
    private String enrolSeason;
    
    /**
     * 用户管理部门
     */
    @Column(name = "MANAGER_DEPT_ID_")
    private String managerDeptId;
    /**
     * 删除状态
     */
    @Column(name = "DELETE_STATUS_")
    private String deleteStatus;
    
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
     * @return COURSE_NAME_ - 课程名称
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

    /**
     * 获取学生学号
     *
     * @return STUDENT_CODE_ - 学生学号
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * 设置学生学号
     *
     * @param studentCode 学生学号
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }

    /**
     * 获取学生姓名
     *
     * @return NAME_ - 学生姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置学生姓名
     *
     * @param name 学生姓名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
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

    public String getEnrolSeason() {
		return enrolSeason;
	}

	public void setEnrolSeason(String enrolSeason) {
		this.enrolSeason = enrolSeason;
	}

	public String getManagerDeptId() {
		return managerDeptId;
	}

	public void setManagerDeptId(String managerDeptId) {
		this.managerDeptId = managerDeptId;
	}

	/**
     * 获取申请类别
     *
     * @return APPLY_TYPE_ - 申请类别
     */
    public Integer getApplyType() {
        return applyType;
    }

    /**
     * 设置申请类别
     *
     * @param applyType 申请类别
     */
    public void setApplyType(Integer applyType) {
        this.applyType = applyType;
    }

    /**
     * 获取成绩
     *
     * @return SCORE_ - 成绩
     */
    public String getScore() {
        return score;
    }

    /**
     * 设置成绩
     *
     * @param score 成绩
     */
    public void setScore(String score) {
        this.score = score == null ? null : score.trim();
    }

    /**
     * 获取审核结果 0 待审批 1审批通过 2 审批未通过
     *
     * @return EXAMINE_RESULT_ - 审核结果 0 待审批 1审批通过 2 审批未通过
     */
    public Integer getExamineResult() {
        return examineResult;
    }

    /**
     * 设置审核结果 0 待审批 1审批通过 2 审批未通过
     *
     * @param examineResult 审核结果 0 待审批 1审批通过 2 审批未通过
     */
    public void setExamineResult(Integer examineResult) {
        this.examineResult = examineResult;
    }

    /**
     * 获取审核人
     *
     * @return AUDITOR_ - 审核人
     */
    public String getAuditor() {
        return auditor;
    }

    /**
     * 设置审核人
     *
     * @param auditor 审核人
     */
    public void setAuditor(String auditor) {
        this.auditor = auditor == null ? null : auditor.trim();
    }

    /**
     * 获取申请免修类型
     *
     * @return EXEMPTION_TYPE_ - 申请免修类型
     */
    public String getExemptionType() {
        return exemptionType;
    }

    /**
     * 设置申请免修类型
     *
     * @param exemptionType 申请免修类型
     */
    public void setExemptionType(String exemptionType) {
        this.exemptionType = exemptionType == null ? null : exemptionType.trim();
    }

    /**
     * 获取上传材料地址
     *
     * @return MATERIAL_IP_ - 上传材料地址
     */
    public String getMaterialIp() {
        return materialIp;
    }

    
    public String getDeleteStatus() {
		return deleteStatus;
	}

	public void setDeleteStatus(String deleteStatus) {
		this.deleteStatus = deleteStatus;
	}

	/**
     * 设置上传材料地址
     *
     * @param materialIp 上传材料地址
     */
    public void setMaterialIp(String materialIp) {
        this.materialIp = materialIp == null ? null : materialIp.trim();
    }
    
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", courseName=").append(courseName);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", name=").append(name);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", applyType=").append(applyType);
        sb.append(", score=").append(score);
        sb.append(", examineResult=").append(examineResult);
        sb.append(", auditor=").append(auditor);
        sb.append(", exemptionType=").append(exemptionType);
        sb.append(", materialIp=").append(materialIp);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}