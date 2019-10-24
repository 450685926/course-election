package com.server.edu.exam.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "graduate_exam_room_t")
public class GraduateExamRoom implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 教室ID
     */
    @Column(name = "ROOM_ID_")
    private Long roomId;

    /**
     * 教室名字
     */
    @Column(name = "ROOM_NAME_")
    private String roomName;

    /**
     * 教室容量
     */
    @Column(name = "ROOM_CAPACITY_")
    private Integer roomCapacity;

    /**
     * 已排考人数
     */
    @Column(name = "ROOM_NUMBER_")
    private Integer roomNumber;

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
     * 备注
     */
    @Column(name = "TOWER_CODE_")
    private String towerCode;

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
     * 获取教室ID
     *
     * @return ROOM_ID_ - 教室ID
     */
    public Long getRoomId() {
        return roomId;
    }

    /**
     * 设置教室ID
     *
     * @param roomId 教室ID
     */
    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    /**
     * 获取教室容量
     *
     * @return ROOM_CAPACITY_ - 教室容量
     */
    public Integer getRoomCapacity() {
        return roomCapacity;
    }

    /**
     * 设置教室容量
     *
     * @param roomCapacity 教室容量
     */
    public void setRoomCapacity(Integer roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    /**
     * 获取已排考人数
     *
     * @return ROOM_NUMBER_ - 已排考人数
     */
    public Integer getRoomNumber() {
        return roomNumber;
    }

    /**
     * 设置已排考人数
     *
     * @param roomNumber 已排考人数
     */
    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getTowerCode() {
        return towerCode;
    }

    public void setTowerCode(String towerCode) {
        this.towerCode = towerCode;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", roomId=").append(roomId);
        sb.append(", roomName=").append(roomName);
        sb.append(", roomCapacity=").append(roomCapacity);
        sb.append(", roomNumber=").append(roomNumber);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", remark=").append(remark);
        sb.append(", towerCode=").append(towerCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}