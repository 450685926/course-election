package com.server.edu.exam.dao;

import com.github.pagehelper.Page;
import com.server.edu.exam.entity.GraduateExamAuth;
import com.server.edu.exam.vo.GraduateExamAuthVo;
import tk.mybatis.mapper.common.Mapper;

public interface GraduateExamAuthDao  extends Mapper<GraduateExamAuth> {
    Page<GraduateExamAuthVo> listGraduateExamAuth(GraduateExamAuthVo condition);
}
