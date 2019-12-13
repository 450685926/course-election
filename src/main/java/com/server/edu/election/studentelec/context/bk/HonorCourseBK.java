package com.server.edu.election.studentelec.context.bk;

import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.studentelec.context.BclHonorCourse;

/**
 * 已选择课程
 */
@CodeI18n
public class HonorCourseBK
{
    private BclHonorCourse course;

    public BclHonorCourse getCourse() {
        return course;
    }

    public void setCourse(BclHonorCourse course) {
        this.course = course;
    }
}