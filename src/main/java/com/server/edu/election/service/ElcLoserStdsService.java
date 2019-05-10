package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.LoserStuElcCourse;
import com.server.edu.election.vo.ElcLoserStdsVo;

import java.util.List;


public interface ElcLoserStdsService {
    /**查询预警学生*/
    PageResult<ElcLoserStdsVo> findElcLoserStds(PageCondition<ElcLoserStdsVo> condition);

    /**移除预警学生*/
    String deleteLoserStudent(List<Long> ids);

    /**预警学生已经选课*/
    List<LoserStuElcCourse> findStudentElcCourse(Long calendarId, String studentId);
}
