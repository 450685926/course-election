package com.server.edu.exam.dao;

import com.github.pagehelper.Page;
import com.server.edu.exam.entity.GraduateExamMakeUpAuth;
import tk.mybatis.mapper.common.Mapper;

public interface GraduateExamMakeUpAuthDao extends Mapper<GraduateExamMakeUpAuth> {
    Page<GraduateExamMakeUpAuth> listMakeUpExamAuth(GraduateExamMakeUpAuth upAuth);
}