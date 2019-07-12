package com.server.edu.election.dto;

public class ElcStudentCourseDto extends ElcStudentDto {
    private String studentCode;

    private String studentName;

    private String teachingClassName;

    private String chooseObj;

    private String researchDirection;


    public String getResearchDirection() {
        return researchDirection;
    }

    public void setResearchDirection(String researchDirection) {
        this.researchDirection = researchDirection;
    }

    public String getTeachingClassName() {
        return teachingClassName;
    }

    public void setTeachingClassName(String teachingClassName) {
        this.teachingClassName = teachingClassName;
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

    public String getChooseObj() {
        return chooseObj;
    }

    public void setChooseObj(String chooseObj) {
        this.chooseObj = chooseObj;
    }
}
