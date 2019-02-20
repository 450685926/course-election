package com.server.edu.election.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.AchievementLevelDto;
import com.server.edu.election.service.AchievementLevelService;
import com.server.edu.election.vo.AchievementLevelVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "成绩与分级等级", version = ""))
@RestSchema(schemaId = "AchievementLevelController")
@RequestMapping("achievementLevel")
public class AchievementLevelController {
	private static Logger LOG =
	        LoggerFactory.getLogger(AchievementLevelController.class);
	@Autowired
	private AchievementLevelService achievementLevelService;
	 /**
     * 成绩与分级等级列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = " 成绩与分级等级列表")
    @PostMapping("/list")
    public RestResult<PageInfo<AchievementLevelVo>> list(
        @RequestBody PageCondition<AchievementLevelDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<AchievementLevelVo> list =achievementLevelService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 复制
     * 
     * @param achievementLevelDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "复制")
    @PostMapping("/copy")
    public RestResult<Integer> copy(
    		@RequestBody @Valid AchievementLevelDto achievementLevelDto)
        throws Exception
    {
        LOG.info("copy.start");
        int result =achievementLevelService.copy(achievementLevelDto);
        return RestResult.successData(result);
    }
    
    /**
     * 新增
     * 
     * @param achievementLevelDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "新增")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestBody @Valid AchievementLevelDto achievementLevelDto)
        throws Exception
    {
        LOG.info("add.start");
        int result =achievementLevelService.add(achievementLevelDto);
        return RestResult.successData(result);
    }
    
    /**
     * 获取分级等级
     * 
     * @param achievementLevelDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取分级等级")
    @GetMapping("/getLevel")
    public RestResult<AchievementLevelVo> getLevel(
    		@RequestParam("id") @NotNull Long id)
        throws Exception
    {
        LOG.info("getLevel.start");
        AchievementLevelVo achievementLevelVo =achievementLevelService.getLevel(id);
        return RestResult.successData(achievementLevelVo);
    }
    
    /**
     * 修改
     * 
     * @param achievementLevelDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = " 修改")
    @PostMapping("/update")
    public RestResult<Integer> update(
    		@RequestBody @Valid AchievementLevelDto achievementLevelDto)
        throws Exception
    {
        LOG.info("update.start");
        int result =achievementLevelService.update(achievementLevelDto);
        return RestResult.successData(result);
    }
    
    /**
     * 删除
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestBody @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("delete.start");
        int result =achievementLevelService.delete(ids);
        return RestResult.successData(result);
    }


}
