package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.StudentSelectCourseList;
import com.server.edu.election.entity.ElectionRounds;

import tk.mybatis.mapper.common.Mapper;

public interface ElecRoundsDao extends Mapper<ElectionRounds>
{
    /**
     * 分页查询轮次数据
     * 
     * @param round
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<ElectionRounds> listPage(@Param("round") ElectionRounds round);
    
    /**
     * 查询一个
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    ElectionRoundsDto getOne(@Param("roundId") Long roundId);
    
    /**
     * 查询所有关联的规则ID
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<Long> listAllRefRuleId(@Param("roundId") Long roundId);
    
    /**
     * 保存轮次与规则关系
     * 
     * @param roundId
     * @param ruleId
     * @see [类、类#方法、类#成员]
     */
    void saveRoundRefRule(@Param("roundId") Long roundId,
        @Param("ruleId") Long ruleId);
    
    /**
     * 删除所有关联的规则
     * 
     * @param roundId
     * @see [类、类#方法、类#成员]
     */
    void deleteAllRefRule(@Param("roundId") Long roundId);
    
    @Select("select * from election_rounds_t where id_ IN (select ROUNDS_ID_ from election_rounds_stu_t where STUDENT_ID_ = #{stdId})")
    List<ElectionRounds> selectByStdId(@Param("stdId") Long stdId);
    
    /**
     * 查询快要开始的轮次，提前1个小时
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<ElectionRounds> selectWillBeStart();
    
    /**查询学生选课名单*/
    Page<StudentSelectCourseList> findElectCourseList(ReportManagementCondition condition);
}