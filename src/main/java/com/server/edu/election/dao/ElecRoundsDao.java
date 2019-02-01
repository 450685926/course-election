package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.TeachingClassDto;
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
    @Select("select * from election_rounds_t where DATE_ADD(sysdate(),INTERVAL 1 HOUR) BETWEEN BEGIN_TIME_ and END_TIME_ and OPEN_FLAG_ = 1")
    List<ElectionRounds> selectBeStart();
    
    /**
     * 查询轮次所有可选课程
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<TeachingClassDto> selectTeachingClassByRoundId(Long roundId);
}