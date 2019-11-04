package com.server.edu.exam.entity;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.SchoolCalendarTranslator;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "graduate_exam_info_t")
@CodeI18n
public class GraduateExamInfo implements Serializable {
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
     * 课程代码 选课数据
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 校区
     */
    @Code2Text(DictTypeEnum.X_XQ)
    @Column(name = "CAMPUS_")
    private String campus;

    /**
     * 考场数
     */
    @Column(name = "EXAM_ROOMS_")
    private Integer examRooms;

    /**
     * 应考人数
     */
    @Column(name = "TOTAL_NUMBER_")
    private Integer totalNumber;

    /**
     * 实际人数
     */
    @Column(name = "ACTUAL_NUMBER_")
    private Integer actualNumber;

    /**
     * 考试日期
     */
    @Column(name = "EXAM_DATE_")
    private Date examDate;

    /**
     * 考试开始时间
     */
    @Column(name = "EXAM_START_TIME_")
    private String examStartTime;

    /**
     * 考试结束时间
     */
    @Column(name = "EXAM_END_TIME_")
    private String examEndTime;

    /**
     * 学院通知 0 有时间地点 1 学院通知
     */
    @Column(name = "NOTICE_")
    private Integer notice;

    /**
     * 排考状态 0 未排考 1 已排考 2 已发布
     */
    @Column(name = "EXAM_STATUS_")
    private Integer examStatus;

    /**
     * 排考类型 1 期末考试 2 补缓考
     */
    @Column(name = "EXAM_TYPE_")
    private Integer examType;

    /**
     * 部门ID
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
     * 备注 学院通知可填写信息
     */
    @Column(name = "REMARK_")
    private String remark;

    /**
     *  排考第几周
     */
    @Column(name = "WEEK_NUMBER_")
    private Integer weekNumber;

    /**
     *  排考 星期几
     */
    @Column(name = "WEEK_DAY_")
    private Integer weekDay;

    /**
     * 排考节次
     */
    @Column(name = "ClASS_NODE_")
    private String classNode;

    /**
     * 排考时间
     */
    @Column(name = "EXAM_TIME_")
    private String examTime;

