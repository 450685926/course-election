package com.server.edu.election.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElectionApplyDto;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.vo.ElectionApplyVo;

public interface ElectionApplyService {
	PageInfo<ElectionApplyVo> applyList(PageCondition<ElectionApplyDto> condition);
	int reply(ElectionApply electionApply);
	int delete(Long calendarId);
	int agree(Long id);
	int apply(String studentId,Long roundId,String courseCode);
}
