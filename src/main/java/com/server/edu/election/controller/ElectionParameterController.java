package com.server.edu.election.controller;

import javax.validation.Valid;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.service.ElectionParameterService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课规则参数", version = ""))
@RestSchema(schemaId = "ElectionParameterController")
@RequestMapping("electionParameter")
public class ElectionParameterController {
	
	private static Logger LOG =
	        LoggerFactory.getLogger(ElectionParameterController.class);
	@Autowired
	private ElectionParameterService electionParameterService;
	
	 /**
     * 修改参数状态
     * 
     * @param electionParameter
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改参数状态")
    @PostMapping("/updateParameter")
    public RestResult<Integer> updateParameter(
        @Valid @RequestBody ElectionParameter electionParameter)
        throws Exception
    {
        LOG.info("updateStatus.start");
        int result =electionParameterService.updateParameter(electionParameter);
        return RestResult.successData(result);
    }

}
