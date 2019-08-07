package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElcNoSelectReason;
import com.server.edu.election.vo.ElcNoSelectReasonVo;
import com.server.edu.util.excel.export.ExcelResult;

/**
 * 
 * 未选课学生
 * 
 */
public interface NoSelectStudentService
{
    /**查询选课名单*/
    PageResult<NoSelectCourseStdsDto> findElectCourseList(PageCondition<NoSelectCourseStdsDto> condition);

    /**未选课原因*/
    String addNoSelectReason(ElcNoSelectReasonVo noSelectReason);

    /**查找未选课原因*/
    ElcNoSelectReason findNoSelectReason(Long calendarId, String studentCode);
    
    /**研究生导出未选课学生名单*/
    String exportStudentNoCourseListGradute(NoSelectCourseStdsDto condition) throws Exception;
    
    /**异步导出*/
    ExcelResult export(NoSelectCourseStdsDto condition);
}
