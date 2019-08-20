package com.server.edu.election.vo;

import java.util.List;

/**
 * 要添加课程和冲突课程实体类
 */
public class CourseConflictVo {
    public List<String> addCourse;

    public List<String> selectedCourse;

    public List<String> getAddCourse() {
        return addCourse;
    }

    public void setAddCourse(List<String> addCourse) {
        this.addCourse = addCourse;
    }

    public List<String> getSelectedCourse() {
        return selectedCourse;
    }

    public void setSelectedCourse(List<String> selectedCourse) {
        this.selectedCourse = selectedCourse;
    }
}
