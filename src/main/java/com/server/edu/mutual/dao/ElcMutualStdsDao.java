package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcMutualStdVo;
import com.server.edu.mutual.entity.ElcMutualStds;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcMutualStdsDao extends Mapper<ElcMutualStds>,MySqlMapper<ElcMutualStds> {
	List<ElcMutualCrossStuVo> getMutualStds(ElcMutualCrossStuDto dto);
	
	
	/**
	   *  判断学生是否在本研互选学生名单中
	 *
	 *  修改说明：返回单个po对象在切换学期时代码报错（切换未上送学生id所以返回多条记录），故统一使用list接收
	 * @param dto
	 * @return
	 */
	List<ElcMutualCrossStuVo> isInElcMutualStdList(ElcMutualCrossStuDto dto);

	/**
	 * 根据条件全量删除本研互选学生
	 * @param calendarId
	 * @param studentList
	 */
	int deleteMutualByParames(@Param("calendarId") long calendarId,@Param("studentList") List<String> studentList);
}