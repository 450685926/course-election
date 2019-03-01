package com.server.edu.election.dao;


import com.server.edu.election.entity.ElcLoserDownStds;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ElcLoserDownStdsDao extends Mapper<ElcLoserDownStds> {
    /**查询留降级学生*/
    ElcLoserDownStds findLoserDownStds(@Param("roundId") Long roundId,@Param("studentCode") String studentCode);
}