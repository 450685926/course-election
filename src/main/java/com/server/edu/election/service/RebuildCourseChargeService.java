package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.RebuildCourseDto;
import com.server.edu.election.dto.RebuildCoursePaymentCondition;
import com.server.edu.election.entity.RebuildCourseCharge;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.util.excel.export.ExcelResult;

import java.util.List;

public interface RebuildCourseChargeService {
    PageResult<RebuildCourseCharge> findCourseCharge(PageCondition<RebuildCourseCharge> condition);

    String deleteCourseCharge(List<Long> ids);

    String editCourseCharge(RebuildCourseCharge courseCharge);

    String addCourseCharge(RebuildCourseCharge courseCharge);

    PageResult<RebuildCourseNoChargeType> findCourseNoChargeType(PageCondition<RebuildCourseNoChargeType> condition);

    String addCourseNoChargeType(RebuildCourseNoChargeType noChargeType);

    String deleteCourseNoChargeType(List<Long> ids);

    String editCourseNoChargeType(RebuildCourseNoChargeType courseNoCharge);

    //查询未缴费课程名单
    PageResult<RebuildCourseNoChargeList> findCourseNoChargeList(PageCondition<RebuildCourseDto> condition);

    //查询学生未缴费课程门数
    PageResult<StudentVo> findCourseNoChargeStudentList(PageCondition<RebuildCourseDto > condition);
    /**移动到回收站*/
    String moveToRecycle(List<RebuildCourseNoChargeList> list);

    /**查询回收站*/
    PageResult<RebuildCourseNoChargeList> findRecycleCourse(PageCondition<RebuildCourseDto> condition);

    /**从回收站回复数据*/
    String moveRecycleCourseToNoChargeList(List<RebuildCourseNoChargeList> list);

    /**导出未缴费课程名单*/
    String exportNoChargeList(RebuildCourseDto condition) throws Exception;

    /**导出课程汇总名单*/
    String exportStudentNoChargeCourse(RebuildCoursePaymentCondition condition) throws Exception;

    /**导出未缴费重修名单*/
    ExcelResult export(RebuildCourseDto condition);
}

