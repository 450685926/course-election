package com.server.edu.exam.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.ExamMakeUp;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.dto.SelectDto;
import com.server.edu.exam.entity.GraduateExamApplyExamination;
import com.server.edu.exam.service.GraduateExamApplyExaminationService;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
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
 * @description: 研究生补缓考申请列表
 * @author: bear
 * @create: 2019-08-29 09:28
 */

@SwaggerDefinition(info = @Info(title = "研究生补缓考申请列表", version = ""))
@RestSchema(schemaId = "GraduateExamApplyExaminationController")
@RequestMapping("/graduateApply")
public class GraduateExamApplyExaminationController {
    private static Logger LOG =
            LoggerFactory.getLogger(GraduateExamApplyExaminationController.class);

    @Autowired
    private GraduateExamApplyExaminationService applyExaminationService;

    @PostMapping("/addGraduateApply")
    public RestResult<?> addGraduateApply(@RequestBody GraduateExamApplyExamination applyExamination){
        applyExaminationService.addGraduateApply(applyExamination);
        return RestResult.success();
    }

    @PostMapping("/listGraduateApply")
    public RestResult<PageResult<GraduateExamApplyExaminationVo>> listGraduateApply(@RequestBody PageCondition<GraduateExamApplyExaminationVo> applyExamination){
        PageResult<GraduateExamApplyExaminationVo> pageResult = applyExaminationService.listGraduateApply(applyExamination);
        return RestResult.successData(pageResult);
    }

    @PostMapping("/examineGraduateApply")
    public RestResult<?> examineGraduateApply(@RequestBody List<Long> ids, @RequestParam Integer status,@RequestParam String aduitOpinions){
         applyExaminationService.examineGraduateApply(ids,status,aduitOpinions);
        return RestResult.success();
    }

    @PostMapping("/applyStudentList")
    public RestResult<PageResult<SelectDto>> applyStudentList(@RequestBody PageCondition<SelectDto> condition){
        PageResult<SelectDto> pageResult = applyExaminationService.applyStudentList(condition);
        return RestResult.successData(pageResult);
    }

    @PostMapping("/applyCourseList")
    public RestResult<PageResult<SelectDto>> applyCourseList(@RequestBody PageCondition<SelectDto> condition){
        PageResult<SelectDto> pageResult = applyExaminationService.applyCourseList(condition);
        return RestResult.successData(pageResult);
    }

    @PostMapping("/makeUpCourseList")
    public RestResult<PageResult<ExamMakeUp>> makeUpCourseList(@RequestBody PageCondition<ExamMakeUp> condition){
        PageResult<ExamMakeUp> pageResult = applyExaminationService.makeUpCourseList(condition);
        return RestResult.successData(pageResult);
    }

}
