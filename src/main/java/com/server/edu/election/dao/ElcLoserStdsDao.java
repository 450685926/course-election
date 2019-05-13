package com.server.edu.election.dao;


import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElcLoserStds;
import com.server.edu.election.vo.ElcLoserStdsVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;


public interface ElcLoserStdsDao extends Mapper<ElcLoserStds> {
    /**查询预警学生*/
    Page<ElcLoserStdsVo> findElcLoserStds(ElcLoserStdsVo condition);

    void deleteByIds(List<Long> list);

    void deleteByCalendarId(Long calendarId);

    void insertLoserStu(List<ElcLoserStds> list);
}