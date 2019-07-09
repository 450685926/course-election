package com.server.edu.election.controller;

import java.io.File;
import java.util.List;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dictionary.utils.SpringUtils;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.vo.ElcResultCountVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;

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
     
    @Value("${cache.directory}")
    private String cacheDirectory;
    
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
     * 调整教学班容量
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "调整教学班容量")
    @PostMapping("/adjustClassNumber")
    public RestResult<?> adjustClassNumber(
        @RequestBody TeachingClass teachingClass)
    {
        elcResultService.adjustClassNumber(teachingClass);
        
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
    		@RequestBody ElcResultQuery condition)
    		throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition);
    	Session session = SessionUtils.getCurrentSession();
        
        if (session.realType() == UserTypeEnum.STUDENT.getValue() || session.realType() == UserTypeEnum.TEACHER.getValue())
        {
            return RestResult.fail("elec.mustBeAdmin");
        }
    	ElcResultCountVo result = elcResultService.elcResultCountByStudent(condition);
    	return RestResult.successData(result);
    }
    
    @ApiOperation(value = "未选课学生名单")
    @PostMapping("/elcResultNonSelectedStudent")
    public RestResult<List<Student4Elc>> studentPage(
    		@RequestBody ElcResultQuery condition)
    				throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition);
    	Session session = SessionUtils.getCurrentSession();
        
        if (session.realType() == UserTypeEnum.STUDENT.getValue() || session.realType() == UserTypeEnum.TEACHER.getValue())
        {
            return RestResult.fail("elec.mustBeAdmin");
        }
    	List<Student4Elc> result = elcResultService.getStudentPage(condition);
    	return RestResult.successData(result);
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出")})
    @PostMapping(value = "/export")
    public ResponseEntity<Resource> export(
        @RequestBody ElcResultQuery condition)
        throws Exception
    {
        ValidatorUtil.validateAndThrow(condition);
        
        
        List<Student4Elc> res = elcResultService.getStudentPage(condition);
        res = SpringUtils.convert(res);
        
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.addCell("学号", "studentId");
        design.addCell("姓名", "name");
        design.addCell("培养层次", "trainingLevel");
        design.addCell("培养类别", "degreeCategory");
        design.addCell("学位类型", "degreeType");
        design.addCell("学习形式", "formLearning");
        design.addCell("学院", "faculty");
        design.addCell("专业", "profession");
        design.addCell("入学季节", "enrolSeason");
        design.setDatas(res);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        
        return ExportUtil
            .exportExcel(excelUtil, cacheDirectory, "yanJiuShengWeiXuanKeMingDan.xls");
    }
    
}
