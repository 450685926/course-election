package com.server.edu.election.dto;

import java.util.List;
import java.util.Objects;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.translator.ClassRoomTranslator;

/**
 * @description: 时间表
 * @author: bear
 * @create: 2019-04-29 17:51
 */
@CodeI18n
public class TimeTableMessage {
    private Integer dayOfWeek;
    private Integer timeStart;
    private Integer timeEnd;
    @Code2Text(translator = ClassRoomTranslator.class)
    private String roomId;
    private String teacherCode;
    private String weekNum;//周次
    private String weekstr;//星期
    private String teacherName;//
    private String timeAndRoom;//时间地点
    private String timeTab;//时间
    private String className;//教学班名称
    private String classCode;//教学班序号
    private String courseName;//课程名称
    private String courseCode;//课程代码
    private Long teachingClassId;//教学班Id
    @Code2Text(transformer = "X_XQ")
    private String campus;
    // 所有的周 1,2,3,5,6,7
    private List<Integer> weeks;
    private Long timeId; //教学班排课时间ID

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getTimeTab() {
        return timeTab;
    }

    public void setTimeTab(String timeTab) {
        this.timeTab = timeTab;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(Integer timeStart) {
        this.timeStart = timeStart;
    }

    public Integer getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Integer timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getWeekNum() {
        return weekNum;
    }

    public void setWeekNum(String weekNum) {
        this.weekNum = weekNum;
    }

    public String getWeekstr() {
        return weekstr;
    }

    public void setWeekstr(String weekstr) {
        this.weekstr = weekstr;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTimeAndRoom() {
        return timeAndRoom;
    }

    public void setTimeAndRoom(String timeAndRoom) {
        this.timeAndRoom = timeAndRoom;
    }

    public List<Integer> getWeeks()
    {
        return weeks;
    }

    public void setWeeks(List<Integer> weeks)
    {
        this.weeks = weeks;
    }

	public Long getTimeId() {
		return timeId;
	}

	public void setTimeId(Long timeId) {
		this.timeId = timeId;
	}
}
