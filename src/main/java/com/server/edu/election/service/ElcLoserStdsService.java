package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.LoserStuElcCourse;
import com.server.edu.election.vo.ElcLoserStdsVo;
import com.server.edu.util.async.AsyncExecuter;
import com.server.edu.util.async.AsyncResult;
import com.server.edu.util.excel.export.ExcelResult;

import java.util.List;


public interface ElcLoserStdsService {
    /**查询预警学生*/
    PageResult<ElcLoserStdsVo> findElcLoserStds(PageCondition<ElcLoserStdsVo> condition);

    /**移除预警学生*/
    String deleteLoserStudent(List<Long> ids);

    /**预警学生已经选课*/
    List<LoserStuElcCourse> findStudentElcCourse(Long calendarId, String studentId);

    /**预警学生退课*/
    void withdrawCourse(List<LoserStuElcCourse> ids);

    /**导出预警学生名单*/
    ExcelResult exportLoserStu(ElcLoserStdsVo condition);

    /**刷新预警名单*/
    AsyncResult reLoadLoserStu(Long calendarId,String deptId);

    void queryReloadLoserStu(Long calendarId,String deptId,AsyncExecuter resul);
}
