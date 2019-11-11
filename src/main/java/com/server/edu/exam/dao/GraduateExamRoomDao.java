package com.server.edu.exam.dao;

import com.github.pagehelper.Page;
import com.server.edu.exam.dto.ExamInfoRoomDto;
import com.server.edu.exam.dto.ExamRoomDto;
import com.server.edu.exam.dto.PropertySheetDto;
import com.server.edu.exam.dto.SelectDto;
import com.server.edu.exam.entity.GraduateExamRoom;
import com.server.edu.exam.query.GraduateExamMessageQuery;
import com.server.edu.exam.query.GraduateExamRoomsQuery;
import com.server.edu.exam.query.TeachingClassQuery;
import com.server.edu.exam.vo.GraduateExamMessage;
import com.server.edu.exam.vo.GraduateExamRoomVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface GraduateExamRoomDao extends Mapper<GraduateExamRoom> ,MySqlMapper<GraduateExamRoom>{
    Page<GraduateExamRoomVo> listExamRoomByExamInfoId(GraduateExamRoomsQuery examRoom);

    Page<SelectDto> listRoom(SelectDto condition);

    void updateRoomNumberByList(List<ExamInfoRoomDto> dtoList);

    /**根据infoId 查询所有教室Id*/
    List<Long> listRoomsByExamInfoIds(@Param("list") List<Long> examInfoIds);

    /**通过Id查询空闲考场*/
    List<GraduateExamRoom> findSurplusRoom(@Param("list") List<Long> examRoomIds);

    /**校验考场人数是否已满*/
    int checkNum(Long id);

    List<ExamRoomDto> getExamRoomCampus(Long examRoomId);

    GraduateExamRoom getExamRoomNumber(Long examRoomId);

    /**物业单和巡考单*/
    List<PropertySheetDto> listExamRoomAndExamInfo(GraduateExamMessageQuery condition);
}