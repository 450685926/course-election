package com.server.edu.election.controller;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.service.ElcNoGraduateStdsService;
import com.server.edu.election.vo.ElcNoGraduateStdsVo;
import com.server.edu.util.CollectionUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @description: 留学结业生表
 * @author: bear
 * @create: 2019-02-22 09:45
 */

@SwaggerDefinition(info = @Info(title = "留学结业生", version = ""))
@RestSchema(schemaId = "OverseasAndGraduatesController")
@RequestMapping("/overseasOrGraduate")
public class OverseasAndGraduatesController {

    @Autowired
    private ElcNoGraduateStdsService stdsService;

    @ApiOperation(value = "查询留学结业生")
    @PostMapping("/findOverseasOrGraduate")
    public RestResult<PageResult<ElcNoGraduateStdsVo>> findOverseasOrGraduate(@RequestBody PageCondition<ElcNoGraduateStdsVo> condition){
        if(condition.getCondition().getMode()==null){
            return RestResult.fail("common.parameterError");
        }
        PageResult<ElcNoGraduateStdsVo> overseasOrGraduate = stdsService.findOverseasOrGraduate(condition);
        return RestResult.successData(overseasOrGraduate);
    }


    @ApiOperation(value = "新增留学结业生")
    @PostMapping("/addOverseasOrGraduate")
    public RestResult<String> addOverseasOrGraduate(@RequestBody List<String> studentCodes,Integer mode){
        if(mode==null){
            return RestResult.fail("common.parameterError");
        }
        String s = stdsService.addOverseasOrGraduate(studentCodes, mode);
        return RestResult.success(s);
    }

    @ApiOperation(value = "删除留学结业生")
    @PostMapping("/deleteOverseasOrGraduate")
    public RestResult<String> deleteOverseasOrGraduate(@RequestBody List<Long> ids){
        if(CollectionUtil.isEmpty(ids)){
            return RestResult.fail("common.parameterError");
        }
        String s = stdsService.deleteOverseasOrGraduate(ids);
        return RestResult.success(s);
    }
}
