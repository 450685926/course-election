package com.server.edu.election.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.StudentScoreVo;

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
}
