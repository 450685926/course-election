package com.server.edu.exam.dto;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

import java.io.Serializable;

/**
 * @description: 通过选课获取教学班
 * @author: bear
 * @create: 2019-09-09 10:33
 */
@CodeI18n
public class TeachingClassDto implements Serializable {
    private Long examInfoId;
    private Long teachingClassId;
    private String teachingClassCode;
    private String teachingClassName;
    private String teacherName;
    private String teacherCode;
    @Code2Text(DictTypeEnum.X_YX)
    private String faculty;
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    private Integer totalNumber;
    private Integer examNumber;
    private Integer examRoomNumber;
    private Integer noExamNumber;

    public Long getExamInfoId() {
        return examInfoId;
    }

    public void setExamInfoId(Long examInfoId) {
        this.examInfoId = examInfoId;
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

    public String getTeachingClassName() {
        return teachingClassName;
    }

    public void setTeachingClassName(String teachingClassName) {
        this.teachingClassName = teachingClassName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getTeacherCode() {
        return teacherCode;
    }

    public void setTeacherCode(String teacherCode) {
        this.teacherCode = teacherCode;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getExamNumber() {
        return examNumber;
    }

    public void setExamNumber(Integer examNumber) {
        this.examNumber = examNumber;
    }

    public Integer getExamRoomNumber() {
        return examRoomNumber;
    }

    public void setExamRoomNumber(Integer examRoomNumber) {
        this.examRoomNumber = examRoomNumber;
    }

    public Integer getNoExamNumber() {
        return noExamNumber;
    }

    public void setNoExamNumber(Integer noExamNumber) {
        this.noExamNumber = noExamNumber;
    }
}