    /**
     * 实际排考时间学期
     */
    @Column(name = "ACTUAL_CALENDAR_ID_")
    private Long actualCalendarId;

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
     * 获取课程代码 选课数据
     *
     * @return COURSE_CODE_ - 课程代码 选课数据
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * 设置课程代码 选课数据
     *
     * @param courseCode 课程代码 选课数据
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }

    /**
     * 获取校区
     *
     * @return CAMPUS_ - 校区
     */
    public String getCampus() {
        return campus;
    }

    /**
     * 设置校区
     *
     * @param campus 校区
     */
    public void setCampus(String campus) {
        this.campus = campus == null ? null : campus.trim();
    }

    /**
     * 获取考场数
     *
     * @return EXAM_ROOMS_ - 考场数
     */
    public Integer getExamRooms() {
        return examRooms;
    }

    /**
     * 设置考场数
     *
     * @param examRooms 考场数
     */
    public void setExamRooms(Integer examRooms) {
        this.examRooms = examRooms;
    }

    /**
     * 获取应考人数
     *
     * @return TOTAL_NUMBER_ - 应考人数
     */
    public Integer getTotalNumber() {
        return totalNumber;
    }

    /**
     * 设置应考人数
     *
     * @param totalNumber 应考人数
     */
    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    /**
     * 获取实际人数
     *
     * @return ACTUAL_NUMBER_ - 实际人数
     */
    public Integer getActualNumber() {
        return actualNumber;
    }

    /**
     * 设置实际人数
     *
     * @param actualNumber 实际人数
     */
    public void setActualNumber(Integer actualNumber) {
        this.actualNumber = actualNumber;
    }

    /**
     * 获取考试日期
     *
     * @return EXAM_DATE_ - 考试日期
     */
    public Date getExamDate() {
        return examDate;
    }

    /**
     * 设置考试日期
     *
     * @param examDate 考试日期
     */
    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    /**
     * 获取考试开始时间
     *
     * @return EXAM_START_TIME_ - 考试开始时间
     */
    public String getExamStartTime() {
        return examStartTime;
    }

    /**
     * 设置考试开始时间
     *
     * @param examStartTime 考试开始时间
     */
    public void setExamStartTime(String examStartTime) {
        this.examStartTime = examStartTime;
    }

    /**
     * 获取考试结束时间
     *
     * @return EXAM_END_TIME_ - 考试结束时间
     */
    public String getExamEndTime() {
        return examEndTime;
    }

    /**
     * 设置考试结束时间
     *
     * @param examEndTime 考试结束时间
     */
    public void setExamEndTime(String examEndTime) {
        this.examEndTime = examEndTime;
    }

    /**
     * 获取学院通知 0 有时间地点 1 学院通知
     *
     * @return NOTICE_ - 学院通知 0 有时间地点 1 学院通知
     */
    public Integer getNotice() {
        return notice;
    }

    /**
     * 设置学院通知 0 有时间地点 1 学院通知
     *
     * @param notice 学院通知 0 有时间地点 1 学院通知
     */
    public void setNotice(Integer notice) {
        this.notice = notice;
    }

    /**
     * 获取排考状态 0 未排考 1 已排考 2 已发布
     *
     * @return EXAM_STATUS_ - 排考状态 0 未排考 1 已排考 2 已发布
     */
    public Integer getExamStatus() {
        return examStatus;
    }

    /**
     * 设置排考状态 0 未排考 1 已排考 2 已发布
     *
     * @param examStatus 排考状态 0 未排考 1 已排考 2 已发布
     */
    public void setExamStatus(Integer examStatus) {
        this.examStatus = examStatus;
    }

    /**
     * 获取排考类型 1 期末考试 2 补缓考
     *
     * @return EXAM_TYPE_ - 排考类型 1 期末考试 2 补缓考
     */
    public Integer getExamType() {
        return examType;
    }

    /**
     * 设置排考类型 1 期末考试 2 补缓考
     *
     * @param examType 排考类型 1 期末考试 2 补缓考
     */
    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    /**
     * 获取部门ID
     *
     * @return PROJ_ID_ - 部门ID
     */
    public String getProjId() {
        return projId;
    }

    /**
     * 设置部门ID
     *
     * @param projId 部门ID
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
     * 获取备注 学院通知可填写信息
     *
     * @return REMARK_ - 备注 学院通知可填写信息
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注 学院通知可填写信息
     *
     * @param remark 备注 学院通知可填写信息
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }


    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Integer getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(Integer weekDay) {
        this.weekDay = weekDay;
    }

    public String getClassNode() {
        return classNode;
    }

    public void setClassNode(String classNode) {
        this.classNode = classNode;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public Long getActualCalendarId() {
        return actualCalendarId;
    }

    public void setActualCalendarId(Long actualCalendarId) {
        this.actualCalendarId = actualCalendarId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", calendarId=").append(calendarId);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", campus=").append(campus);
        sb.append(", examRooms=").append(examRooms);
        sb.append(", totalNumber=").append(totalNumber);
        sb.append(", actualNumber=").append(actualNumber);
        sb.append(", examDate=").append(examDate);
        sb.append(", examStartTime=").append(examStartTime);
        sb.append(", examEndTime=").append(examEndTime);
        sb.append(", notice=").append(notice);
        sb.append(", examStatus=").append(examStatus);
        sb.append(", examType=").append(examType);
        sb.append(", projId=").append(projId);
        sb.append(", createAt=").append(createAt);
        sb.append(", updateAt=").append(updateAt);
        sb.append(", remark=").append(remark);
        sb.append(", weekNumber=").append(weekNumber);
        sb.append(", weekDay=").append(weekDay);
        sb.append(", classNode=").append(classNode);
        sb.append(", actualCalendarId=").append(actualCalendarId);
        sb.append(", examTime=").append(examTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}