package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface RebuildCourseChargeDao extends Mapper<RebuildCourseCharge> {


    Page<RebuildCourseCharge> findCourseCharge(RebuildCourseCharge condition);

    void deleteCourseCharge(List<Long> list);

    /**移动到回收站*/
    void addCourseStudentToRecycle( List<RebuildCourseNoChargeList> list);

    /**查询回收站*/
    Page<RebuildCourseNoChargeList> findRecycleCourse(RebuildCourseDto condition);

    /**从回收站回复数据*/

    void recoveryDataFromRecycleCourse(List<RebuildCourseNoChargeList> list);

    /**按培养层次和学习形式*/
    RebuildCourseCharge findPrice(@Param("trainingLevel") String trainingLevel,@Param("formLearning") String formLearning);

    List<RebuildCourseCharge> selectByStuId(String studentCode);
}
