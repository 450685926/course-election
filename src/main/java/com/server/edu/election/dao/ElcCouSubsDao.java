package com.server.edu.election.dao;

import java.util.List;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.ElcCouSubsVo;

import tk.mybatis.mapper.common.Mapper;

public interface ElcCouSubsDao extends Mapper<ElcCouSubs> {
	Page<ElcCouSubsVo> selectElcNoGradCouSubs(ElcCouSubsDto elcNoGradCouSubs);
	Page<Student> findStuInfoList(ElcCouSubsDto elcNoGradCouSubs);
}