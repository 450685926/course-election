package com.server.edu.election.service;

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
    PageResult<ElcLogVo> listPage(
        PageCondition<ElcLog> page);


}
