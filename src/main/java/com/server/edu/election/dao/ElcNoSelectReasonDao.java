package com.server.edu.election.dao;


import com.server.edu.election.entity.ElcNoSelectReason;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ElcNoSelectReasonDao extends Mapper<ElcNoSelectReason> {
    ElcNoSelectReason findNoSelectReason(@Param("calendarId") Long calendarId,@Param("studentCode") String studentCode);

    /**删除未选课原因*/
    void deleteNoSelectReason(@Param("calendarId") Long calendarId,@Param("studentCode") String studentCode);
}