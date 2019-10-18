package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;

/**
 * @description: 未缴费课程名单
 * @author: bear
 * @create: 2019-02-13 15:06
 */
@CodeI18n
public class RebuildCourseNoChargeList implements Serializable{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long calendarId;
    private String studentCode;
    private String studentName;
    private String courseCode;//课程代码
    private String courseName;
    private String nature;
    private Double credits;
    private Integer paid;
    private Double weekHour;
    private Integer endWeek;
    private Integer startWeek;
    private String label;
    private Double period;
    private Long teachingClassId;
    private String teachingClassCode;
    private String courseArr;//课程按排
    private String calendarName;
    @Code2Text(DictTypeEnum.X_XDLX)
    private Integer courseTakeType;
    private Integer mode;
    private Integer turn;
    private Integer chooseObj;
    @Code2Text(DictTypeEnum.X_PYCC)
    private String trainingLevel;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCourseTakeType() {
        return courseTakeType;
    }

    public void setCourseTakeType(Integer courseTakeType) {
        this.courseTakeType = courseTakeType;
    }

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Integer getTurn() {
        return turn;
    }

    public void setTurn(Integer turn) {
        this.turn = turn;
    }

    public Integer getChooseObj() {
        return chooseObj;
    }

    public void setChooseObj(Integer chooseObj) {
        this.chooseObj = chooseObj;
    }



    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getCourseArr() {
        return courseArr;
    }

    public void setCourseArr(String courseArr) {
        this.courseArr = courseArr;
    }

    public Double getPeriod() {
        return period;
    }

    public void setPeriod(Double period) {
        this.period = period;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }

    public Double getWeekHour() {
        return weekHour;
    }

    public void setWeekHour(Double weekHour) {
        this.weekHour = weekHour;
    }

    public Integer getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(Integer endWeek) {
        this.endWeek = endWeek;
    }

    public Integer getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(Integer startWeek) {
        this.startWeek = startWeek;
    }
}
