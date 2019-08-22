package com.server.edu.election.dao;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElcStuCouLevelDto;
import com.server.edu.election.entity.ElcStuCouLevel;
import com.server.edu.election.query.StuCourseLevelQuery;

import tk.mybatis.mapper.common.Mapper;

/**
 * 学生课程能力
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年8月22日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ElcStuCouLevelDao extends Mapper<ElcStuCouLevel>
{
    /**
     * 查询学生课程能力
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<ElcStuCouLevelDto> listPage(
        @Param("query") PageCondition<StuCourseLevelQuery> page);
}