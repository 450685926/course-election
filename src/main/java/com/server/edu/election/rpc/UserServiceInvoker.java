package com.server.edu.election.rpc;

import java.util.ArrayList;
import java.util.List;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;

public class UserServiceInvoker
{
    public static List<Long> findGroupListByUserId(String userId) {
    	List<Long> list = new ArrayList<>();
        @SuppressWarnings("unchecked")
        RestResult<List<Long>> result = ServicePathEnum.USER.getForObject(
                "/userGroup/findGroupIdListByUserId?userId={userId}",
                RestResult.class,
                userId);
        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()) {
        	list = result.getData();
        }
        return list;
    }



}
