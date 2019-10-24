package com.server.edu.exam.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "graduate_exam_log_t")
public class GraduateExamLog implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 学号
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 考场
     */
    @Column(name = "ROOM_ID_")
    private Long roomId;

    /**
     * 考场
     */
    @Column(name = "ROOM_NAME_")
    private String roomName;

    /**
     * 排考课程
     */
    @Column(name = "EXAM_INFO_ID_")
    private Long examInfoId;

    /**
     * 操作类型 1 排考 0 退考
     */
    @Column(name = "EXAM_TYPE_")
    private Integer examType;

    /**
     * 操作人
     */
    @Column(name = "OPERATOR_CODE_")
    private String operatorCode;

    /**
     * 操作人姓名
     */
    @Column(name = "OPERATOR_NAME_")
    private String operatorName;

    /**
     * 操作人IP
     */
    @Column(name = "IP_")
    private String ip;


    /**
     * 操作时间
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
     * 获取操作类型 1 排考 0 退考
     *
     * @return ExamType - 操作类型 1 排考 0 退考
     */
    public Integer getExamType() {
        return examType;
    }



    /**
     * 设置操作类型 1 排考 0 退考
     *
     * @param examType 操作类型 1 排考 0 退考
     */
    public void setExamType(Integer examType) {
        this.examType = examType;
    }


    /**
     * 获取操作人IP
     *
     * @return IP_ - 操作人IP
     */
    public String getIp() {
        return ip;
    }

    /**
     * 设置操作人IP
     *
     * @param ip 操作人IP
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }


    /**
     * 获取操作时间
     *
     * @return CREATE_AT_ - 操作时间
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * 设置操作时间
     *
     * @param createAt 操作时间
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

    public Long getExamInfoId() {
        return examInfoId;
    }

    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getOperatorCode() {
        return operatorCode;
    }

    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", roomId=").append(roomId);
        sb.append(", roomName=").append(roomName);
        sb.append(", examType=").append(examType);
        sb.append(", operatorCode=").append(operatorCode);
        sb.append(", operatorName=").append(operatorName);
        sb.append(", ip=").append(ip);
        sb.append(", examInfoId=").append(examInfoId);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}