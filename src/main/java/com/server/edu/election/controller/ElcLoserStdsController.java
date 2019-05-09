package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.service.ElcLoserStdsService;
import com.server.edu.election.vo.ElcLoserStdsVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

}
