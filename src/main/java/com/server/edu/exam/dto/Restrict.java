package com.server.edu.exam.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @description: 排考学生冲突实体
 * @author: bear
 * @create: 2019-10-18 15:40
 */
public class Restrict implements Serializable{
    /** 约束描述*/
    private String descript;

    private String courseCode;

    private Set<String> studentIds;

    public Set<String> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(Set<String> studentIds) {
        this.studentIds = studentIds;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
