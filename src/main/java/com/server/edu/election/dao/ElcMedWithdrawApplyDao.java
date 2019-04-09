package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElcMedWithdrawApplyDto;
import com.server.edu.election.entity.ElcMedWithdrawApply;
import com.server.edu.election.vo.ElcMedWithdrawApplyVo;

import tk.mybatis.mapper.common.Mapper;
public interface ElcMedWithdrawApplyDao extends Mapper<ElcMedWithdrawApply> {
	List<ElcMedWithdrawApplyVo> selectMedApplyList(ElcMedWithdrawApplyDto dto);
	List<ElcMedWithdrawApplyVo> selectMedRuleCourse(ElcMedWithdrawApplyDto dto);
	int batchUpdate(List<ElcMedWithdrawApply> list);
	List<ElcMedWithdrawApplyVo> selectApplyLogs(ElcMedWithdrawApplyDto dto);
}