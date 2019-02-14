package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.entity.RollBookList;

public interface ReportManagementService {
    /**点名册*/
    PageResult<RollBookList> findRollBookList(PageCondition<ReportManagementCondition> condition);
}
