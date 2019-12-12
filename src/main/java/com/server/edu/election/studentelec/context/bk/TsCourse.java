package com.server.edu.election.studentelec.context.bk;

import com.server.edu.election.studentelec.context.ElecCourse;

import java.io.Serializable;
import java.util.List;

public class TsCourse implements Serializable {
    private static final long serialVersionUID = 111L;

    private String tag;

    private ElecCourse course;

    public ElecCourse getCourse() {
        return course;
    }

    public void setCourse(ElecCourse course) {
        this.course = course;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
