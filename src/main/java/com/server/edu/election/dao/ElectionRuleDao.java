package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.vo.ElectionRuleVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElectionRuleDao extends Mapper<ElectionRule>
{
    
    List<ElectionRule> selectByRoundId(@Param("roundId") Long roundId);
    
    /**
     * 根据projectId查询所有规则
     * 
     * @param projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<ElectionRuleVo> listAllByProjectId(@Param("projectId") String projectId);
    
}