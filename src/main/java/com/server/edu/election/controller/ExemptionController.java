package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.log.LogRecord;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dmskafka.entity.AuditType;
import com.server.edu.election.dto.ExemptionCourseScoreDto;
import com.server.edu.election.entity.ExemptionCourse;
import com.server.edu.election.entity.ExemptionCourseRule;
import com.server.edu.election.service.ExemptionCourseService;
import com.server.edu.election.vo.ExemptionCourseRuleVo;
import com.server.edu.election.vo.ExemptionCourseScoreVo;
import com.server.edu.election.vo.ExemptionCourseVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @description: 免修免考管理
 * @author: bear
 * @create: 2019-01-31 09:26
 */

@SwaggerDefinition(info = @Info(title = "免修免考管理", version = ""))
@RestSchema(schemaId = "ExemptionController")
@RequestMapping("/Exemption")
public class ExemptionController {

    @Autowired
    private ExemptionCourseService exemptionCourseService;


    /**
    *@Description: 查询免修免考课程
    *@Param:
    *@return:
    *@Author: bear
    *@date: 2019/1/31 9:29
    */

    @ApiOperation(value = "查询免修免考课程信息")
    @PostMapping("/findExemptionCourse")
    public RestResult<PageResult<ExemptionCourseVo>> findMasterCourse(@RequestBody PageCondition<ExemptionCourse> condition) {
        PageResult<ExemptionCourseVo> exemptionCourse = exemptionCourseService.findExemptionCourse(condition);
        return RestResult.successData(exemptionCourse);
    }

    @LogRecord(title="删除免修免考课程",type = AuditType.DELETE)
    @ApiOperation(value = "删除免修免考课程")
    @PostMapping("/deleteExemptionCourse")
     public RestResult<String> deleteExemptionCourseByIds(@RequestBody List<Long> ids){
        String s = exemptionCourseService.deleteExemptionCourse(ids);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="新增免修免考课程",type = AuditType.INSERT)
    @ApiOperation(value = "新增免修免考课程")
    @PostMapping("/addExemptionCourse")
    public RestResult<String> addExemptionCourse(@RequestBody ExemptionCourse exemptionCourse){
        String s = exemptionCourseService.addExemptionCourse(exemptionCourse);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @LogRecord(title="修改免修免考课程",type = AuditType.UPDATE)
    @ApiOperation(value = "修改免修免考课程")
    @PostMapping("/editExemptionCourse")
    public RestResult<String> editExemptionCourse(@RequestBody ExemptionCourse exemptionCourse){
        String s = exemptionCourseService.updateExemptionCourse(exemptionCourse);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }

    @ApiOperation(value = "查询免修免考入学成绩")
    @PostMapping("/findExemptionCourseScore")
    public RestResult<PageResult<ExemptionCourseScoreVo>> findExemptionCourseScore(@RequestBody PageCondition<ExemptionCourseScoreDto> condition) {
        PageResult<ExemptionCourseScoreVo> exemptionScore = exemptionCourseService.findExemptionScore(condition);
        return RestResult.successData(exemptionScore);
    }

    //免修免考成绩导入todo

    @ApiOperation(value = "查询免修免考申请规则")
    @PostMapping("/findExemptionCourseRule")
    public RestResult<PageResult<ExemptionCourseRuleVo>> findExemptionCourseRule(@RequestBody PageCondition<ExemptionCourseRule> courseRule){
        PageResult<ExemptionCourseRuleVo> exemptionRule = exemptionCourseService.findExemptionRule(courseRule);
        return RestResult.successData(exemptionRule);
    }


    @LogRecord(title="删除免修免考申请规则",type = AuditType.DELETE)
    @ApiOperation(value = "删除免修免考申请规则")
    @PostMapping("/deleteExemptionCourseRule")
    public RestResult<String> deleteExemptionCourseRule(@RequestBody List<Long> ids, @RequestParam Integer applyType){
        String s = exemptionCourseService.deleteExemptionCourseRule(ids, applyType);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


    @LogRecord(title="新增免修免考申请规则",type = AuditType.INSERT)
    @ApiOperation(value = "新增免修免考申请规则")
    @PostMapping("/addExemptionCourseRule")
    public RestResult<String> addExemptionCourseRule(@RequestBody ExemptionCourseRuleVo courseRuleVo,@RequestParam Integer applyType){
        String s = exemptionCourseService.addExemptionCourseRule(courseRuleVo, applyType);
        return RestResult.success(I18nUtil.getMsg(s,""));
    }


}
