package com.server.edu.exam.dao;

import com.github.pagehelper.Page;
import com.server.edu.exam.entity.GraduateExamLog;
import com.server.edu.exam.entity.GraduateExamStudent;
import com.server.edu.exam.query.GraduateExamLogQuery;
import com.server.edu.exam.vo.GraduateExamLogVo;
import com.server.edu.exam.vo.GraduateExamStudentVo;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface GraduateExamLogDao extends Mapper<GraduateExamLog> ,MySqlMapper<GraduateExamLog> {
    Page<GraduateExamLogVo> listGraduateExamLog(GraduateExamLogQuery examLogVo);

    /**自动分配批量插入排考日志*/
    void insertBatchs(List<GraduateExamStudentVo> matchList);
}