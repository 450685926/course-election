package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.GraduateExcelDto;
import com.server.edu.election.vo.ElcNoGraduateStdsVo;

public interface ElcNoGraduateStdsService {
    /**查询结业生名单*/
    PageResult<ElcNoGraduateStdsVo> findOverseasOrGraduate(PageCondition<ElcNoGraduateStdsVo> condition);

    /**新增结业生名单*/
    String addOverseasOrGraduate(List<String> studentCodes, Integer mode);

    /**删除结业生*/
    String deleteOverseasOrGraduate(List<String> codes);

    /**批量导入结业生*/
    String addExcel(List<GraduateExcelDto> datas, Integer mode);
}
