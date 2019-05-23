package com.server.edu.election.controller;

import java.util.List;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.service.ElcCourseSuggestSwitchService;
import com.server.edu.election.vo.CourseOpenVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "选课建议课表", version = ""))
@RestSchema(schemaId = "ElcCourseSuggestController")
@RequestMapping("elcCourseSuggest")
public class ElcCourseSuggestController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcCourseSuggestController.class);
	@Autowired
	private ElcCourseSuggestSwitchService elcCourseSuggestSwitchService;
	 /**
     * 建议课表列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "建议课表列表")
    @PostMapping("/page")
    public RestResult<PageInfo<CourseOpenVo>> page(
        @RequestBody PageCondition<CourseOpenDto> condition)
        throws Exception
    {
        LOG.info("page.start");
        PageInfo<CourseOpenVo> list =elcCourseSuggestSwitchService.page(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 点击开启
     * 
     * @param courses
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "点击开启")
    @PostMapping("/start")
    public RestResult<Integer> start(
    		@RequestParam("courses") @NotEmpty List<String> courses)
        throws Exception
    {
        LOG.info("start.start");
        int result =elcCourseSuggestSwitchService.start(courses);
        return RestResult.successData(result);
    }
	
	
    /**
     * 点击关闭
     * 
     * @param courses
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "点击关闭")
    @PostMapping("/stop")
    public RestResult<Integer> stop(
    		@RequestParam("courses") @NotEmpty List<String> courses)
        throws Exception
    {
        LOG.info("stop.start");
        int result =elcCourseSuggestSwitchService.stop(courses);
        return RestResult.successData(result);
    }
    
	
}
