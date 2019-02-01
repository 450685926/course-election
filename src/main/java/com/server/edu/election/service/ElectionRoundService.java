package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.entity.ElectionRounds;

public interface ElectionRoundService
{
    /**
     * 分页查询
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElectionRounds> listPage(
        PageCondition<ElectionRounds> condition);
    /**
     * 根据ID查询轮次
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    ElectionRoundsDto get(Long roundId);
    
    /**
     * 添加轮次
     * 
     * @param dto
     * @see [类、类#方法、类#成员]
     */
    void add(ElectionRoundsDto dto);
    /**
     * 修改轮次
     * 
     * @param dto
     * @see [类、类#方法、类#成员]
     */
    void update(ElectionRoundsDto dto);
    /**
     * 删除轮次
     * 
     * @param ids
     * @see [类、类#方法、类#成员]
     */
    void delete(List<Long> ids);
}
