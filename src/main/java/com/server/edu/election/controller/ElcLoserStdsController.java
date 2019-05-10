package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.LoserStuElcCourse;
import com.server.edu.election.service.ElcLoserStdsService;
import com.server.edu.election.vo.ElcLoserStdsVo;
import com.server.edu.util.CollectionUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.commons.lang.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @description: 预警学生
 * @author: bear
 * @create: 2019-05-09 16:42
 */
@SwaggerDefinition(info = @Info(title = "预警学生", version = ""))
@RestSchema(schemaId = "ElcLoserStdsController")
@RequestMapping("/loserStudent")
public class ElcLoserStdsController {

    @Autowired
    private ElcLoserStdsService elcLoserStdsService;

    @ApiOperation(value = "查询预警学生名单")
    @PostMapping("/findElcLoserStds")
    public RestResult<PageResult<ElcLoserStdsVo>> findElcLoserStds(@RequestBody PageCondition<ElcLoserStdsVo> condition){
        if(condition.getCondition().getCalendarId()==null){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        PageResult<ElcLoserStdsVo> elcLoserStds = elcLoserStdsService.findElcLoserStds(condition);
        return RestResult.successData(elcLoserStds);
    }

    @ApiOperation(value = "学生选课结果")
    @GetMapping("/findStudentElcCourse")
    public RestResult<List<LoserStuElcCourse>> findStudentElcCourse(@RequestParam Long calendarId, String studentId){
        if(calendarId==null || StringUtils.isBlank(studentId)){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }

        List<LoserStuElcCourse> list=elcLoserStdsService.findStudentElcCourse(calendarId,studentId);
        return RestResult.successData(list);
    }

    @ApiOperation(value = "移除预警学生")
    @GetMapping("/deleteLoserStudent")
    public RestResult<String> deleteLoserStudent(@RequestBody List<Long> ids){
        if(CollectionUtil.isEmpty(ids)){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }

        String s=elcLoserStdsService.deleteLoserStudent(ids);
        return RestResult.success(I18nUtil.getMsg(s));
    }


    @ApiOperation(value = "预警学生退课")
    @PostMapping("/withdrawCourse")
    public RestResult<?> withdrawCourse(@RequestBody List<LoserStuElcCourse> ids){
        if(CollectionUtil.isEmpty(ids)){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        elcLoserStdsService.withdrawCourse(ids);
        return RestResult.success();
    }


}
