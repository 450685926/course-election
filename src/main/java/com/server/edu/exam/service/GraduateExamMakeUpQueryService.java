package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import com.server.edu.util.excel.export.ExcelResult;

public interface GraduateExamMakeUpQueryService {
    /**
    * 查询补缓考应考学生
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/11 16:32
    */
    PageResult<GraduateExamApplyExaminationVo> listExamMakeUpQuery(PageCondition<GraduateExamApplyExaminationVo> condition);

    /**
    *  导出
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/11 17:01
    */
    ExcelResult export(GraduateExamApplyExaminationVo condition);
}
