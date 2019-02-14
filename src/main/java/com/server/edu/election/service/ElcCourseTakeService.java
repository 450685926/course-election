package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.vo.ElcCourseTakeVo;

/**
 * 已选课名单
 */
public interface ElcCourseTakeService
{
    /**
     * 分页查询已选课名单
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcCourseTakeVo> listPage(
        PageCondition<ElcCourseTakeQuery> page);
    
    /**
     * 查询学生的选退课信息
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcCourseTakeVo> listHistoryPage(
        PageCondition<ElcCourseTakeQuery> page);
    
    /**
     * 为指定学生加课
     * 
     * @param teachingClassIds
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void add(Long calendarId, List<Long> teachingClassIds, String studentId);
    
    /**
     * 为指定学生退课
     * 
     * @param teachingClassIds
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void withdraw(List<Long> teachingClassIds, String studentId);
}
