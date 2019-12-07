package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.HonorPlanStds;
import com.server.edu.election.query.HonorPlanStdsQuery;
import com.server.edu.election.vo.HonorPlanStdsVo;
import com.server.edu.util.async.AsyncResult;
import com.server.edu.util.excel.export.ExcelResult;

import java.util.List;


public interface HonorPlanStdsService
{

    /**
     * 列表
     * @param condition
     * @return
     */
    PageResult<HonorPlanStdsVo> listPage(PageCondition<HonorPlanStdsQuery> condition);

    /**
     * 学生名单添加
     * @param honorPlanStdsVo
     */
    RestResult<?> add(HonorPlanStdsVo honorPlanStdsVo);

    /**
     * 学生名单删除
     * @param honorPlanStds
     */
    RestResult<?> delete(HonorPlanStdsQuery honorPlanStds);

    ExcelResult export(HonorPlanStdsQuery condition);

    /**
     * 异步导入
     * @param honorPlanStdsList
     * @return
     */
    AsyncResult addList(List<HonorPlanStds> honorPlanStdsList);

    /**
     * 查看学生是否在本学期的荣誉计划名单内
     * @param studentId
     * @param calendarId
     * @return
     */
    boolean fingStudentByStudentId(String studentId, Long calendarId);
}
