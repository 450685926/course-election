package com.server.edu.exam.dao;

import com.server.edu.exam.dto.ExamInfoRoomDto;
import com.server.edu.exam.entity.GraduateExamInfoRoom;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface GraduateExamInfoRoomDao extends Mapper<GraduateExamInfoRoom>,MySqlMapper<GraduateExamInfoRoom> {

}