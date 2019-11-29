package com.server.edu.mutual.rpc;

import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.LabelCreditCount;
import com.server.edu.common.rest.RestResult;

/**
 * 培养计划微服务调用
 *
 * @author LuoXiaoLi
 * @version [版本号, 2019年11月1日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CultureSerivceInvokerToMutual {
    
	/**
	 * 学生个人计划统计
	 * @param studentId
	 * @return
	 */
	public static List<LabelCreditCount> studentPlanCountByStuId(String studentId)
    {
		RestResult resultList = ServicePathEnum.CULTURESERVICE
            .getForObject("/culturePlan/studentPlanCountByStuId?studentId={studentId}", RestResult.class, studentId);
		String json =JSONObject.toJSON(resultList.getData()).toString();
		List<LabelCreditCount> list = JSONArray.parseArray(json, LabelCreditCount.class);
        return list;
    }
	
	/**
	 * 根据学生ID获取培养计划
	 * @param studentId
	 * @param isPass
	 * @return
	 */
	public static RestResult getCulturePlanByStudentId(String id, int isPass)
	{
		RestResult<Map<String, Object>> resultList = ServicePathEnum.CULTURESERVICE
				.getForObject("/culturePlan/getCulturePlanByStudentId?id={id}&isPass={isPass}", RestResult.class, id, isPass);
		return resultList;
	}
	
	
	
	
	
}
