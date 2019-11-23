package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "rebuild_course_recycle_t")
public class RebuildCourseRecycle implements Serializable {
    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 校历ID
     */
    @Column(name = "CALENDAR_ID_")
    private Long calendarId;

    /**
     * 学生学号
     */
    @Column(name = "STUDENT_CODE_")
    private String studentCode;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 教学班ID
     */
    @Column(name = "TEACHING_CLASS_ID_")
    private Long teachingClassId;

    /**
     * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Column(name = "COURSE_TAKE_TYPE_")
    private Integer courseTakeType;

    /**
     * 第几轮
     */
    @Column(name = "TURN_")
    private Integer turn;

    /**
     * 模式
     */
    @Column(name = "MODE_")
    private Integer mode;

    /**
     * 选课对象(1学生，2教务员，3管理员)
     */
    @Column(name = "CHOOSE_OBJ_")
    private Integer chooseObj;

    /**
     * 重修费是否支付(0否，1是)
     */
    @Column(name = "PAID_")
    private Integer paid;

    /**
     * 回收数据类型（1.重修，2.自动筛选）
     */
    @Column(name = "TYPE_")
    private Integer type;

    private static final long serialVersionUID = 1L;

    /**
     * @return ID_
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Long id) {
        this.id = id;
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
     * 获取教学班ID
     *
     * @return TEACHING_CLASS_ID_ - 教学班ID
     */
    public Long getTeachingClassId() {
        return teachingClassId;
    }

    /**
     * 设置教学班ID
     *
     * @param teachingClassId 教学班ID
     */
    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    /**
     * 获取修读类别(1正常修读,2重修,3免修不免考,4免修)
     *
     * @return COURSE_TAKE_TYPE_ - 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    public Integer getCourseTakeType() {
        return courseTakeType;
    }

    /**
     * 设置修读类别(1正常修读,2重修,3免修不免考,4免修)
     *
     * @param courseTakeType 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    public void setCourseTakeType(Integer courseTakeType) {
        this.courseTakeType = courseTakeType;
    }

    /**
     * 获取第几轮
     *
     * @return TURN_ - 第几轮
     */
    public Integer getTurn() {
        return turn;
    }

    /**
     * 设置第几轮
     *
     * @param turn 第几轮
     */
    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    /**
     * 获取模式
     *
     * @return MODE_ - 模式
     */
    public Integer getMode() {
        return mode;
    }

    /**
     * 设置模式
     *
     * @param mode 模式
     */
    public void setMode(Integer mode) {
        this.mode = mode;
    }

    /**
     * 获取选课对象(1学生，2教务员，3管理员)
     *
     * @return CHOOSE_OBJ_ - 选课对象(1学生，2教务员，3管理员)
     */
    public Integer getChooseObj() {
        return chooseObj;
    }

    /**
     * 设置选课对象(1学生，2教务员，3管理员)
     *
     * @param chooseObj 选课对象(1学生，2教务员，3管理员)
     */
    public void setChooseObj(Integer chooseObj) {
        this.chooseObj = chooseObj;
    }

    /**
     * 获取重修费是否支付(0否，1是)
     *
     * @return PAID_ - 重修费是否支付(0否，1是)
     */
    public Integer getPaid() {
        return paid;
    }

    /**
     * 设置重修费是否支付(0否，1是)
     *
     * @param paid 重修费是否支付(0否，1是)
     */
    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    /**
     * 获取回收数据类型（1.重修，2.自动筛选）
     *
     * @return TYPE_ - 回收数据类型（1.重修，2.自动筛选）
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置回收数据类型（1.重修，2.自动筛选）
     *
     * @param type 回收数据类型（1.重修，2.自动筛选）
     */
    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", studentCode=").append(studentCode);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", teachingClassId=").append(teachingClassId);
        sb.append(", courseTakeType=").append(courseTakeType);
        sb.append(", turn=").append(turn);
        sb.append(", mode=").append(mode);
        sb.append(", chooseObj=").append(chooseObj);
        sb.append(", paid=").append(paid);
        sb.append(", type=").append(type);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}