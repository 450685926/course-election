package com.server.edu.mutual.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
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
import com.server.edu.mutual.dto.ElcMutualCoursesDto;
import com.server.edu.mutual.service.ElcMutualCoursesService;
import com.server.edu.mutual.vo.ElcMutualCoursesVo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
@SwaggerDefinition(info = @Info(title = "本研互选课程管理", version = ""))
@RestSchema(schemaId = "ElcMutualCoursesController")
@RequestMapping("elcMutualCourses")
public class ElcMutualCoursesController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualCoursesController.class);
	@Autowired
	private ElcMutualCoursesService elcMutualCoursesService;
    
    /**
     *获取互选课程管理列表
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取互选课程管理列表")
    @PostMapping("/page")
    public RestResult<PageInfo<ElcMutualCoursesVo>> page(
    		@RequestBody PageCondition<ElcMutualCoursesDto> dto)
        throws Exception
    {
        LOG.info("page.start");
        PageInfo<ElcMutualCoursesVo> list =elcMutualCoursesService.getElcMutualCourseList(dto);
        return RestResult.successData(list);
    }
    
    /**
     *获取互选课程数量
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "获取互选课程数量")
    @PostMapping("/count")
    public RestResult<Integer> count(
    		@RequestParam("calendarId") @NotNull Long calendarId,@RequestParam("mode") @NotNull Integer mode)
        throws Exception
    {
        LOG.info("getElcMutualCourseCount.start");
        int count =elcMutualCoursesService.getElcMutualCourseCount(calendarId, mode);
        return RestResult.successData(count);
    }
	
	
    /**
     * 通过课程编号添加本研互选课程
     * 
     * @param calendarId
     * @param courseList 课程编号ID集合
     * @param mode
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "通过课程编号添加本研互选课程")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestParam("calendarId")  @NotNull Long calendarId,@RequestParam("courseList") @NotBlank String courseList,@RequestParam("mode") @NotNull Integer mode)
        throws Exception
    {
        LOG.info("add.start");
        int result =elcMutualCoursesService.add(calendarId,courseList,mode);
        return RestResult.successData(result);
    }
    
    
    /**
     * 批量添加本研互选课程
     * 
     * @param calendarId
     * @param college
     * @param mode
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "批量添加本研互选课程")
    @PostMapping("/batchAdd")
    public RestResult<Integer> batchAdd(
    		@RequestParam("calendarId")  @NotNull Long calendarId,@RequestParam("college") String college,@RequestParam("mode") @NotNull Integer mode)
        throws Exception
    {
        LOG.info("batchAdd.start");
        int result =elcMutualCoursesService.batchAdd(calendarId,college,mode);
        return RestResult.successData(result);
    }
    
    /**
     * 全部添加本研互选课程
     * 
     * @param calendarId
     * @param mode
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "全部添加本研互选课程")
    @PostMapping("/addAll")
    public RestResult<Integer> addAll(
    		@RequestParam("calendarId")  @NotNull Long calendarId,@RequestParam("mode") @NotNull Integer mode)
        throws Exception
    {
        LOG.info("addAll.start");
        int result =elcMutualCoursesService.addAll(calendarId,mode);
        return RestResult.successData(result);
    }
    
    /**
     * 按选中编号删除本研互选课程
     * 
     * @param ids 课程ID集合
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "按选中编号删除本研互选课程")
    @PostMapping("/delete")
    public RestResult<Integer> delete(
    		@RequestBody @NotEmpty List<Long> ids)
        throws Exception
    {
        LOG.info("delete.start");
        int result =elcMutualCoursesService.delete(ids);
        return RestResult.successData(result);
    }
    
    
    /**
     *删除全部本研互选课程
     * 
     * @param dto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "删除全部本研互选课程")
    @PostMapping("/deleteAll")
    public RestResult<Integer> deleteAll(
    		@RequestParam("calendarId") Long calendarId,@RequestParam("mode") Integer mode)
        throws Exception
    {
        LOG.info("deleteAll.start");
        int result =elcMutualCoursesService.deleteAll(calendarId,mode);
        return RestResult.successData(result);
    }
	
}
