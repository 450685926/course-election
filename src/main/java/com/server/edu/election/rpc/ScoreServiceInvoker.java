package com.server.edu.election.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.election.entity.Student;

/**
 * 成绩模块微服务调用
 *
 * @author OuYangGuoDong
 * @version [版本号, 2018年12月5日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ScoreServiceInvoker {
    public static List<StudentScoreVo> findStuScoreBest(String studentCode)
    {
        @SuppressWarnings("unchecked")
        RestResult<List<StudentScoreVo>> restResult =
                ServicePathEnum.SCORESERVICE.getForObject(
                        "/studentScoreCount/findStudentScoreGood/?studentCode={studentCode}",
                        RestResult.class,
                        studentCode);

        return restResult.getData();
    }

    public static List<ScoreStudentResultVo> findStuScore(String studentCode)
    {
        Student student = new Student();
        student.setStudentCode(studentCode);
        @SuppressWarnings("unchecked")
        List<ScoreStudentResultVo> list =
                ServicePathEnum.SCORESERVICE.postForObject(
                        "/studentScoreQuery/getOldScoresNoPagedNoMsg",student,
                        List.class
                        );
        return list;
    }

    public static List<String> findStuFailedCourseCodes(String studentCode)
    {
        @SuppressWarnings("unchecked")
        RestResult<Set<String>> restResult =
                ServicePathEnum.SCORESERVICE.getForObject(
                        "/studentScoreCount/findStuFailedCourseCode?studentCode={studentCode}",
                        RestResult.class,
                        studentCode);
        Set<String> set = restResult.getData();
        List<String> list = new ArrayList<>(set.size());
        list.addAll(set);
        return list;
    }

    public static StudentScore findViolationStu(String studentCode,String courseCode,Long calendarId)
    {
        @SuppressWarnings("unchecked")
        RestResult<StudentScore> restResult =
                ServicePathEnum.SCORESERVICE.getForObject(
                        "/studentScoreCount/findViolationStu/?studentCode={studentCode}&courseCode={courseCode}&calendarId={calendarId}",
                        RestResult.class,
                        studentCode,courseCode,calendarId);

        return restResult.getData();
    }

    public static PageResult<StudentScore> findUnPassStuScore(PageCondition<String> condition)
    {
        @SuppressWarnings("unchecked")
        RestResult<PageResult<StudentScore>> restResult =
                ServicePathEnum.SCORESERVICE.postForObject(
                        "/studentScoreCount/findUnPassStuScore",
                        condition, RestResult.class);

        return restResult.getData();
    }

    public static RestResult<List<StudentScore>> findPassStuScore(List<StudentScore> unPassList)
    {
        @SuppressWarnings("unchecked")
        RestResult<List<StudentScore>> restResult =
                ServicePathEnum.SCORESERVICE.postForObject(
                        "/studentScoreCount/findPassStuScore",
                        unPassList, RestResult.class);

        return restResult;
    }

    public static List<StudentScoreVo> findStuScoreByCalendarIdAndStudentCode(Long calendarId,String studentCode)
    {
        @SuppressWarnings("unchecked")
        RestResult<List<StudentScoreVo>> restResult =
                ServicePathEnum.SCORESERVICE.getForObject(
                        "/studentScoreCount/findStuScoreByCalendarIdAndStudentCode/?calendarId={calendarId}&studentCode={studentCode}",
                        RestResult.class,
                        calendarId,
                        studentCode);

        return restResult.getData();
    }
    /**
     * 向成绩表插入免修免考成绩
     */
    public static RestResult saveExemptionScore(JSONObject jsonObject)
    {
    	@SuppressWarnings("unchecked")
        RestResult restResult =
                ServicePathEnum.SCORESERVICE.postForObject(
                        "/teachingClassScore/saveExemptionScore",
                        jsonObject, RestResult.class);

        return restResult;
    }
    /**
     * 删除成绩表免修免考成绩
     */
    public static RestResult deleteExemptionScore(String studentCode, Long calendarId, String courseCode)
    {
    	@SuppressWarnings("unchecked")
    	RestResult restResult =
    	ServicePathEnum.SCORESERVICE.getForObject("/teachingClassScore/deleteExemptionScore/{calendarId}/{studentId}/{courseCode}", 
    			RestResult.class, calendarId,studentCode,courseCode);
    			
    	return restResult;
    }

    /**
     *  查询学生课程是否有成绩
     * @param studentCode
     * @return
     */
    public static List<String> findCourseHaveScore(String studentCode, Long calendarId, List<String> courseCodes)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("studentCode", studentCode);
        jsonObject.put("calendarId", calendarId);
        jsonObject.put("courseCodes", courseCodes);
        @SuppressWarnings("unchecked")
        List<String> list =
                ServicePathEnum.SCORESERVICE.postForObject(
                        "/studentScoreCount/findCourseHaveScore",
                        jsonObject, List.class);
        return list;
    }

    /**
     *  查询学生课程是否有成绩
     * @param studentCode
     * @return
     */
    public static List<String> findCourseHaveScore2(String courseCode, Long calendarId, List<String> studentCodes)
    {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("studentCode", studentCodes);
        jsonObject.put("calendarId", calendarId);
        jsonObject.put("courseCodes", courseCode);
        @SuppressWarnings("unchecked")
        List<String> list =
                ServicePathEnum.SCORESERVICE.postForObject(
                        "/studentScoreCount/findViolationStus",
                        jsonObject, List.class);
        return list;
    }
}
