package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.ElcNoGradCouSubsDto;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcNoGradCouSubsDao extends Mapper<ElcNoGradCouSubs> {
	List<ElcNoGradCouSubsVo> selectElcNoGradCouSubs(ElcNoGradCouSubsDto elcNoGradCouSubs);
}