package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.service.GraduateExamInfoService;
import com.server.edu.exam.service.MyGraduateExamService;
import com.server.edu.exam.vo.MyGraduateExam;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 我的考试
 * @author: bear
 * @create: 2019-09-05 16:52
 */

@SwaggerDefinition(info = @Info(title = "我的考试", version = ""))
@RestSchema(schemaId = "MyGraduateExamController")
@RequestMapping("/graduateMyExam")
public class MyGraduateExamController {
    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamInfoController.class);

    @Autowired
    private MyGraduateExamService myGraduateExamService;

    @PostMapping("/listMyExam")
    public RestResult<PageResult<MyGraduateExam>> listMyExam(@RequestBody PageCondition<MyGraduateExam> myExam){
       PageResult<MyGraduateExam> pageResult = myGraduateExamService.listMyExam(myExam);
       return RestResult.successData(pageResult);
    }

    @PostMapping("/cancelApply")
    public RestResult<?> cancelApply(@RequestBody List<MyGraduateExam> myExam,@RequestParam Integer applyType){
        myGraduateExamService.cancelApply(myExam,applyType);
        return RestResult.success();
    }

    @PostMapping("/addGraduateApplyList")
    public RestResult<?> addGraduateApplyList(@RequestBody List<MyGraduateExam> myExam,@RequestParam Integer applyType,@RequestParam String applyReason){
        myGraduateExamService.addGraduateApplyList(myExam,applyType,applyReason);
        return RestResult.success();
    }

    @PostMapping("/listMyExamTime")
    public RestResult<PageResult<MyGraduateExam>> listMyExamTime(@RequestBody PageCondition<MyGraduateExam> myExam){
        PageResult<MyGraduateExam> pageResult = myGraduateExamService.listMyExamTime(myExam);
        return RestResult.successData(pageResult);
    }


}
