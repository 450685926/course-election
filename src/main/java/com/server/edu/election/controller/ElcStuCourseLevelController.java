package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.CourseLevelDto;
import com.server.edu.election.dto.ElcStuCouLevelDto;
import com.server.edu.election.entity.ElcStuCouLevel;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.StuCourseLevelQuery;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.ElcStuCourseLevelService;
import com.server.edu.util.excel.GeneralExcelUtil;
import com.server.edu.util.excel.parse.ExcelParseConfig;
import com.server.edu.util.excel.parse.ExcelParseDesigner;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

/**
 * 学生课程能力
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年8月22日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@SwaggerDefinition(info = @Info(title = "学生课程能力", version = ""))
@RestSchema(schemaId = "StuCourseLevelController")
@RequestMapping("ElcStuCourseLevel")
public class ElcStuCourseLevelController
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElcStuCourseLevelService courseLevelService;
    
    @Autowired
    private StudentDao studentDao;
    
    /**
     * 上课名单列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "查询学生课程能力列表")
    @PostMapping("/page")
    public RestResult<PageResult<ElcStuCouLevelDto>> page(
        @RequestBody PageCondition<StuCourseLevelQuery> condition)
        throws Exception
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());
        
        PageResult<ElcStuCouLevelDto> list =
            courseLevelService.listPage(condition);
        
        return RestResult.successData(list);
    }
    
    @ApiOperation(value = "新增学生课程能力")
    @PostMapping("/add")
    public RestResult<?> add(@RequestBody @Valid ElcStuCouLevel couLevel)
    {
        courseLevelService.add(couLevel);
        
        return RestResult.success();
    }
    
    @ApiOperation(value = "修改学生课程能力")
    @PostMapping("/update")
    public RestResult<?> update(@RequestBody @Valid ElcStuCouLevel couLevel)
    {
        courseLevelService.update(couLevel);
        
        return RestResult.success();
    }
    
    @ApiOperation(value = "修改学生课程能力")
    @PostMapping("/delete")
    public RestResult<?> delete(@RequestBody @NotEmpty List<Long> ids)
    {
        courseLevelService.delete(ids);
        
        return RestResult.success();
    }
    
    @ApiResponses({
        @ApiResponse(code = 200, response = File.class, message = "下载模版")})
    @GetMapping(value = "/download")
    public ResponseEntity<Resource> download()
    {
        Resource resource = new ClassPathResource("/excel/XueShengKeChengNengLi.xls");
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "application/vnd.ms-excel")
            .header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment;filename=" + "XueShengKeChengNengLi.xls")
            .body(resource);
    }
    
    @ApiOperation(value = "导入学生课程能力")
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
            List<CourseLevelDto> coursesLevel = CultureSerivceInvoker.getCoursesLevel();
            ExcelParseDesigner designer = new ExcelParseDesigner();
            designer.setDataStartRowIdx(1);
            designer.setConfigs(new ArrayList<>());
            
            designer.getConfigs().add(new ExcelParseConfig("studentId", 0) {
                @Override
                public Object handler(String value)
                {
                    value = StringUtils.trim(value);
                    Student stu = studentDao.findStudentByCode(value);
                    if(stu == null) {
                        return null;
                    }
                    return value;
                }
            });
            designer.getConfigs()
                .add(new ExcelParseConfig("courseCategoryId", 2) {
                    @Override
                    public Object handler(String value)
                    {
                        String value1 = StringUtils.trim(value);
                        CourseLevelDto findFirst = coursesLevel.stream()
                            .filter(p -> {return StringUtils.equalsIgnoreCase(p.getLevelName(), value1);})
                            .findFirst()
                            .orElse(null);
                        if(null == findFirst) {
                            return null;
                        }
                        return findFirst.getId();
                    }
                });
            
            List<ElcStuCouLevel> datas = GeneralExcelUtil
                .parseExcel(workbook, designer, ElcStuCouLevel.class);
            
            String msg = courseLevelService.batchAdd(datas);
            return RestResult.success(msg);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
            return RestResult.error("解析文件错误" + e.getMessage());
        }
    }
    
}
