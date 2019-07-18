package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.vo.RetakeCourseCountVo;

public interface RetakeCourseCountDao {
    Page<RetakeCourseCountVo> findRetakeCourseCountList(RetakeCourseCountVo retakeCourseCountVo);

    int saveRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);

    int updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo);
}
