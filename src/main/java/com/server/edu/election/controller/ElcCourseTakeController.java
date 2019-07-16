package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.server.edu.election.vo.ElcStudentVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.AddGroup;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElecRoundCourseService;
import com.server.edu.election.vo.ElcCourseTakeNameListVo;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
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
    
	private RestTemplate restTemplate = RestTemplateBuilder.create();
    
    /**
     * 上课名单列表(研究生课程维护模块学生选课列表)
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

    /**
     * 学生个人全部选课信息
     *
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "学生个人全部选课信息")
    @PostMapping("/allSelectedCourse")
    public RestResult<PageResult<ElcCourseTakeVo>> allSelectedCourse(
            @RequestBody PageCondition<String> condition)
            throws Exception
    {
        if (StringUtils.isBlank(condition.getCondition()))
        {
            throw new ParameterValidateException("studentId not be empty");
        }
        PageResult<ElcCourseTakeVo> list =
                courseTakeService.allSelectedCourse(condition);

        return RestResult.successData(list);
    }

    /**
     * 返回研究生可以选的课程
     * @param condition
     * @return
     */
    @ApiOperation(value = "课程维护模块查询研究生可选课程")
    @PostMapping("/addCourseList")
    public RestResult<PageResult<ElcStudentVo>> addCourseList(
            @RequestBody PageCondition<String> condition) {
        PageResult<ElcStudentVo> page = courseTakeService.addCourseList(condition);
        return RestResult.successData(page);
    }

    /**
     * 课程维护模块研究生加课
     * @param teachingClassIds
     * @return
     */
    @ApiOperation(value = "课程维护模块研究生加课")
    @GetMapping("/addCourse")
    public RestResult<String> addCourseList(@RequestParam("teachingClassIds") List<Long> teachingClassIds) {
        String msg = courseTakeService.addCourse(teachingClassIds);
        return RestResult.successData(msg);
    }

    /**
     * 研究生上课名单列表
     * 
     * @param condition
     * @return
     */
    @ApiOperation(value = "研究生上课名单列表")
    @PostMapping("/courseTakePage")
    public RestResult<PageResult<ElcCourseTakeNameListVo>> courseTakePage(
    		@RequestBody PageCondition<ElcCourseTakeQuery> condition)
    				throws Exception
    {
    	ValidatorUtil.validateAndThrow(condition.getCondition());
    	
    	PageResult<ElcCourseTakeNameListVo> list = 
    			courseTakeService.courseTakeNameListPage(condition);
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

    
    @ApiOperation(value = "研究生学生加课")
    @PutMapping("/graduateAdd")
    public RestResult<?> graduateAdd(@RequestBody ElcCourseTakeAddDto value)
    {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        Session session = SessionUtils.getCurrentSession();
    	
        String msg = courseTakeService.graduateAdd(value,session.realType());
        
        return RestResult.success(msg);
    }   
    
    @ApiOperation(value = "个人培养计划中有该课程且又没有选课的学生名单")
    @PutMapping("/studentList")
    public RestResult<PageResult<Student4Elc>> getGraduateStudentForCulturePlan(
    		@RequestBody PageCondition<ElcResultQuery> condition)
    {
    	ValidatorUtil.validateAndThrow(condition.getCondition());
    	/**
    	 * 调用培养：个人培养计划中有该课程且又没有选课的学生名单
    	 * coursesLabelList (课程分类列表)
    	 * cultureCourseLabelRelationList(课程列表)
    	 */
    	String path = ServicePathEnum.CULTURESERVICE.getPath("/culturePlan/getCulturePlanByCourseCodeForElection?courseCode={courseCode}&&selCourse={selCourse}");
    	RestResult<List<String>> restResult = restTemplate.getForObject(path,RestResult.class, condition.getCondition().getCourseCode(), 0);
    	
    	if (CollectionUtil.isEmpty(restResult.getData())) {
    		return RestResult.successData(null);
		}
    	condition.getCondition().setStudentIds(restResult.getData());
    	PageResult<Student4Elc> msg = courseTakeService.getGraduateStudentForCulturePlan(condition);
    	
    	return RestResult.successData(msg);
    }    
    
    
    
    @ApiOperation(value = "研究生学生退课")
    @DeleteMapping("/graduateWithdraw")
    public RestResult<?> graduateWithdraw(
		 @RequestPart(name = "calendarId") @NotNull Long calendarId,
		 @RequestPart(name = "teachingClassId") @NotNull Long teachingClassId,
		 @RequestPart(name = "courseCode") @NotNull String courseCode,
	     @RequestPart(name = "students") @NotNull List<String> students)
    {
    	Session session = SessionUtils.getCurrentSession();
        
    	courseTakeService.graduateWithdraw(calendarId,teachingClassId,courseCode,students,session.realType());
    	
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
        condition.setCondition(query);
        
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
        if (StringUtils.isBlank(query.getStudentId()))
        {
            throw new ParameterValidateException("studentId not be empty");
        }
        //只查询没有成绩的选课
        List<ElcCourseTakeVo> list= courseTakeService.page2StuAbnormal(query);
        return RestResult.successData(list);
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
        //对学生无成绩的选课进行退课处理
        courseTakeService.withdraw2StuAbnormal(query);
        return RestResult.success();
    }
    
    /**
     *@Description: 根据学期模式查找可以加课的学生
     *@Param:
     *@return: 
     *@Author: bear
     *@date: 2019/2/23 14:16
     */
     @ApiOperation(value = "加课学生列表")
     @PostMapping("/studentPage")
     public RestResult<PageResult<Student>> studentPage(
         @RequestBody PageCondition<ElcCourseTakeQuery> condition)
         throws Exception
     {
         ValidatorUtil.validateAndThrow(condition.getCondition());
         
         PageResult<Student> list = courseTakeService.findStudentList(condition);
         
         return RestResult.successData(list);
     }
    
    @PostMapping(value = "/upload")
    public RestResult<?> upload(@RequestPart(name = "file") MultipartFile file,
        @RequestPart(name = "calendarId") @NotNull Long calendarId,
        @RequestPart(name = "mode") @NotNull Integer mode)
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
            
            String msg = courseTakeService.addByExcel(calendarId, datas, mode);
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
    
    @ApiOperation(value = "修改修读类别")
    @PostMapping("/editStudyType")
    public RestResult<Integer> editStudyType(
        @RequestBody ElcCourseTakeDto elcCourseTakeDto)
    {
        int result =courseTakeService.editStudyType(elcCourseTakeDto);
        return RestResult.successData(result);
    }

    @ApiOperation(value = "学生选课列表")
    @GetMapping(value = "/findTeachingClassId")
    public RestResult<?> findTeachingClassIdByStudentId(String studentId){
        try {
            if (StringUtils.isBlank(studentId)) {
                throw new ParameterValidateException("studentId not be empty");
            }
            List<String> list = courseTakeService.findAllByStudentId(studentId);
            return RestResult.successData(list);
        } catch (Exception e) {
            e.printStackTrace();
            return RestResult.fail();
        }
    }

}
