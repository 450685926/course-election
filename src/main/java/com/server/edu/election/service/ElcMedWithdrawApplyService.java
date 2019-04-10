package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcMedWithdrawApplyDto;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcMedWithdrawApplyVo;
public interface ElcMedWithdrawApplyService {
	PageInfo<ElcCourseTakeVo> applyList(PageCondition<ElcMedWithdrawApplyDto> condition);
	PageInfo<ElcMedWithdrawApplyVo> applyLogs(PageCondition<ElcMedWithdrawApplyDto> condition);
	PageInfo<ElcMedWithdrawApplyVo> list(PageCondition<ElcMedWithdrawApplyDto> condition);
	int apply(Long id,Integer projectId);
	String approval(List<Long> ids,Integer projectId);

}
