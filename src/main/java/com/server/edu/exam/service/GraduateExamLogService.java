package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.entity.GraduateExamLog;
import com.server.edu.exam.query.GraduateExamLogQuery;
import com.server.edu.exam.vo.GraduateExamLogVo;

public interface GraduateExamLogService {
    PageResult<GraduateExamLogVo> listGraduateExamLog(PageCondition<GraduateExamLogQuery> logVo);
    void addGraduateExamLog(GraduateExamLog examLog);
}
