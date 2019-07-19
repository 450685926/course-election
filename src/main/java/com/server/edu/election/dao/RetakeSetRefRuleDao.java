package com.server.edu.election.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RetakeSetRefRuleDao {
    int saveRetakeSetRefRule(@Param("retakeSetId") Long retakeSetId,@Param("ruleIds") List<Long> ruleIds);

    int deleteByRetakeSetId(Long retakeSetId);
}
