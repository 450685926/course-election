package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.Student;

import java.util.List;

@CodeI18n
public class RebuildStuVo extends Student {
    private Integer count;

    private List<String> courses;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
