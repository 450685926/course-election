package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.vo.ExemptionCourseVo;

import java.util.List;

public interface ExemptionCourseDao {
    int deleteByPrimaryKey(Long id);

    int insert(ExemptionCourse record);

    int insertSelective(ExemptionCourse record);

    ExemptionCourse selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExemptionCourse record);

    int updateByPrimaryKey(ExemptionCourse record);

    //查询免修免考课程
    Page<ExemptionCourseVo> findExemptionCourse(ExemptionCourse condition);

    //批量删除
    void  deleteExemptionCourseByIds(List<Long> list);


}
