package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElcMedWithdrawRuleRefCourDto;
import com.server.edu.election.entity.ElcMedWithdrawRuleRefCour;
import com.server.edu.election.vo.ElcMedWithdrawRuleRefCourVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcMedWithdrawRuleRefCourDao extends Mapper<ElcMedWithdrawRuleRefCour> {
	List<ElcMedWithdrawRuleRefCourVo> selectMedRuleRefCours(ElcMedWithdrawRuleRefCourDto dto);
	List<ElcMedWithdrawRuleRefCourVo> selectUnMedRuleRefCours(ElcMedWithdrawRuleRefCourDto dto);
	int batchInsert(List<ElcMedWithdrawRuleRefCour> list);
}