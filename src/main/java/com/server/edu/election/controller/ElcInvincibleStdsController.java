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

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcInvincibleStdsService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "特殊学生管理", version = ""))
@RestSchema(schemaId = "ElcInvincibleStdsController")
@RequestMapping("elcInvincibleStds")
public class ElcInvincibleStdsController {

	private static Logger LOG =
	        LoggerFactory.getLogger(ElcAffinityCoursesController.class);
	@Autowired
	private ElcInvincibleStdsService elcInvincibleStdsService;
	 /**
     * 特殊学生名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "特殊学生名单列表")
    @PostMapping("/list")
    public RestResult<PageInfo<Student>> list(
        @RequestBody PageCondition<Student> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<Student> list =elcInvincibleStdsService.list(condition);
        return RestResult.successData(list);
    }
    
	 /**
     * 学生名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "学生名单列表")
    @PostMapping("/getStudents")
    public RestResult<PageInfo<Student>> getStudents(
        @RequestBody PageCondition<Student> condition)
        throws Exception
    {
        LOG.info("getStudents.start");
        PageInfo<Student> list =elcInvincibleStdsService.getStudents(condition);
        return RestResult.successData(list);
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
    		@RequestBody @NotEmpty List<String> studentCodes)
        throws Exception
    {
        LOG.info("delete.start");
        int result =elcInvincibleStdsService.delete(studentCodes);
        return RestResult.successData(result);
    }
    
    /**
     * 添加学生
     * 
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "添加学生")
    @PostMapping("/add")
    public RestResult<Integer> add(
    		@RequestBody @NotEmpty List<String> studentCodes)
        throws Exception
    {
        LOG.info("add.start");
        int result =elcInvincibleStdsService.add(studentCodes);
        return RestResult.successData(result);
    }
}
