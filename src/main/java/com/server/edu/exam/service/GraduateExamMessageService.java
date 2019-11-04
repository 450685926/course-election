package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.dto.ExportExamInfoDto;
import com.server.edu.exam.query.GraduateExamMessageQuery;
import com.server.edu.exam.vo.GraduateExamMessage;
import com.server.edu.util.excel.export.ExcelResult;
import org.springframework.web.bind.annotation.RequestParam;

public interface GraduateExamMessageService {
    /**
     * 查询研究生排考信息
     *
     * @author bear
     * @date 2019/9/6 16:41
     */
    PageResult<GraduateExamMessage> listGraduateExamMessage(PageCondition<GraduateExamMessageQuery> condition);

    /**
     * 导出
     */
    ExcelResult export(GraduateExamMessageQuery condition);

    /**
     * 导出学生名单
     */
    ExcelResult exportStuList(ExportExamInfoDto exportExamInfoDto);

    /**
     * 导出签到表
     */
    ExcelResult exportCheckTable( Long calendarId,Integer examType,String calendarName);

    /**
     * 导出物业单
     */
    ExcelResult exportPropertySheet(GraduateExamMessageQuery condition);

    /**
     * 导出巡考单
     */
    ExcelResult exportInspectionSheet(GraduateExamMessageQuery condition);

    ExcelResult exportCheckTableFreemarker(Long calendarId, Integer examType, String calendarName);
}
