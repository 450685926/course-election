package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.vo.TeachingClassVo;

import tk.mybatis.mapper.common.Mapper;

/**
 * 教学班
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2018年11月28日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface TeachingClassDao extends Mapper<TeachingClass>
{
    /**
     * 分页查询教学班
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<TeachingClassVo> listPage(ElcResultQuery condition);
    
}
