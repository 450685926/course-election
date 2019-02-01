package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.common.rest.StudentInfo;
import com.server.edu.election.query.ElecRoundStuQuery;

/**
 * 可选课学生名单
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ElecRoundStuDao
{
    /**
     * 分页查询可选课学生列表
     * 
     * @param stu
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<StudentInfo> listPage(@Param("stu") ElecRoundStuQuery stu,
        @Param("roundId") Long roundId);
    
    /**
     * 根据条件查询满足条件的学生
     * 
     * @param stu
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<StudentInfo> listStudent(@Param("stu") ElecRoundStuQuery stu);
    /**
     * 查询不存在的学号
     * 
     * @param studentCodes
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<String> listExistStu(@Param("studentCodes") List<String> studentCodes);
    
    /**
     * 添加可选课学生名单
     * 
     * @param roundId
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void add(@Param("roundId") Long roundId,
        @Param("studentId") String studentId);
    
    /**
     * 删除可选课学生名单
     * 
     * @param roundId
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void delete(@Param("roundId") Long roundId,
        @Param("studentId") String studentId);
    /**
     * 删除指定轮次的学生名单
     * 
     * @see [类、类#方法、类#成员]
     */
    void deleteByRoundId(@Param("roundId") Long roundId);
}
