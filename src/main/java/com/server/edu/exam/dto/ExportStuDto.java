package com.server.edu.exam.dto;

/**
 * @author daichang
 * @description 导出学生名单专用，为了导出两列
 * @date 2019/10/8
 */
public class ExportStuDto {
    private Integer order;
    private String teachingClassCode;
    private String studentCode;
    private String studentName;
    private String faculty;
    private String courseCode;
    private String courseName;
    private String sign;

    private Integer order_R;
    private String teachingClassCode_R;
    private String studentCode_R;
    private String studentName_R;
    private String faculty_R;
    private String courseCode_R;
    private String courseName_R;

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getTeachingClassCode() {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode) {
        this.teachingClassCode = teachingClassCode;
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

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public Integer getOrder_R() {
        return order_R;
    }

    public void setOrder_R(Integer order_R) {
        this.order_R = order_R;
    }

    public String getTeachingClassCode_R() {
        return teachingClassCode_R;
    }

    public void setTeachingClassCode_R(String teachingClassCode_R) {
        this.teachingClassCode_R = teachingClassCode_R;
    }

    public String getStudentCode_R() {
        return studentCode_R;
    }

    public void setStudentCode_R(String studentCode_R) {
        this.studentCode_R = studentCode_R;
    }

    public String getStudentName_R() {
        return studentName_R;
    }

    public void setStudentName_R(String studentName_R) {
        this.studentName_R = studentName_R;
    }

    public String getFaculty_R() {
        return faculty_R;
    }

    public void setFaculty_R(String faculty_R) {
        this.faculty_R = faculty_R;
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

    public String getCourseCode_R() {
        return courseCode_R;
    }

    public void setCourseCode_R(String courseCode_R) {
        this.courseCode_R = courseCode_R;
    }

    public String getCourseName_R() {
        return courseName_R;
    }

    public void setCourseName_R(String courseName_R) {
        this.courseName_R = courseName_R;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
