package com.server.edu.mutual.controller;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.mutual.dto.ElcMutualListDto;
import com.server.edu.mutual.service.ElcMutualListService;
import com.server.edu.mutual.vo.ElcMutualListVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "本研互选名单", version = ""))
@RestSchema(schemaId = "elcMutualListController")
@RequestMapping("elcMutualList")
public class ElcMutualListController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualListController.class);
	
	@Autowired
	private ElcMutualListService elcMutualListService;
	
    /**
     * 获取研究生或本科生名单列表
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取研究生或本科生名单列表")
    @PostMapping("/elcMutualStuList")
    public RestResult<PageInfo<ElcMutualListVo>> elcMutualStuList(
    		@RequestBody PageCondition<ElcMutualListDto> condition)
        throws Exception
    {
        LOG.info("elcMutualStuList.start");
        PageInfo<ElcMutualListVo> list = elcMutualListService.getMutualStuList(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 获取课程名单列表
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取课程名单列表")
    @PostMapping("/elcMutualCourseList")
    public RestResult<PageInfo<ElcMutualListVo>> elcMutualCourseList(
    		@RequestBody PageCondition<ElcMutualListDto> condition)
        throws Exception
    {
        LOG.info("elcMutualCourseList.start");
        PageInfo<ElcMutualListVo> list = elcMutualListService.getMutualCourseList(condition);
        return RestResult.successData(list);
    }
	
}
