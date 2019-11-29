package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.mutual.dto.ElcMutualListDto;
import com.server.edu.mutual.entity.ElcMutualApply;
import com.server.edu.mutual.vo.ElcMutualListVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcMutualListDao extends Mapper<ElcMutualApply>,MySqlMapper<ElcMutualApply>{
	/**
	 * 获取研究生或本科生名单列表
	 * @param dto
	 * @return
	 */
	List<ElcMutualListVo> getMutualStuList(ElcMutualListDto dto);

	/**
	 * 获取课程名单列表 
	 * @param dto 
	 * @return
	 */
	List<ElcMutualListVo> getMutualCourseList(ElcMutualListDto dto);
	
}
