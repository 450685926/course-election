package com.server.edu.election.controller;

import java.io.File;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.dto.ReserveDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElcScreeningLabel;
import com.server.edu.election.entity.ElcTeachingClassBind;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.vo.ElcResultCountVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.excel.export.ExcelResult;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课结果", version = ""))
@RestSchema(schemaId = "ElcResultController")
@RequestMapping("elcResult")
public class ElcResultController
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElcResultService elcResultService;
    
    /**
     * 上课名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "教学班信息")
    @PostMapping("/teachClassPage")
    public RestResult<PageResult<TeachingClassVo>> page(
        @RequestBody PageCondition<ElcResultQuery> condition)
        throws Exception
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());
        
        PageResult<TeachingClassVo> list = elcResultService.listPage(condition);
        
        return RestResult.successData(list);
    }
    
    /**
     * 研究生教学班查询列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "研究生教学班查询列表")
    @PostMapping("/graduateTeachClassPage")
    public RestResult<PageResult<TeachingClassVo>> graduatePage(
        @RequestBody PageCondition<ElcResultQuery> condition)
        throws Exception
    {
        PageResult<TeachingClassVo> list = elcResultService.graduatePage(condition);
        
        return RestResult.successData(list);
    }
    
    /**
     * 调整教学班容量
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "调整教学班容量")
    @PostMapping("/adjustClassNumber")
    public RestResult<?> adjustClassNumber(
        @RequestBody TeachingClassVo teachingClassVo)
    {
        elcResultService.adjustClassNumber(teachingClassVo);
        
        return RestResult.success();
    }
    
    /**
     * 设置具体预留人数
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "设置具体预留人数")
    @PostMapping("/setReserveNum")
    public RestResult<?> setReserveNum(
        @RequestBody TeachingClass teachingClass)
    {
        elcResultService.setReserveNum(teachingClass);
        
        return RestResult.success();
    }
    
    /**
     * 批量设置预留人数
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "批量设置预留人数")
    @PostMapping("/batchSetReserveNum")
    public RestResult<?> batchSetReserveNum(
        @RequestBody @Valid ReserveDto reserveDto)
    {
        elcResultService.batchSetReserveNum(reserveDto);
        
        return RestResult.success();
    }
    
    /**
     * 设置具体预留人数比例
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "设置具体预留人数比例")
    @PostMapping("/setReserveProportion")
    public RestResult<?> setReserveProportion(
        @RequestBody @Valid ReserveDto reserveDto)
    {
        elcResultService.setReserveProportion(reserveDto);
        
        return RestResult.success();
    }
    
    /**
     * 释放人数
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "释放人数")
    @PostMapping("/release")
    public RestResult<?> release(
        @RequestBody @Valid ReserveDto reserveDto)
    {
        elcResultService.release(reserveDto);
        
        return RestResult.success();
    }
    
    
    /**
     * 释放所有
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "释放所有")
    @PostMapping("/releaseAll")
    public RestResult<?> releaseAll(@RequestBody ElcResultQuery condition)
    {
        elcResultService.releaseAll(condition);
        
        return RestResult.success();
    }
    
    @ApiOperation(value = "自动剔除超过人数")
    @PostMapping("/autoRemove")
    public RestResult<?> autoRemove(@RequestBody AutoRemoveDto dto)
        throws Exception
    {
        elcResultService.autoRemove(dto);
        return RestResult.success();
    }
    
    @ApiOperation(value = "学生选课结果统计")
    @PostMapping("/elcResultCountByStudent")
    public RestResult<ElcResultCountVo> elcResultCountByStudent(
    		@RequestBody PageCondition<ElcResultQuery> condition)
    		throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition.getCondition());
    	Session session = SessionUtils.getCurrentSession();
    	if (!session.isAdmin()) {
    		return RestResult.fail("elec.mustBeAdmin");
        }
    	condition.getCondition().setManagerDeptId(session.getCurrentManageDptId());
    	ElcResultCountVo result = elcResultService.elcResultCount(condition);
    	return RestResult.successData(result);
    }
    

    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "选课学生统计导出")})
    @GetMapping("/elcResultCountByStudentExport")
    public File elcResultCountByStudentExport(
    		@ModelAttribute ElcResultQuery condition)
    {
    	try {
    		Session session = SessionUtils.getCurrentSession();
        	condition.setManagerDeptId(session.getCurrentManageDptId());
            RestResult<String> restResult = elcResultService.elcResultCountsExport(condition);
            if (restResult.getCode() == ResultStatus.SUCCESS.code()
                    && !"".equals(restResult.getData()))
            {
            	return new File(restResult.getData());
            }
            else
            {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    	
    }
    
    @ApiOperation(value = "未选课学生名单")
    @PostMapping("/elcResultNonSelectedStudent")
    public RestResult<PageResult<Student4Elc>> studentPage(
    		@RequestBody PageCondition<ElcResultQuery> condition)
    				throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition.getCondition());
    	Session session = SessionUtils.getCurrentSession();
    	condition.getCondition().setManagerDeptId(session.getCurrentManageDptId());
    	if (!session.isAdmin()) {
    		return RestResult.fail("elec.mustBeAdmin");
        }
        PageResult<Student4Elc> result = elcResultService.getStudentPage(condition);
    	return RestResult.successData(result);
    }
    
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "未选课学生名单导出")})
    @GetMapping("/export")
    public File export(
    		@ModelAttribute ElcResultQuery condition)
    {
    	Session session = SessionUtils.getCurrentSession();
    	condition.setManagerDeptId(session.getCurrentManageDptId());
    	try {
            RestResult<String> restResult = elcResultService.exportOfNonSelectedCourse(condition);
            if (restResult.getCode() == ResultStatus.SUCCESS.code()
                    && !"".equals(restResult.getData()))
            {
                return new File(restResult.getData());
            }
            else
            {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    	
    }
    
    /**
     * 保存教学班选课限制
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "保存教学班选课限制")
    @PostMapping("/saveElcLimit")
    public RestResult<?> saveElcLimit(
        @RequestBody TeachingClassVo teachingClassVo)
    {
        elcResultService.saveElcLimit(teachingClassVo);
        
        return RestResult.success();
    }
    
    /**
     * 保存教学班男女人数
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "保存教学班男女人数")
    @PostMapping("/saveProportion")
    public RestResult<?> saveProportion(
        @RequestBody @Valid TeachingClassVo teachingClassVo)
    {
        elcResultService.saveProportion(teachingClassVo);
        
        return RestResult.success();
    }
    
    /**
     * 保存选课筛选标签
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "保存选课筛选标签")
    @PostMapping("/saveScreeningLabel")
    public RestResult<?> saveScreeningLabel(
        @RequestBody @Valid ElcScreeningLabel elcScreeningLabel)
    {
        elcResultService.saveScreeningLabel(elcScreeningLabel);
        
        return RestResult.success();
    }
    
    /**
     * 修改选课筛选标签
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改选课筛选标签")
    @PostMapping("/updateScreeningLabel")
    public RestResult<?> updateScreeningLabel(
        @RequestBody @Valid ElcScreeningLabel elcScreeningLabel)
    {
        elcResultService.updateScreeningLabel(elcScreeningLabel);
        
        return RestResult.success();
    }
    
    
    /**
     * 保存选课班级属性信息
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "保存选课班级属性信息")
    @PostMapping("/saveClassBind")
    public RestResult<?> saveClassBind(
        @RequestBody @Valid ElcTeachingClassBind elcTeachingClassBind)
    {
        elcResultService.saveClassBind(elcTeachingClassBind);
        
        return RestResult.success();
    }
    
    /**
     * 修改选课班级属性信息
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改选课班级属性信息")
    @PostMapping("/updateClassBind")
    public RestResult<?> updateClassBind(
        @RequestBody @Valid ElcTeachingClassBind elcTeachingClassBind)
    {
        elcResultService.updateClassBind(elcTeachingClassBind);
        
        return RestResult.success();
    }
    
    /**
     * 修改教学班备注信息
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "修改教学班备注信息")
    @PostMapping("/updateClassRemark")
    public RestResult<?> updateClassRemark(
        @RequestParam("id") @NotNull Long id,@RequestParam("remark") @NotBlank String remark)
    {
        elcResultService.updateClassRemark(id,remark);
        
        return RestResult.success();
    }
    
    /**
     * 班级信息导出
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "导出班级信息")
    @PostMapping("/teachClassPageExport")
    public RestResult<?> teachClassPageExport(
    		@RequestBody ElcResultQuery condition)
            throws Exception
        {
    		logger.info("export.start");
            ExcelResult result = elcResultService.teachClassPageExport(condition);
            return RestResult.successData(result);
        }   
    
}
