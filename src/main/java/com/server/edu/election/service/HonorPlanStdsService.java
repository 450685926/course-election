package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.query.HonorPlanStdsQuery;
import com.server.edu.election.vo.HonorPlanStdsVo;
import com.server.edu.util.excel.export.ExcelResult;


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
}
