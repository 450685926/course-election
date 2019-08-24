package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ElcStuCouLevelDto;
import com.server.edu.election.entity.ElcStuCouLevel;
import com.server.edu.election.query.StuCourseLevelQuery;

/**
 * 学生课程能力
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年8月22日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ElcStuCourseLevelService
{
    /**
     * 分页查询学生课程能力
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcStuCouLevelDto> listPage(
        PageCondition<StuCourseLevelQuery> page)
        throws Exception;
    
    /**
     * 新增
     * 
     * @param couLevel
     * @see [类、类#方法、类#成员]
     */
    void add(ElcStuCouLevel couLevel);
    
    /**
     * 更新
     * 
     * @param couLevel
     * @see [类、类#方法、类#成员]
     */
    void update(ElcStuCouLevel couLevel);
    
    /**
     * 删除
     * 
     * @param ids
     * @see [类、类#方法、类#成员]
     */
    void delete(List<Long> ids);
    
    /**
     * 批量新增
     * 
     * @param datas
     * @see [类、类#方法、类#成员]
     */
    String batchAdd(List<ElcStuCouLevel> datas);
}
