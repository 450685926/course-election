package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.vo.ElcLogVo;

public interface ElcLogService
{
    /**
     * 分页查询已选课名单
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcLogVo> listPage(PageCondition<ElcLog> page);
    
    void addCourseLog(ElcLog log);
    
    /**
     * 创建ElcLog实体类
     * 
     * @param calendarId
     * @param teachingClassId
     * @param teachingClassCode
     * @see [类、类#方法、类#成员]
     */
    ElcLog createElcLog(Long calendarId, Long teachingClassId,
        String teachingClassCode);
    
    void addList(List<ElcLog> list);
}
