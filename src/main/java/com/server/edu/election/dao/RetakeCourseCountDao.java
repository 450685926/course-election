package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.RetakeCourseCountVo;

public interface RetakeCourseCountDao {
    Page<RetakeCourseCountVo> findRetakeCourseCountList(RetakeCourseCountVo retakeCourseCountVo);

    int saveRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);

    int updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);

    int deleteRetakeCourseCount(Long retakeCourseCountId);

    /**根据学生信息查询学生重修门数上限*/
    Integer findRetakeCount(Student student);
}
