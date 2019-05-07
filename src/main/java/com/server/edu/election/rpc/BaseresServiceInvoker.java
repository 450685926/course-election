package com.server.edu.election.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.Classroom;
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.common.vo.SchoolCalendarVo;

public class BaseresServiceInvoker
{

    @SuppressWarnings("unchecked")
    public static SchoolCalendarVo getSchoolCalendarById(Long id) {
        RestResult<SchoolCalendarVo> schoolCalendarVoResult = ServicePathEnum.BASESERVICE
                .getForObject("/schoolCalendar/{id}", RestResult.class, id);
        if (null != schoolCalendarVoResult
                && ResultStatus.SUCCESS.code() == schoolCalendarVoResult.getCode())
        {
            return schoolCalendarVoResult.getData();
        }
        return null;
    }
    @SuppressWarnings("unchecked")
    public static Long getSchoolCalendarById(Long id, Integer weekNum,
        Integer weekDay)
    {
        RestResult<Long> schoolCalendarVoResult =
            ServicePathEnum.BASESERVICE.getForObject(
                "/schoolCalendar/time?id={id}&weekNum={weekNum}&weekDay={weekDay}",
                RestResult.class,
                id,
                weekNum,
                weekDay);
        if (null != schoolCalendarVoResult
            && ResultStatus.SUCCESS.code() == schoolCalendarVoResult.getCode())
        {
            return schoolCalendarVoResult.getData();
        }
        return null;
    }

    public static List<SchoolCalendarVo> getSchoolCalendarList()
    {
        
        List<SchoolCalendarVo> schoolCalendarList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        RestResult<List<SchoolCalendarVo>> schoolCalendarResult =
            ServicePathEnum.BASESERVICE
                .getForObject("/schoolCalendar/list?&year=", RestResult.class);
        if (null != schoolCalendarResult
            && ResultStatus.SUCCESS.code() == schoolCalendarResult.getCode())
        {
            schoolCalendarList = schoolCalendarResult.getData();
        }
        
        return schoolCalendarList;
    }

    
    public static List<Classroom> getClassRoomByTower(String tower) {
    	List<Classroom> list =  new ArrayList<>();
        @SuppressWarnings("unchecked")
        RestResult<List<Classroom>> result =  ServicePathEnum.BASESERVICE.getForObject(
                "/classroom/findClassroomByTower?tower={tower}",
                RestResult.class,
                tower);
        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode())
            {
        	 list = result.getData();
            }
        return list;
    }
    
    public static Classroom getClassRoomById(Long id) {
        Classroom classroom = new Classroom();
        @SuppressWarnings("unchecked")
        RestResult<Classroom> result = ServicePathEnum.BASESERVICE.getForObject(
                "/classroom/findClassroomById?id={id}",
                RestResult.class,
                id);
        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()) {
            classroom = result.getData();
        }
        return classroom;
    }
    
    public static Teacher findTeacherBycode(String code) {
    	Teacher teacher = new Teacher();
        @SuppressWarnings("unchecked")
        RestResult<Teacher> result = ServicePathEnum.BASESERVICE.getForObject(
                "/classroom/findClassroomById?code={code}",
                RestResult.class,
                code);
        if (null != result
                && ResultStatus.SUCCESS.code() == result.getCode()) {
        	teacher = result.getData();
        }
        return teacher;
    }
    
    public static Map<String,Object> getSchoolCalendarByTime(Long time)
    {
        
    	Map<String,Object> map  = new HashMap<String,Object>();
        @SuppressWarnings("unchecked")
        RestResult<Map<String,Object>> schoolCalendarResult =
            ServicePathEnum.BASESERVICE
                .getForObject("/schoolCalendar/result/time?time={time}", RestResult.class,time);
        if (null != schoolCalendarResult
            && ResultStatus.SUCCESS.code() == schoolCalendarResult.getCode())
        {
        	map = schoolCalendarResult.getData();
        }
        
        return map;
    }


    public static SchoolCalendarVo getPreSemester(Long calendarId){
        RestResult<SchoolCalendarVo> preSchoolCalendar= ServicePathEnum.BASESERVICE.getForObject("/schoolCalendar/lastTermCalendar?calendarId={calendarId}", RestResult.class, calendarId);
        return preSchoolCalendar.getData();
    }

}
