package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.vo.GraduateExamAuthVo;

import java.util.List;

public interface GraduateExamAuthService {
    void addGraduateAuth(GraduateExamAuthVo authVo);

    void editGraduateAuth(GraduateExamAuthVo examAuthVo);

    void deleteGraduateAuth(List<Long> ids);

    PageResult<GraduateExamAuthVo> listGraduateExamAuth(PageCondition<GraduateExamAuthVo> examAuthVo);
}
