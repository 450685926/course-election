package com.server.edu.election.dao;

import java.util.List;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ElectionApplyDto;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.vo.ElectionApplyVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElectionApplyDao extends Mapper<ElectionApply> {
	List<ElectionApplyVo> selectApplys(ElectionApplyDto dto);

	Page<ElectionApplyVo> applyUnList(ElectionApplyDto dto);

	Page<ElectionApplyVo> alreadyApplyList(ElectionApplyDto dto);
}