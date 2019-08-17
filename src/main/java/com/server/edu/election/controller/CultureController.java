package com.server.edu.election.controller;


import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.LoserStuElcCourse;
import com.server.edu.election.service.AchievementLevelService;
import com.server.edu.election.service.CultureService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.commons.lang.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@SwaggerDefinition(info = @Info(title = "培养调用", version = ""))
@RestSchema(schemaId = "CultureController")
@RequestMapping("/culture")
public class CultureController {

    @Autowired
    private CultureService cultureService;


    @ApiOperation(value = "查询学生入学第一外国语国标")
    @PostMapping ("/findStudentFirstLanguageCode")
    @ResponseBody
    public RestResult<Map<String,String>> findStudentFirstLanguageCode(@RequestBody List<String> studentIds){
        if(studentIds==null || studentIds.size()==0){
            return RestResult.fail(I18nUtil.getMsg("baseresservice.parameterError"));
        }
     Map<String,String> map= cultureService.findStudentFirstLanguageCode(studentIds);

        return RestResult.successData(map);
    }
}
