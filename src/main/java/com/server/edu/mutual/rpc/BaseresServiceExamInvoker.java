package com.server.edu.mutual.rpc;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.SchoolCalendar;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;

/**
 * 功能描述: baserservice调用
 *
 * @params:
 * @return:
 * @author: zhaoerhu
 * @date: 2020/2/25 22:49
 */
public class BaseresServiceExamInvoker {

    /**
     * 功能描述: 调用base服务，根据year和term获取学生的calendarId
     *
     * @params: [year, term]
     * @return: java.lang.Long
     * @author: zhaoerhu
     * @date: 2020/2/25 22:48
     */
    public static Long getCalendarId(Integer year, Integer term) {
        RestResult<SchoolCalendar> result = ServicePathEnum.BASESERVICE.getForObject(
                "/schoolCalendar/getCalendarId?year={year}&term={term}",
                RestResult.class, year, term);
        if (null != result && ResultStatus.SUCCESS.code() == result.getCode()) {
            return result.getData().getId();
        }
        return null;
    }

}
