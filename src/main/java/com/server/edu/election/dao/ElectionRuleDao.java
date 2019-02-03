package com.server.edu.election.dao;

import java.util.List;
import com.server.edu.election.entity.ElectionRule;
import com.server.edu.election.vo.ElectionRuleVo;

import tk.mybatis.mapper.common.Mapper;
import org.apache.ibatis.annotations.Param;

public interface ElectionRuleDao extends Mapper<ElectionRule>
{
    
    List<ElectionRule> selectByRoundId(@Param("roundId") Long roundId);

    List<ElectionRule> selectTplOfRule(Long tplId);
}