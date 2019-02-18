package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.query.ElecRoundCourseQuery;

/**
 * 可选课学生
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ElecRoundCourseService
{
    /**
     * 已添加任务列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<CourseOpenDto> listPage(PageCondition<ElecRoundCourseQuery> condition);
    
    /**
     * 未添加任务列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<CourseOpenDto> listUnAddPage(PageCondition<ElecRoundCourseQuery> condition);
    
    /**
     * 查询已添加的教学任务中所有的教学班
     * 
     * @param query
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<CourseOpenDto> listTeachingClassPage(
        PageCondition<ElecRoundCourseQuery> condition);
    
    /**
     * 添加
     * 
     * @param courseCodes
     * @return
     * @see [类、类#方法、类#成员]
     */
    void add(Long roundId, List<String> courseCodes);
    
    /**
     * 添加所有
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
    void addAll(ElecRoundCourseQuery condition);
    
    /**
     * 删除
     * 
     * @param roundId
     * @param studentCodes
     * @see [类、类#方法、类#成员]
     */
    void delete(Long roundId, List<String> courseCodes);
    
    /**
     * 删除全部
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
    void deleteAll(Long roundId);
}
