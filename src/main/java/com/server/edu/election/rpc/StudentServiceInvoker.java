package com.server.edu.election.rpc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.AbnormalTypeElection;
import com.server.edu.common.entity.TeacherInfo;
import com.server.edu.common.query.TeacherInfoQuery;
import com.server.edu.common.rest.BaseUser;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.election.service.impl.NoSelectStudentServiceImpl;

/**
 * 学生微服务调用
 *
 * @author
 * @version [版本号, 2018年12月5日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class StudentServiceInvoker {
	
	private static Logger LOG = LoggerFactory.getLogger(StudentServiceInvoker.class);
	
    /**
     * 根据code查询老师信息
     */

    public static TeacherInfo findTeacherInfoBycode(String code) {
        TeacherInfo teacherInfo = new TeacherInfo();
        @SuppressWarnings("unchecked")
        RestResult<TeacherInfo> result = ServicePathEnum.STUDENT.getForObject(
                "/teacherInfo/findTeacherInfoBycode?code={code}",
                RestResult.class,
                code);
        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()) {
            teacherInfo = result.getData();
        }
        return teacherInfo;
    }

    /**
     * 
     * 查询老师下拉框信息
     */
    public static List<TeacherInfo> findSelectList(TeacherInfoQuery teacherInfoQuery){
    	List<TeacherInfo> list =  new ArrayList<>();
        @SuppressWarnings("unchecked")
        RestResult<PageInfo<TeacherInfo>> result = ServicePathEnum.STUDENT.postForObject(
                "/teacherInfo/findSelectList",
                teacherInfoQuery,
                RestResult.class);
        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode())
            {
        	  list = result.getData().getList();
            }
        return list;
    }
    /**
     * 根据userId查询用户信息
     */

    public static BaseUser getBaseUser(String uid) {
        BaseUser baseUser = new BaseUser();
        @SuppressWarnings("unchecked")
        RestResult<BaseUser> result = ServicePathEnum.STUDENT.getForObject(
                "/studentInfo/getBaseUser?uid={uid}",
                RestResult.class,
                uid);
        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()) {
            baseUser = result.getData();
        }
        return baseUser;
    }

    /**
     * 查询民族预科生的学生信息
     *
     * @author daichang
     * @date 2019/2/28
     */
    public static List<String> getNationalPreStudentInfo(String enrolCategory, List<String> studentIds) {
        if (null == enrolCategory) {
            enrolCategory = "";
        }
        @SuppressWarnings("unchecked")
        RestResult<List<String>> list = ServicePathEnum.STUDENT.postForObject(String.format("/studentInfo/getNationalPreStudentInfo?enrolCategory=%s", enrolCategory), studentIds, RestResult.class);
        return list.getData();
    }

    /**
     * 根据学生学号集合查询对应的学籍异动大类
     * @author luoxiaoli
     * @date 2019/8/15
     */
    public static List<AbnormalTypeElection> getAbnormalTypeByStudentCode(List<String> studentCodes) {
        @SuppressWarnings("unchecked")
        RestResult<List<AbnormalTypeElection>> list = ServicePathEnum.STUDENT.postForObject("/applyType/getAbnormalTypeByStudentCode",JSONArray.parseArray(JSON.toJSONString(studentCodes)), RestResult.class);
        return list.getData();
    }
}
    
    
