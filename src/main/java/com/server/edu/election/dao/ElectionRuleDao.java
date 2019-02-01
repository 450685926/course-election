package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.server.edu.election.entity.ElectionRule;

import tk.mybatis.mapper.common.Mapper;

public interface ElectionRuleDao extends Mapper<ElectionRule>
{
    
    @Select("select * from election_rule_t where id_ in(select RULE_ID_ from election_rounds_ref_rule_t where ROUNDS_ID_ = #{roundId})")
    List<ElectionRule> selectByRoundId(@Param("roundId") Long roundId);
}