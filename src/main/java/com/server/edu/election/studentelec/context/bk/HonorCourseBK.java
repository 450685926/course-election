package com.server.edu.election.studentelec.context.bk;

import com.server.edu.common.entity.BclHonorModule;
import com.server.edu.dictionary.annotation.CodeI18n;

/**
 * 已选择课程
 */
@CodeI18n
public class HonorCourseBK
{
    private BclHonorModule course;

    public BclHonorModule getCourse() {
        return course;
    }

    public void setCourse(BclHonorModule course) {
        this.course = course;
    }
}