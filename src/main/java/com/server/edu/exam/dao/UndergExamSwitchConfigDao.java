package com.server.edu.exam.dao;

import com.server.edu.exam.entity.UndergExamSwitchConfig;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface UndergExamSwitchConfigDao extends Mapper<UndergExamSwitchConfig>, MySqlMapper<UndergExamSwitchConfig> {
    int deleteByPrimaryKey(Long id);

    int insert(UndergExamSwitchConfig record);

    int insertSelective(UndergExamSwitchConfig record);

    UndergExamSwitchConfig selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(UndergExamSwitchConfig record);

    int updateByPrimaryKey(UndergExamSwitchConfig record);
}