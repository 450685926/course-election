package com.server.edu.election.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.AddGroup;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dto.AddCourseDto;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.dto.ElcCourseTakeWithDrawDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.vo.ElcCourseTakeNameListVo;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcStudentVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.ExportUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelCell;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "研究生上课名单", version = ""))
@RestSchema(schemaId = "ElcCourseTakeYjsController")
@RequestMapping("elcCourseTake")
public class ElcCourseTakeYjsController
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    @Autowired
    private DictionaryService dictionaryService;

    @Value("${cache.directory}")
    private String cacheDirectory;
    
	private RestTemplate restTemplate = RestTemplateBuilder.create();

    /**
     * 研究生课程维护模块学生选课列表
     *
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "学生选课列表")
    @PostMapping("/graduatePage")
    public RestResult<PageResult<ElcCourseTakeVo>> graduatePage(
            @RequestBody PageCondition<ElcCourseTakeQuery> condition)
    {
        ValidatorUtil.validateAndThrow(condition.getCondition());

        PageResult<ElcCourseTakeVo> list =
                courseTakeService.graduatePage(condition);

        return RestResult.successData(list);
    }

    /**
     * 研究生课程维护模块学生选课列表导出
     *
     * @param ids
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "导出")})
    @PostMapping(value = "/exportGraduatePage")
    public ResponseEntity<Resource> exportGraduatePage(
            @RequestBody List<Long> ids)
            throws Exception
    {
        ValidatorUtil.validateAndThrow(ids);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 2);
        List<ElcCourseTakeVo> list = courseTakeService.getExportGraduatePage(ids);;
        GeneralExcelDesigner design = graduatePage();
        design.setDatas(list);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        Session currentSession = SessionUtils.getCurrentSession();
        String uid = currentSession.getUid();
        return ExportUtil.exportExcel(excelUtil, cacheDirectory, uid + ".xls");
    }

    /**
     * 学生个人全部选课信息
     *
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "课程维护模块学生个人全部选课信息")
    @PostMapping("/allSelectedCourse")
    public RestResult<PageResult<ElcCourseTakeVo>> allSelectedCourse(
            @RequestBody PageCondition<Student> condition)
            throws Exception
    {
        Student student = condition.getCondition();
        if (student == null || student.getStudentCode() == null)
        {
            throw new ParameterValidateException("studentId not be empty");
        }
        PageResult<ElcCourseTakeVo> list =
                courseTakeService.allSelectedCourse(condition);

        return RestResult.successData(list);
    }

    /**
     * 研究生课程维护模块学生个人全部选课信息导出
     *
     * @param student
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiResponses({
            @ApiResponse(code = 200, response = File.class, message = "导出")})
    @PostMapping(value = "/exportAllSelectedCourse")
    public ResponseEntity<Resource> exportAllSelectedCourse(
            @RequestBody Student student)
            throws Exception
    {
        ValidatorUtil.validateAndThrow(student);
        FileUtil.mkdirs(cacheDirectory);
        //删除超过30天的文件
        FileUtil.deleteFile(cacheDirectory, 2);
        List<ElcCourseTakeVo> list = new ArrayList<>();
        PageCondition<Student> condition = new PageCondition<>();
        condition.setPageNum_(1);
        condition.setPageSize_(200);
        condition.setCondition(student);
        RestResult<PageResult<ElcCourseTakeVo>> pageResultRestResult = allSelectedCourse(condition);
        PageResult<ElcCourseTakeVo> data = pageResultRestResult.getData();
        long total_ = data.getTotal_();
        list.addAll(data.getList());
        while (list.size() < total_) {
            condition.setPageNum_(condition.getPageNum_() + 1);
            data = allSelectedCourse(condition).getData();
            list.addAll(data.getList());
        }
        GeneralExcelDesigner design = allSelectedCourseExcel();
        design.setDatas(list);
        ExcelWriterUtil excelUtil = GeneralExcelUtil.generalExcelHandle(design);
        return ExportUtil.exportExcel(excelUtil, cacheDirectory, student.getStudentCode() + ".xls");
    }

    /**
     * 返回研究生可以选的课程
     * @param condition
     * @return
     */
    @ApiOperation(value = "课程维护模块查询研究生可选课程")
    @PostMapping("/addCourseList")
    public RestResult<PageResult<ElcStudentVo>> addCourseList(
            @RequestBody PageCondition<ElcCourseTakeQuery> condition) {
        PageResult<ElcStudentVo> page = courseTakeService.addCourseList(condition);
        return RestResult.successData(page);
    }

    /**
     * 课程维护模块研究生加课
     * @param courseDto
     * @return
     */
    @ApiOperation(value = "课程维护模块研究生加课")
    @PostMapping("/addCourse")
    public RestResult<Integer> addCourse(@RequestBody AddCourseDto courseDto) {
        Integer count = courseTakeService.addCourse(courseDto);
        return RestResult.successData(count);
    }

    /**
     * 课程维护模块研究生加课
     * @param value
     * @return
     */
    @ApiOperation(value = "课程维护模块退课")
    @PostMapping("/removedCourse")
    public RestResult<Integer> removedCourse(@RequestBody @NotEmpty List<ElcCourseTake> value) {
        ValidatorUtil.validateAndThrow(value, AddGroup.class);
        return RestResult.successData(courseTakeService.removedCourse(value));
    }

    /**
     * 返回的退课课程列表修读类型为正常修读,废弃
     * @param condition
     * @return
     */
    @ApiOperation(value = "查询学生退课列表")
    @PostMapping("/removedCourseList")
    public RestResult<PageResult<ElcStudentVo>> removedCourseList(
            @RequestBody PageCondition<ElcCourseTakeQuery> condition) {
        PageResult<ElcStudentVo> page = courseTakeService.removedCourseList(condition);
        return RestResult.successData(page);
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
    @PostMapping("/graduateWithdraw")
    public RestResult<?> graduateWithdraw(@RequestBody ElcCourseTakeWithDrawDto value)
    {
    	Session session = SessionUtils.getCurrentSession();
        
    	courseTakeService.graduateWithdraw(value,session.realType());
    	
    	return RestResult.success();
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

    /**
     * 点名册excel拼装返回
     * @return
     */
    private GeneralExcelDesigner graduatePage() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell("学号", "studentId");
        design.addCell("姓名", "studentName");
        design.addCell("年级", "grade");
        design.addCell("学院", "faculty").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    return dictionaryService
                            .query(DictTypeEnum.X_YX.getType(), value);
                });
        design.addCell("专业", "profession").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query(DictTypeEnum.G_ZY.getType(), value);
                });
        design.addCell("课程序号", "teachingClassCode");
        design.addCell("课程名称", "courseName");
        design.addCell("教学班", "teachingClassName");
        design.addCell("课程性质", "nature").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query(DictTypeEnum.X_KCXZ.getType(), value);
                });
        design.addCell("教学安排", "courseArrange");
        design.addCell("学分", "credits");
        design.addCell("修读类别", "courseTakeType").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query(DictTypeEnum.X_XDLX.getType(), value);
                });
        return design;
    }

    private GeneralExcelDesigner allSelectedCourseExcel() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell("学年学期", "calendarName");
        design.addCell("学号", "studentId");
        design.addCell("姓名", "studentName");
        design.addCell("培养层次", "trainingLevel").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query(DictTypeEnum.X_PYCC.getType(), value);
                });
        design.addCell("课程序号", "teachingClassCode");
        design.addCell("课程名称", "courseName");
        design.addCell("课程性质", "nature").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query(DictTypeEnum.X_KCXZ.getType(), value);
                });
        design.addCell("开课学院", "faculty").setValueHandler(
                (String value, Object rawData, GeneralExcelCell cell) -> {
                    return dictionaryService
                            .query(DictTypeEnum.X_YX.getType(), value);
                });
        design.addCell("学分", "credits");
        design.addCell("修读类别", "courseTakeType").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query(DictTypeEnum.X_XDLX.getType(), value);
                });
        return design;
    }
}
