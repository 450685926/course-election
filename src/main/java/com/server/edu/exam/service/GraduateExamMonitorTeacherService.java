package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.entity.GraduateExamMonitorTeacher;

import java.util.List;

public interface GraduateExamMonitorTeacherService {
    void addGraduateMonitorTeacher(GraduateExamMonitorTeacher monitorTeacher);

    void editGraduateMonitorTeacher(GraduateExamMonitorTeacher monitorTeacher);

    void deleteGraduateMonitorTeacher(List<Long> ids);

    PageResult<GraduateExamMonitorTeacher> listGraduateMonitorTeacher(PageCondition<GraduateExamMonitorTeacher> monitorTeacher);

}
