package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.vo.RebuildCourseNoChargeTypeVo;
import com.server.edu.election.vo.StudentRebuildFeeVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RebuildCourseNoChargeTypeDao extends Mapper<RebuildCourseNoChargeType> {


    Page<RebuildCourseNoChargeType> findCourseNoChargeType(RebuildCourseNoChargeTypeVo condition);

    void deleteRebuildCourseNoChargeType(List<Long> list);

    List<RebuildCourseNoChargeType> findTypeByCondition(RebuildCourseNoChargeType noChargeType);

    /**查询异动大类对应学号和异动大类*/
    List<StudentRebuildFeeVo> getAbnormalStudent(@Param("list") List<String> list,@Param("time") Long time);
}
