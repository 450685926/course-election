package com.server.edu.election.dao;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.vo.ElcLogVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 选课日志
 * 
 */
public interface ElcLogDao extends Mapper<ElcLog>, MySqlMapper<ElcLog>
{
    Page<ElcLogVo> listPage(@Param("query") ElcLog elcLog);
    
    /**查询选退课日志*/
    Page<ElcLogVo> findCourseLog(ElcLogVo logVo);
    
}