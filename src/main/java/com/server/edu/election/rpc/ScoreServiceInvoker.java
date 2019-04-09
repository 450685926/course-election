package com.server.edu.election.rpc;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.StudentScoreVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 成绩模块微服务调用
 *
 * @author OuYangGuoDong
 * @version [版本号, 2018年12月5日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ScoreServiceInvoker {
    private static Logger LOG =
            LoggerFactory.getLogger(ScoreServiceInvoker.class);


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


}
