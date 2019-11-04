package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.entity.GraduateExamMakeUpAuth;

import java.util.List;

public interface GraduateExamMakeUpAuthService {
    void addMakeUpExamAuth(GraduateExamMakeUpAuth makeUpAuth);

    void editMakeUpExamAuth(GraduateExamMakeUpAuth makeUpAuth);

    void deleteMakeUpExamAuth(List<Long> ids);


    PageResult<GraduateExamMakeUpAuth> listMakeUpExamAuth(PageCondition<GraduateExamMakeUpAuth> makeUpAuth);
}
