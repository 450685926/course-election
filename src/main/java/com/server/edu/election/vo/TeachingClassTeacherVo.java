package com.server.edu.election.vo;

import com.server.edu.election.entity.TeachingClassTeacher;

public class TeachingClassTeacherVo extends TeachingClassTeacher {
    private String faculty;

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }
}
