package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "achievement_course_t")
public class AchievementCourse implements Serializable {
    /**
     * 成绩分级ID
     */
    @Id
    @Column(name = "ACHIENVEMENT_ID_")
    private Long achienvementId;

    /**
     * 课程代码
     */
    @Id
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    private static final long serialVersionUID = 1L;

    /**
     * 获取成绩分级ID
     *
     * @return ACHIENVEMENT_ID_ - 成绩分级ID
     */
    public Long getAchienvementId() {
        return achienvementId;
    }

    /**
     * 设置成绩分级ID
     *
     * @param achienvementId 成绩分级ID
     */
    public void setAchienvementId(Long achienvementId) {
        this.achienvementId = achienvementId;
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", achienvementId=").append(achienvementId);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}