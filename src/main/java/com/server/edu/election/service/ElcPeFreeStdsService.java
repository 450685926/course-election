package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcPeFreeStdsDto;
import com.server.edu.election.vo.ElcPeFreeStdsVo;

public interface ElcPeFreeStdsService {
	 PageInfo<ElcPeFreeStdsVo> list(PageCondition<ElcPeFreeStdsDto> condition);
	 int add(List<String> studentIds);
	 int delete(List<Long> ids);
	 PageInfo<ElcPeFreeStdsVo> addStudentInfo(PageCondition<ElcPeFreeStdsDto> condition);

}
