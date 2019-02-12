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
import com.server.edu.election.dto.ElcPeFreeStdsDto;
import com.server.edu.election.service.ElcPeFreeStdsService;
import com.server.edu.election.vo.ElcPeFreeStdsVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "体院课不限选名单", version = ""))
@RestSchema(schemaId = "ElcPeFreeStdsController")
@RequestMapping("elcPeFreeStds")
public class ElcPeFreeStdsController {
	
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcPeFreeStdsController.class);
	@Autowired
	private ElcPeFreeStdsService elcPeFreeStdsService;
	
	 /**
     * 体院课不限选名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "体院课不限选名单列表")
    @PostMapping("/list")
    public RestResult<PageInfo<ElcPeFreeStdsVo>> list(
        @RequestBody PageCondition<ElcPeFreeStdsDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ElcPeFreeStdsVo> list =elcPeFreeStdsService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 添加体院课不限选名单初始化数据
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加体院课不限选名单初始化数据")
    @PostMapping("/addStudentInfo")
    public RestResult<PageInfo<ElcPeFreeStdsVo>> addStudentInfo(
    		@RequestBody PageCondition<ElcPeFreeStdsDto> condition)
        throws Exception
    {
        LOG.info("addStudentInfo.start");
        PageInfo<ElcPeFreeStdsVo> list =elcPeFreeStdsService.addStudentInfo(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 添加体院课不限选名单
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加体院课不限选名单")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestParam("studentIds") @NotEmpty List<String> studentIds)
        throws Exception
    {
        LOG.info("add.start");
        int result =elcPeFreeStdsService.add(studentIds);
        return RestResult.successData(result);
    }
    
    /**
     * 删除体院课不限选名单
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除体院课不限选名单")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestParam("ids") @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("delete.start");
        int result =elcPeFreeStdsService.delete(ids);
        return RestResult.successData(result);
    }
    
    

}
