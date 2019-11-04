package com.server.edu.exam.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "graduate_exam_student_t")
public class GraduateExamStudent implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 研究生排考教室ID
     */
    @Column(name = "EXAM_ROOM_ID_")
    private Long examRoomId;

    /**
     * 排考学生学号
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 考试情况 1 正常 2 缓考 3 补考 4 重修
     */
    @Column(name = "EXAM_SITUATION_")
    private Integer examSituation;

    /**
     * 教学班ID
     */
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 教学班序号
     */
    @Column(name = "TEACHING_CLASS_CODE_")
    private String teachingClassCode;


    /**
     * 教学班名字
     */
    @Column(name = "TEACHING_CLASS_NAME_")
    private String teachingClassName;

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

    /**
     * 排考课程主键Id
     */
    @Column(name = "EXAM_INFO_ID_")
    private Long examInfoId;

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
     * 获取研究生排考教室ID
     *
     * @return EXAM_ROOM_ID_ - 研究生排考教室ID
     */
    public Long getExamRoomId() {
        return examRoomId;
    }

    /**
     * 设置研究生排考教室ID
     *
     * @param examRoomId 研究生排考教室ID
     */
    public void setExamRoomId(Long examRoomId) {
        this.examRoomId = examRoomId;
    }

    /**
     * 获取排考学生学号
     *
     * @return STUDENT_CODE_ - 排考学生学号
     */
    public String getStudentCode() {
        return studentCode;
    }

    /**
     * 设置排考学生学号
     *
     * @param studentCode 排考学生学号
     */
    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode == null ? null : studentCode.trim();
    }

    /**
     * 获取考试情况 1 正常 2 缓考 3 补考 4 重修
     *
     * @return EXAM_SITUATION_ - 考试情况 1 正常 2 缓考 3 补考 4 重修
     */
    public Integer getExamSituation() {
        return examSituation;
    }

    /**
     * 设置考试情况 1 正常 2 缓考 3 补考 4 重修
     *
     * @param examSituation 考试情况 1 正常 2 缓考 3 补考 4 重修
     */
    public void setExamSituation(Integer examSituation) {
        this.examSituation = examSituation;
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

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
    }

    public String getTeachingClassName() {
        return teachingClassName;
    }

    public void setTeachingClassName(String teachingClassName) {
        this.teachingClassName = teachingClassName;
    }

    public Long getExamInfoId() {
        return examInfoId;
    }

    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", examRoomId=").append(examRoomId);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", examSituation=").append(examSituation);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", teachingClassCode=").append(teachingClassCode);
        sb.append(", teachingClassName=").append(teachingClassName);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", remark=").append(remark);
        sb.append(", examInfoId=").append(examInfoId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}