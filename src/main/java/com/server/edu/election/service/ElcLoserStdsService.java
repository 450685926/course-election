package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.vo.ElcLoserStdsVo;


public interface ElcLoserStdsService {
    /**查询预警学生*/
    PageResult<ElcLoserStdsVo> findElcLoserStds(PageCondition<ElcLoserStdsVo> condition);
}
