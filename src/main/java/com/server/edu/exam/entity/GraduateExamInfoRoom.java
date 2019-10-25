package com.server.edu.exam.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "graduate_exam_info_room_t")
public class GraduateExamInfoRoom implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 研究生排考主键ID
     */
    @Column(name = "EXAM_INFO_ID_")
    private Long examInfoId;

    /**
     * 研究生排考教室主键ID
     */
    @Column(name = "EXAM_ROOM_ID_")
    private Long examRoomId;

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
     * 获取研究生排考主键ID
     *
     * @return EXAM_INFO_ID_ - 研究生排考主键ID
     */
    public Long getExamInfoId() {
        return examInfoId;
    }

    /**
     * 设置研究生排考主键ID
     *
     * @param examInfoId 研究生排考主键ID
     */
    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
    }

    /**
     * 获取研究生排考教室主键ID
     *
     * @return EXAM_ROOM_ID_ - 研究生排考教室主键ID
     */
    public Long getExamRoomId() {
        return examRoomId;
    }

    /**
     * 设置研究生排考教室主键ID
     *
     * @param examRoomId 研究生排考教室主键ID
     */
    public void setExamRoomId(Long examRoomId) {
        this.examRoomId = examRoomId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", examInfoId=").append(examInfoId);
        sb.append(", examRoomId=").append(examRoomId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}