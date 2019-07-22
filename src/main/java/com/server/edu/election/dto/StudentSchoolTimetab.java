package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.dictionary.annotation.Code2Text.DataType;
import com.server.edu.dictionary.translator.ClassRoomTranslator;

/**
 * @description: 学生课表
 * @author: bear
 * @create: 2019-02-15 15:03
 */
@CodeI18n
public class StudentSchoolTimetab  {
    private Long calendarId;
    private String courseCode;
    private String courseName;
    private Double credits;
    private String teacherName;
    private String time;
    
    @Code2Text(translator = ClassRoomTranslator.class, dataType = DataType.SPLIT)
    private String room;
    private String calendarName;
    

    /**
             * 修读类别(1正常修读,2重修,3免修不免考,4免修)
     */
    @Code2Text(transformer = "X_XDLX")
    private String courseType;
    
    /**
              * 考核方式（从字典取值 X_KSLX）
     */
    @Code2Text(transformer = "X_KSLX")
    private String  assessmentMode;
    
    private String remark;
    
    @Code2Text(transformer = "X_XQ")
    private String campus;
    private Long teachingClassId;
    private String classCode;
    
    private String classRoom;
    private String className;
    private String teacherCode;
    
    /**
             *   是否为公共选修课(开课课程属性冗余 1:是，0：否)
     */
    private Integer isElective;

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
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

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public String getAssessmentMode() {
        return assessmentMode;
    }

    public void setAssessmentMode(String assessmentMode) {
        this.assessmentMode = assessmentMode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

	public Integer getIsElective() {
		return isElective;
	}

	public void setIsElective(Integer isElective) {
		this.isElective = isElective;
	}


}
