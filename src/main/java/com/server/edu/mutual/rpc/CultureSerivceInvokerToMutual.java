package com.server.edu.mutual.rpc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.vo.PlanCourseTabVo;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.CultureScheme;
import com.server.edu.common.entity.LabelCreditCount;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.mutual.controller.ElcMutualApplyController;

/**
 * 培养计划微服务调用
 *
 * @author LuoXiaoLi
 * @version [版本号, 2019年11月1日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CultureSerivceInvokerToMutual {
	private static Logger LOG =
	        LoggerFactory.getLogger(CultureSerivceInvokerToMutual.class);
    
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
	
	/**
	 * 获取本科生培养计划里的所有课程代码
	 * @param studentId
	 * @return
	 */
	public static List<String> getCulturePlanCourseCodeByStudentId(String studentId){
		RestResult result = ServicePathEnum.CULTURESERVICE
                .getForObject("/bclCulturePlan/findPlanCourseTab?studentID={0}", RestResult.class,studentId);
		LOG.info("findPlanCourseTab return value:"+JSONObject.toJSONString(result));

		if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()&&null!=result.getData())
        {
			String json =JSONObject.toJSON(result.getData()).toString();
			List<PlanCourseTabVo> ls = JSONArray.parseArray(json, PlanCourseTabVo.class);
			List<String> list=new ArrayList<>();
			ls.stream().forEach(v->{
				list.add(v.getCourseCode());
			});
        	return list;
        }
        return Collections.emptyList();
	}

    /**
     * 功能描述: 获取本科生培养计划里的所有课程代码(获取多个学生的)
     *
     * @params: [studentId]
     * @return: java.util.List<java.lang.String>
     * @author: zhaoerhu
     * @date: 2020/3/16 9:34
     */
    public static List<String> getCulturePlanCourseCodeByStudentIdForJD(String studentIds){
        RestResult result = ServicePathEnum.CULTURESERVICE
                .getForObject("/bclCulturePlan/findPlanCourseTabForJD?studentID={0}", RestResult.class,studentIds);
        LOG.info("findPlanCourseTabForJD return value:"+JSONObject.toJSONString(result));

        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()&&null!=result.getData())
        {
			String json = JSONObject.toJSON(result.getData()).toString();
			List<String> list = JSONArray.parseArray(json, String.class);
			return list;
        }
        return Collections.emptyList();
    }

	public static List<String> studentPlanCourseCode(String studentId)
    {
		RestResult resultList = ServicePathEnum.CULTURESERVICE
            .getForObject("/bclCulturePlan/getCourseCode?studentId={studentId}&isPass={isPass}", RestResult.class, studentId,1);
		String json ="";
		List<String> list = new ArrayList<>();
		LOG.info("studentPlanCourseCode"+JSONObject.toJSONString(resultList));
		if(resultList.getCode() == 200 && null != resultList.getData() && StringUtils.isNotEmpty(resultList.getData().toString())) {
			json =JSONObject.toJSON(resultList.getData()).toString();
		}
		if(StringUtils.isNotEmpty(json)) {
			list = JSONArray.parseArray(json, String.class);
		}
		
        return list;
    }

	/**
	 * 功能描述: 更新学生培养计划
	 *
	 * @params: [elcMutualApply]
	 * @return: com.server.edu.common.rest.RestResult
	 * @author: zhaoerhu
	 * @date: 2020/2/18 15:11
	 */
	public static RestResult updateCulturePlan4Stu(ElcMutualApplyDto elcMutualApply) {
		String studentID = elcMutualApply.getStudentId();
		String courseCode = elcMutualApply.getCourseCode();
		Long semester = elcMutualApply.getSemester();
		Long calendarId = elcMutualApply.getCalendarId();
		LOG.info("updateCulturePlan4Stu studentID:" + studentID);
		LOG.info("updateCulturePlan4Stu courseCode:" + courseCode);
		LOG.info("updateCulturePlan4Stu semester:" + semester);
		LOG.info("updateCulturePlan4Stu calendarId:" + calendarId);
		RestResult restResult = ServicePathEnum.CULTURESERVICE.
				getForObject("/bclCulturePlan/addCourseToPlan?studentID={studentID}&courseCode={courseCode}&semester={semester}&calendarId={calendarId}",
						RestResult.class, studentID, courseCode, semester,calendarId);
		return restResult;
	}
	
	/**
	 * 获取本科生培养方案获取模板id
	 * @param studentId
	 * @return
	 */
	public static List<Long> getStudentCultureScheme(String studentId){
//		String res = "";
		RestResult result = ServicePathEnum.CULTURESERVICE
                .getForObject("/bclStudentCultureRel/queryStudentCultureScheme?stuid={0}", RestResult.class,studentId);
		LOG.info(" queryStudentCultureScheme return value:"+JSONObject.toJSONString(result));
		
		if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()&&null!=result.getData())
        {
			List<Long> list=new ArrayList<>(); 
			String json = JSONObject.toJSON(result.getData()).toString();
			List<CultureScheme> cultureSchemes = JSONArray.parseArray(json, CultureScheme.class);
			for(CultureScheme cs : cultureSchemes) {
				if(null != cs) {
					
					list.add(cs.getId());
				}
			}
			
			return list;
//			Map<String, Object> parse = (Map)JSON.parse(json);
//			for (String key : parse.keySet()) {
//				if (StringUtils.equals(key, "id")) {
//					res = parse.get(key).toString();
//					break;
//				}
//			}
        }
		return Collections.emptyList();
	}

	/**
	 * 功能描述: 获取本科生培养方案获取模板id(获取多个学生的id)
	 *
	 * @params: [studentIds]
	 * @return: java.util.List<java.lang.Long>
	 * @author: zhaoerhu
	 * @date: 2020/3/16 9:26
	 */
	public static List<Long> getStudentCultureSchemeForJD(String studentIds) {
		RestResult result = ServicePathEnum.CULTURESERVICE
				.getForObject("/bclStudentCultureRel/queryStudentCultureSchemeForJD?studentIds={0}", RestResult.class, studentIds);
		LOG.info(" queryStudentCultureScheme return value:" + JSONObject.toJSONString(result));

		if (null != result
				&& ResultStatus.SUCCESS.code() == result.getCode() && null != result.getData()) {
			String json = JSONObject.toJSON(result.getData()).toString();
			List<Long> list = JSONArray.parseArray(json, Long.class);
			return list;
		}
		return Collections.emptyList();
	}
	
	/**
	 * 获取本科生培养方案课程列表
	 * @param studentId
	 * @return
	 */
	public static List<String> getStudentCultureSchemeCourseCode(Long id){
		RestResult result = ServicePathEnum.CULTURESERVICE
                .getForObject("bclCourseLabelRelation/list/{0}?type=2", RestResult.class,id);
		LOG.info(" findCultureSchemeById return value:"+JSONObject.toJSONString(result));
		
		if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()&&null!=result.getData())
        {
			List<String> list=new ArrayList<>(); 
			String json =JSONObject.toJSON(result.getData()).toString();
			Map<String, Object> parse = (Map)JSON.parse(json);
			for (String key : parse.keySet()) {
				if (StringUtils.equals(key, "courseLabelRelationList")) {
					String courseCodeList = parse.get(key).toString();
					if(StringUtils.isNotEmpty(courseCodeList)) {
						List<Map> ps = JSONArray.parseArray(courseCodeList,Map.class);
						for (Map mp : ps) {
							list.add(mp.get("courseCode").toString());
						}
					}
				}
			}
        	return list;
        }
        return Collections.emptyList();
	}
	
	
}
