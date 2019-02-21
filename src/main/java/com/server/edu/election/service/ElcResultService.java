package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.vo.TeachingClassVo;

public interface ElcResultService
{
    /**
     * 教学班信息
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<TeachingClassVo> listPage(PageCondition<ElcResultQuery> page);
    
    /**
     * 自动剔除超过人数
     * 
     * @param dto
     * @see [类、类#方法、类#成员]
     */
    void autoRemove(AutoRemoveDto dto);
}
