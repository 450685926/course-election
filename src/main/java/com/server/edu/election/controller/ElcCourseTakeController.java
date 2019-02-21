package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.AddGroup;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElecRoundCourseService;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "上课名单", version = ""))
@RestSchema(schemaId = "ElcCourseTakeController")
@RequestMapping("elcCourseTake")
public class ElcCourseTakeController
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    @Autowired
    private ElecRoundCourseService roundCourseService;
    
    @Autowired
    private DictionaryService dictionaryService;
    
    @Value("${cache.directory}")
    private String cacheDirectory;
    
    /**
     * 上课名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "上课名单列表")
    @PostMapping("/page")
    public RestResult<PageResult<ElcCourseTakeVo>> page(
        @RequestBody PageCondition<ElcCourseTakeQuery> condition)
        throws Exception
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());
        
        PageResult<ElcCourseTakeVo> list =
            courseTakeService.listPage(condition);
        
        return RestResult.successData(list);
    }
    
    @ApiOperation(value = "分页查询已可选课程信息")
    @PostMapping("/coursePage")
    public RestResult<PageResult<CourseOpenDto>> coursePage(
        @RequestBody PageCondition<ElecRoundCourseQuery> query)
    {
        ValidatorUtil.validateAndThrow(query.getCondition());
        PageResult<CourseOpenDto> page =
            roundCourseService.listTeachingClassPage(query);
        
        return RestResult.successData(page);
    }
    
    @ApiOperation(value = "学生加课")
    @PutMapping()
    public RestResult<?> add(@RequestBody ElcCourseTakeAddDto value)
    {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        
        String msg = courseTakeService.add(value);
        
        return RestResult.success(msg);
    }
    
    @ApiOperation(value = "学生退课")
    @DeleteMapping()
    public RestResult<?> withdraw(
        @RequestBody @NotEmpty List<ElcCourseTake> value)
    {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        
        courseTakeService.withdraw(value);
        
        return RestResult.success();
    }
    
    @ApiOperation(value = "查询学生学期是否有选课")
    @GetMapping("/hasElc")
    public RestResult<Boolean> hasElc(
        @RequestBody @Valid ElcCourseTakeQuery query)
    {
        if (StringUtils.isBlank(query.getStudentId()))
        {
            throw new ParameterValidateException("studentId not be empty");
        }
        
        PageCondition<ElcCourseTakeQuery> condition = new PageCondition<>();
        condition.setPageNum_(1);
        condition.setPageSize_(10);
        
        PageResult<ElcCourseTakeVo> page =
            courseTakeService.listPage(condition);
        
        Boolean data = Boolean.FALSE;
        if (CollectionUtil.isNotEmpty(page.getList()))
        {
            data = Boolean.TRUE;
        }
        
        return RestResult.successData(data);
    }
    
    @ApiOperation(value = "学生学籍异动选课列表")
    @PostMapping("/page2StuAbnormal")
    public RestResult<List<ElcCourseTakeVo>> page2StuAbnormal(
        @RequestBody @Valid ElcCourseTakeQuery query)
        throws Exception
    {
        PageCondition<ElcCourseTakeQuery> condition = new PageCondition<>();
        condition.setPageNum_(1);
        condition.setPageSize_(10);
        
        PageResult<ElcCourseTakeVo> page =
            courseTakeService.listPage(condition);
        // TODO 只查询没有成绩的选课
        
        return RestResult.successData(page.getList());
    }
    
    @ApiOperation(value = "学生学籍异动退课")
    @DeleteMapping("/withdraw2StuAbnormal")
    public RestResult<?> withdraw2StuAbnormal(
        @RequestBody @Valid ElcCourseTakeQuery query)
    {
        if (StringUtils.isBlank(query.getStudentId()))
        {
            throw new ParameterValidateException("studentId not be empty");
        }
        // TODO 对学生无成绩的选课进行退课处理
        return RestResult.success();
    }
    
    @PostMapping(value = "/upload")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file,
        @RequestPart(name = "calendarId") @NotNull Long calendarId,
                                @RequestPart(name = "mode") @NotNull Integer mode         )
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
            
            designer.getConfigs().add(new ExcelParseConfig("studentId", 0));
            designer.getConfigs()
                .add(new ExcelParseConfig("teachingClassCode", 1));
            
            List<ElcCourseTakeAddDto> datas = GeneralExcelUtil
                .parseExcel(workbook, designer, ElcCourseTakeAddDto.class);
            
            String msg = courseTakeService.addByExcel(calendarId, datas,mode);
            return RestResult.success(msg);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return RestResult.error("解析文件错误" + e.getMessage());
        }
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "下载模版")})
    @GetMapping(value = "/download")
    public ResponseEntity<Resource> download()
    {
        Resource resource = new ClassPathResource("/excel/shangKeMingDan.xls");
        
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + "shangKeMingDan.xls")
            .body(resource);
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "导出")})
    @PostMapping(value = "/export")
    public ResponseEntity<Resource> export(
        @RequestBody ElcCourseTakeQuery query)
        throws Exception
    {
        ValidatorUtil.validateAndThrow(query);
        
        PageCondition<ElcCourseTakeQuery> page = new PageCondition<>();
        page.setCondition(query);
        page.setPageNum_(1);
        page.setPageSize_(1000);
        
        List<ElcCourseTakeVo> datas = new ArrayList<>();
        
        PageResult<ElcCourseTakeVo> res = courseTakeService.listPage(page);
        while (datas.size() < res.getTotal_())
        {
            datas.addAll(res.getList());
            page.setPageNum_(page.getPageNum_() + 1);
            if (datas.size() < res.getTotal_())
            {
                res = courseTakeService.listPage(page);
            }
        }
        
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.addCell("学号", "studentId");
        design.addCell("姓名", "studentName");
        design.addCell("课程序号", "teachingClassCode");
        design.addCell("课程代码", "courseCode");
        design.addCell("课程名称", "courseName");
        design.addCell("专业", "profession")
            .setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.G_ZY.getType(), value);
                    return dict;
                });
        design.addCell("校区", "campus")
            .setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    String dict = dictionaryService
                        .query(DictTypeEnum.X_XQ.getType(), value);
                    return dict;
                });
        design.addCell("学分", "credits");
        design.addCell("修读类别", "courseTakeType")
            .setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    if ("1".equals(value))
                    {
                        return "正常修读";
                    }
                    else if ("2".equals(value))
                    {
                        return "重修";
                    }
                    else if ("3".equals(value))
                    {
                        return "免修不免考";
                    }
                    else if ("4".equals(value))
                    {
                        return "免修";
                    }
                    return value;
                });
        design.setDatas(datas);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        
        return ExportUtil
            .exportExcel(excelUtil, cacheDirectory, "ShangKeMingDanExport.xls");
    }
    
}
