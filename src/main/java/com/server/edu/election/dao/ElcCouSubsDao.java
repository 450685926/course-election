package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.vo.ElcCouSubsVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcCouSubsDao extends Mapper<ElcCouSubs> {
	List<ElcCouSubsVo> selectElcNoGradCouSubs(ElcCouSubsDto elcNoGradCouSubs);
}