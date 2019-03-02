package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.vo.ExemptionCourseVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExemptionCourseDao extends Mapper<ExemptionCourse> {


    //查询免修免考课程
    Page<ExemptionCourseVo> findExemptionCourse(ExemptionCourse condition);

    //批量删除
    void  deleteExemptionCourseByIds(List<Long> list);

}
