package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ElectionRuleDto;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.vo.ElectionRuleVo;

public interface ElectionRuleService
{
    List<ElectionRule> list(ElectionRuleDto electionRuleDto);
    
    ElectionRuleVo selectRuleDeatil(Long id);
    
    RestResult<Integer> updateRule(ElectionRuleDto electionRuleDto);
    
    RestResult<Integer> updateRuleParameter(ElectionRuleDto electionRuleDto);
    /**
     * 查询所有规则
     * 
     * @param projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<ElectionRuleVo> listAll(String projectId);
    
}
