package com.server.edu.election.studentelec.context;

import com.server.edu.common.entity.BclHonorModule;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class BclHonorCourse extends BclHonorModule
{
    private String courseCode;
    private String courseName;
    private Long courseId;

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }
}
