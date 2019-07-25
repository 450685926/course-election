package com.server.edu.election.dao;

import com.server.edu.election.vo.ElcRetakeSetVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RetakeSetRefRuleDao {
    int deleteByRetakeSetId(Long retakeSetId);

    int saveRetakeSetRefRule(@Param("retakeSet") ElcRetakeSetVo elcRetakeSetVo);
}
