package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.vo.ElcCouSubsVo;

public interface ElcCouSubsService
{
    PageInfo<ElcCouSubsVo> page(
        PageCondition<ElcCouSubsDto> condition);
    
    int add(ElcCouSubs elcCouSubs);
    
    int update(ElcCouSubs elcCouSubs);
    
    int delete(List<Long> ids);
    
    List<ElcCouSubs> getElcNoGradCouSubs(List<String> studentIds);
}
