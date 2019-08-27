package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;

/**
 * 课程维护学生选课数据实体类
 */
public class ElcStudentVo {
    private Long calendarId;
    private String studentId;
    private String studentName;
    private String teacherName;
    private Integer grade;
    @Code2Text(transformer="X_YX")
    private String faculty;
    @Code2Text(transformer="X_YX")
    private String courseFaculty;
    @Code2Text(DictTypeEnum.G_ZY)
    private String profession;
    private String classCode;
    private String courseCode;
    private String courseName;
    private String teachingClassName;
    private Long teachingClassId;
    @Code2Text(transformer="X_KCXZ")
    private String nature;
    private Double credits;
    @Code2Text(transformer="X_XDLX")
    private Integer courseTakeType;
    private Integer elcNumber;
    private Integer number;
    private String courseArrange;
    private String campus;
    private String manArrangeFlag;

    public String getManArrangeFlag() {
        return manArrangeFlag;
    }

    public void setManArrangeFlag(String manArrangeFlag) {
        this.manArrangeFlag = manArrangeFlag;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getCourseArrange() {
        return courseArrange;
    }

    public void setCourseArrange(String courseArrange) {
        this.courseArrange = courseArrange;
    }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getCourseFaculty() {
        return courseFaculty;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public void setCourseFaculty(String courseFaculty) {
        this.courseFaculty = courseFaculty;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getElcNumber() {
        return elcNumber;
    }

    public void setElcNumber(Integer elcNumber) {
        this.elcNumber = elcNumber;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
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

    public String getTeachingClassName() {
        return teachingClassName;
    }

    public void setTeachingClassName(String teachingClassName) {
        this.teachingClassName = teachingClassName;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public Double getCredits() {
        return credits;
    }

    public void setCredits(Double credits) {
        this.credits = credits;
    }

    public Integer getCourseTakeType() {
        return courseTakeType;
    }

    public void setCourseTakeType(Integer courseTakeType) {
        this.courseTakeType = courseTakeType;
    }
}
