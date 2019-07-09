package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
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
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.ElcResultDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.query.ElecRoundStuQuery;
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
    		@RequestBody PageCondition<ElcResultQuery> condition)
    		throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition.getCondition());
    	Session session = SessionUtils.getCurrentSession();
//    	if (!session.isAdmin()) {
//    		return RestResult.fail("elec.mustBeAdmin");
//        }
    	ElcResultCountVo result = elcResultService.elcResultCountByStudent(condition);
    	return RestResult.successData(result);
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "学生选课结果统计导出")})
    @PostMapping("/elcResultCountByStudentExport")
    public ResponseEntity<Resource> elcResultCountByStudentExport(
    		@RequestBody PageCondition<ElcResultQuery> condition)
    		throws Exception
    {
    	condition.setPageNum_(1);
    	condition.setPageSize_(1000);
        List<ElcResultDto> datas = new ArrayList<>();
    	ElcResultCountVo result = elcResultService.elcResultCountByStudent(condition);
    	if(condition.getCondition().getDimension().intValue() == Constants.ONE){
    		PageResult<ElcResultDto> res = result.getElceResultByStudent();
    		while (datas.size() < res.getTotal_())
    	       {
//    	       	List<Student4Elc> list = SpringUtils.convert(res.getList());
    	           datas.addAll(res.getList());
    	           condition.setPageNum_(condition.getPageNum_() + 1);
    	           if (datas.size() < res.getTotal_())
    	           {
    	               res = elcResultService.elcResultCountByStudent(condition).getElceResultByStudent();
    	           }
    	       }
    		GeneralExcelDesigner design = new GeneralExcelDesigner();
            design.addCell("年级", "grade");
            design.addCell("培养层次", "trainingLevel");
            design.addCell("培养类别", "trainingCategory");
            design.addCell("学位类型", "degreeType");
            design.addCell("学习形式", "formLearning");
            design.addCell("人数", "studentNum");
            design.addCell("已选人数", "numberOfelectedPersons");
            design.addCell("未选人数", "numberOfNonCandidates");
            design.addCell("已选人数百分比（%）", "numberOfelectedPersonsPoint");
            design.setDatas(res.getList());
            ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
            
            return ExportUtil
                .exportExcel(excelUtil, cacheDirectory, "yanJiuShengXuanKeJieGuoTongJi(xuesheng).xls");
    	}else{
    		PageResult<ElcResultDto> res = result.getElceResultByFaculty();
    		while (datas.size() < res.getTotal_())
 	       {
 	           datas.addAll(res.getList());
 	           condition.setPageNum_(condition.getPageNum_() + 1);
 	           if (datas.size() < res.getTotal_())
 	           {
 	               res = elcResultService.elcResultCountByStudent(condition).getElceResultByFaculty();
 	           }
 	       }
    		GeneralExcelDesigner design = new GeneralExcelDesigner();
            design.addCell("年级", "grade");
            design.addCell("学院", "faculty");
            design.addCell("专业", "profession");
            design.addCell("人数", "studentNum");
            design.addCell("已选人数", "nNumberOfelectedPersons");
            design.addCell("未选人数", "numberOfNonCandidates");
            design.addCell("已选人数百分比（%）", "numberOfelectedPersonsPoint");
            design.setDatas(res.getList());
            ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
            
            return ExportUtil
                .exportExcel(excelUtil, cacheDirectory, "yanJiuShengXuanKeJieGuoTongJi(xueyuan) .xls");
    	}
    	
    }
    
    
    
    @ApiOperation(value = "未选课学生名单")
    @PostMapping("/elcResultNonSelectedStudent")
    public RestResult<PageResult<Student4Elc>> studentPage(
    		@RequestBody ElcResultQuery condition)
    				throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition);
    	Session session = SessionUtils.getCurrentSession();
        
        if (session.realType() == UserTypeEnum.STUDENT.getValue() || session.realType() == UserTypeEnum.TEACHER.getValue())
        {
            return RestResult.fail("elec.mustBeAdmin");
        }
        PageCondition<ElcResultQuery> page = new PageCondition<>();
        page.setCondition(condition);
        PageResult<Student4Elc> result = elcResultService.getStudentPage(page);
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
        PageCondition<ElcResultQuery> page = new PageCondition<>();
        page.setCondition(condition);
        page.setPageNum_(1);
        page.setPageSize_(1000);
        
        List<Student4Elc> datas = new ArrayList<>();
        PageResult<Student4Elc>  res = elcResultService.getStudentPage(page);
        while (datas.size() < res.getTotal_())
	       {
	           datas.addAll(res.getList());
	           page.setPageNum_(page.getPageNum_() + 1);
	           if (datas.size() < res.getTotal_())
	           {
	        	   res = elcResultService.getStudentPage(page);
	           }
	       }
        
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
        design.setDatas(res.getList());
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        
        return ExportUtil
            .exportExcel(excelUtil, cacheDirectory, "yanJiuShengWeiXuanKeMingDan.xls");
    }
    
}
