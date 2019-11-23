package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.query.ElecRoundStuQuery;
import com.server.edu.util.async.AsyncResult;

/**
 * 可选课学生
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ElecRoundStuService
{
    PageResult<Student4Elc> listPage(PageCondition<ElecRoundStuQuery> condition);
    
    /**
     * 根据学号来添加
     * 
     * @param studentCodes
     * @return
     * @see [类、类#方法、类#成员]
     */
    String add(Long roundId, List<String> studentCodes,Integer mode);
    
    /**
     * 通过条件添加学生
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
    void addByCondition(ElecRoundStuQuery condition);
    
    /**
     * 通过条件添加学生
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
	AsyncResult addByConditionBK(ElecRoundStuQuery condition);
    
    /**
     * 删除可选课学生
     * 
     * @param roundId
     * @param studentCodes
     * @see [类、类#方法、类#成员]
     */
    void delete(Long roundId, List<String> studentCodes);
    
    /**
     * 通过条件删除学生
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
    void deleteByCondition(ElecRoundStuQuery condition);


}
