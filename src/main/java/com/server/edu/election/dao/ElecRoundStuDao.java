package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.Student4Elc;
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
    Page<Student4Elc> listPage(@Param("stu") ElecRoundStuQuery stu,
        @Param("roundId") Long roundId);
    
    /**
     * 根据条件查询满足条件并且没有添加过的的学生
     * 
     * @param stu
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<Student4Elc> listNotExistStudent(@Param("stu") ElecRoundStuQuery stu);
    
    /**
     * 查询存在的学号
     * 
     * @param studentCodes
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<String> listExistStu(@Param("studentCodes") List<String> studentCodes);
    
    /**
     * 查询已经增加过的学号
     * 
     * @param studentCodes
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<String> listAddedStu(@Param("roundId") Long roundId,
        @Param("studentCodes") List<String> studentCodes);
    
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
        @Param("studentCodes") List<String> studentCodes);
    
    /**
     * 删除指定轮次的学生名单
     * 
     * @see [类、类#方法、类#成员]
     */
    void deleteByRoundId(@Param("roundId") Long roundId);

    /**
     * 删除结业生表中学生，轮次中学生和可选课学生相应删除
     * */

    void deleteBystudentId(List<String> list);

    /**
     * 根据条件查询mode 模式 满足条件并且没有添加过的的学生
     *
     * @param stu
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<String> notExistStudent(@Param("stu") ElecRoundStuQuery stu);

    /**获取当前轮次的所有学生*/
    List<String> findStuByRoundId(Long roundId);

}
