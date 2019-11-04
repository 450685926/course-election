package com.server.edu.exam.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "graduate_exam_teacher_t")
public class GraduateExamTeacher implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 关联研究生教室ID_
     */
    @Column(name = "EXAM_ROOM_ID_")
    private Long examRoomId;

    /**
     * 教师工号
     */
    @Column(name = "TEACHER_CODE_")
    private String teacherCode;

    /**
     * 教师姓名
     */
    @Column(name = "TEACHER_NAME_")
    private String teacherName;

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
     * 获取关联研究生教室ID_
     *
     * @return EXAM_ROOM_ID_ - 关联研究生教室ID_
     */
    public Long getExamRoomId() {
        return examRoomId;
    }

    /**
     * 设置关联研究生教室ID_
     *
     * @param examRoomId 关联研究生教室ID_
     */
    public void setExamRoomId(Long examRoomId) {
        this.examRoomId = examRoomId;
    }

    /**
     * 获取教师工号
     *
     * @return TEACHER_CODE_ - 教师工号
     */
    public String getTeacherCode() {
        return teacherCode;
    }

    /**
     * 设置教师工号
     *
     * @param teacherCode 教师工号
     */
    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode == null ? null : teacherCode.trim();
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

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", examRoomId=").append(examRoomId);
        sb.append(", teacherCode=").append(teacherCode);
        sb.append(", teacherName=").append(teacherName);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}