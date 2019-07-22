package com.server.edu.election.controller;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElcClassEditAuthority;
import com.server.edu.election.service.ElcClassEditAuthorityService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "容量修改开关", version = ""))
@RestSchema(schemaId = "ElcClassEditAuthorityController")
@RequestMapping("elcClassEditAuthority")
public class ElcClassEditAuthorityController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcClassEditAuthorityController.class);
	@Autowired
	private ElcClassEditAuthorityService elcClassEditAuthorityService;
	
    /**
     *获取容量开关信息
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取容量开关信息")
    @PostMapping("/getElcClassEditAuthority")
    public RestResult<ElcClassEditAuthority> getElcNumberSetInfo(
    		@RequestParam("calendarId") @NotNull Long calendarId)
        throws Exception
    {
        LOG.info("getElcClassEditAuthority.start");
        ElcClassEditAuthority elcClassEditAuthority =elcClassEditAuthorityService.getElcClassEditAuthority(calendarId);
        return RestResult.successData(elcClassEditAuthority);
    }
	
	
    /**
     * 保存
     * 
     * @param elcNumberSet
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "保存")
    @PostMapping("/save")
    public RestResult<Integer> save(
    		@RequestBody @Valid ElcClassEditAuthority elcClassEditAuthority)
        throws Exception
    {
        LOG.info("save.start");
        int result =elcClassEditAuthorityService.save(elcClassEditAuthority);
        return RestResult.successData(result);
    }

}
