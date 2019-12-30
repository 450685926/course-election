package com.server.edu.exam.rpc;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.entity.StudentScore;
import com.server.edu.common.rest.RestResult;

import java.util.List;

/**
 * @description: 成绩服务调用
 * @author: bear
 * @create: 2019-12-30 14:43
 */
public class ScoreServiceExamInvoker {
    //设置考试情况为重修
    public static RestResult setExamSituationRebuild(List<StudentScore> scores){
        RestResult result =
                ServicePathEnum.SCORESERVICE.postForObject(
                        "/studentScoreQuery/submitSpecialStudentScore",
                        scores,RestResult.class);
        return result;
    }
}
