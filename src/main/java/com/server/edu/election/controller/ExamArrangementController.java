package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.ExamArrangementDto;
import com.server.edu.election.entity.ExamArrangement;
import com.server.edu.election.service.ExamArrangementService;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "考试名单安排", version = ""))
@RestSchema(schemaId = "ExamArrangementController")
@RequestMapping("examArrangement")
public class ExamArrangementController {
	private static Logger LOG =
	        LoggerFactory.getLogger(ExamArrangementController.class);
	@Autowired
	private ExamArrangementService examArrangementService;
	 /**
     * 考试名单安排列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = " 考试名单安排列表")
    @PostMapping("/list")
    public RestResult<PageInfo<ExamArrangement>> list(
        @RequestBody PageCondition<ExamArrangementDto> condition)
        throws Exception
    {
        LOG.info("list.start");
        PageInfo<ExamArrangement> list =examArrangementService.list(condition);
        return RestResult.successData(list);
    }
    
    /**
     * 修改
     * 
     * @param examArrangementDto
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = " 修改")
    @PostMapping("/update")
    public RestResult<Integer> update(
    		@RequestBody @Valid ExamArrangement examArrangement)
        throws Exception
    {
        LOG.info("update.start");
        int result =examArrangementService.update(examArrangement);
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
        int result =examArrangementService.delete(ids);
        return RestResult.successData(result);
    }
    
    @PostMapping(value = "/upload")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file)
    {
        if (file == null)
        {
            return RestResult.error("文件不能为空");
        }
        
        String originalFilename = file.getOriginalFilename();
        if (!originalFilename.endsWith(".xls"))
        {
            return RestResult.error("请使用1999-2003(.xls)类型的Excle");
        }
        
        try (HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream()))
        {
            ExcelParseDesigner designer = new ExcelParseDesigner();
            designer.setDataStartRowIdx(1);
            designer.setConfigs(new ArrayList<>());
            
            designer.getConfigs().add(new ExcelParseConfig("studentCode", 0));
            designer.getConfigs()
                .add(new ExcelParseConfig("name", 1));
            designer.getConfigs().add(new ExcelParseConfig("sex", 2));
            designer.getConfigs().add(new ExcelParseConfig("subject", 3));
            designer.getConfigs().add(new ExcelParseConfig("date", 4));
            designer.getConfigs().add(new ExcelParseConfig("location", 5));
            designer.getConfigs().add(new ExcelParseConfig("notice", 6));
            List<ExamArrangement> datas = GeneralExcelUtil
                .parseExcel(workbook, designer, ExamArrangement.class);
            
            String msg = examArrangementService.addByExcel(datas);
            return RestResult.success(msg);
        }
        catch (Exception e)
        {
        	LOG.error(e.getMessage(), e);
            return RestResult.error("解析文件错误" + e.getMessage());
        }
    }

}
