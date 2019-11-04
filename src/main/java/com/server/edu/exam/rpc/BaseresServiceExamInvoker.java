package com.server.edu.exam.rpc;

import com.server.edu.arrangeoccupation.ConflictMessage;
import com.server.edu.arrangeoccupation.OccupationParam;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.Classroom;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.common.vo.SchCalendarTimeVo;

import java.util.List;

/**
 * @description: baserservice调用
 * @author: bear
 * @create: 2019-09-19 14:31
 */
public class BaseresServiceExamInvoker {

    public static SchCalendarTimeVo getTime(Long beginTime, Long endTime) {
        RestResult<SchCalendarTimeVo> schoolCalendarVoResult =
                ServicePathEnum.BASESERVICE.getForObject(
                        "/schoolCalendar/dayAndWorkTime?beginTime={beginTime}&endTime={endTime}",
                        RestResult.class,
                        beginTime,
                        endTime);
        if (null != schoolCalendarVoResult
                && ResultStatus.SUCCESS.code() == schoolCalendarVoResult.getCode()) {
            return schoolCalendarVoResult.getData();
        }
        return null;
    }

    public static RestResult<List<ConflictMessage>> addOccupy(List<OccupationParam> params) {
        RestResult<List<ConflictMessage>> result =
                ServicePathEnum.BASESERVICE.postForObject(
                        "/occupations/add?addition=exam",
                        params,RestResult.class);
        return result;
    }

    public static RestResult<List<ConflictMessage>> delOccupy(List<OccupationParam> params) {
        RestResult<List<ConflictMessage>> result =
                ServicePathEnum.BASESERVICE.postForObject(
                        "/occupations/delete",
                        params,RestResult.class);
        return result;
    }

    //public static RestResult<List<ConflictMessage>> getTeaOccupy(OccupationParam<Long> propties) {
    //    RestResult<List<ConflictMessage>> result =
    //            ServicePathEnum.BASESERVICE.postForObject(
    //                    "/occupations/teacher",
    //                    propties,RestResult.class);
    //    return result;
    //}
    //
    //public static RestResult<List<ConflictMessage>> delTeaOccupy(OccupationParam<Long> propties) {
    //    RestResult<List<ConflictMessage>> result =
    //            ServicePathEnum.BASESERVICE.postForObject(
    //                    "/occupations/teacher/delete",
    //                    propties,RestResult.class);
    //    return result;
    //}
}
