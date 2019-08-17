package com.server.edu.election.rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.entity.CourseLabelRelation;
import com.server.edu.common.entity.Courses;
import com.server.edu.common.entity.CultureScheme;
import com.server.edu.common.entity.StudentCultureRel;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.common.vo.CoursesVo;
import com.server.edu.election.vo.ElecFirstLanguageContrastVo;
import com.server.edu.util.CollectionUtil;

/**
 * 培养计划微服务调用
 *
 * @author OuYangGuoDong
 * @version [版本号, 2018年12月5日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CultureSerivceInvoker
{
    private static Logger LOG =
        LoggerFactory.getLogger(CultureSerivceInvoker.class);
    
    public static String getCourseName(String courseCode)
    {
        String courseName = ServicePathEnum.CULTURESERVICE
            .getForObject("/courses/code/{code}", String.class, courseCode);
        return courseName;
    }
    
    public static List<String> getMajorByCourseCode(String courseCode,
        Integer year)
    {
        @SuppressWarnings("unchecked")
        List<String> list = ServicePathEnum.CULTURESERVICE.getForObject(
            "/cultureScheme/getMajors?courseCode={a}&year={b}",
            List.class,
            courseCode,
            year);
        return list;
    }
    
    public static List<Long> getCourseId(Long trainingId, Integer type,
        Integer semester)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("trainingId", trainingId);
        map.put("type", type);
        map.put("semester", semester);
        
        @SuppressWarnings("unchecked")
        List<Long> list = ServicePathEnum.CULTURESERVICE.postForObject(
            "/courseLabelRelation/findCourseIdByCondition",
            map,
            List.class);
        return list;
    }
    
    /* public static List<CultureScheme> getScheme(Long calendarId, String faculty, String trainingLevel) {
        Map<String, Object> map = new HashMap<>();
        map.put("calendarId", calendarId);
        map.put("faculty", faculty);
        map.put("trainingLevel", trainingLevel);
    
        List<CultureScheme> list = ServicePathEnum.CULTURESERVICE
                .postForObject("/cultureScheme/findCultureSchemeBySynchromCondition", map, List.class);
        return list;
    }*/
    
    public static List<Courses> getCourses(List<Long> codes)
    {
        @SuppressWarnings("unchecked")
        RestResult<List<Courses>> restResult = ServicePathEnum.CULTURESERVICE
            .postForObject("/courses/findCoursesByCodes",
                codes,
                RestResult.class);
        if (restResult != null
            && ResultStatus.SUCCESS.code() == restResult.getCode())
        {
            return restResult.getData();
        }
        return null;
    }
    
    /**
     * 根据课程code得到课程对象
     *
     * @param code
     * @return
     */
    public static Courses getCourseByCode(String code)
    {
        List<String> lstCode = new ArrayList<>();
        lstCode.add(code);
        
        CoursesVo coursesVo = new CoursesVo();
        coursesVo.setCodes(lstCode);
        
        List<Courses> lstCourse = getCoursesByParams(coursesVo);
        if (CollectionUtil.isNotEmpty(lstCourse))
        {
            return lstCourse.get(0);
        }
        return null;
    }
    
    public static List<Courses> getCoursesByParams(CoursesVo coursesVo)
    {
        
        List<Courses> pcList = new ArrayList<>();
        @SuppressWarnings("unchecked")
        RestResult<List<Courses>> pcListResult = ServicePathEnum.CULTURESERVICE
            .postForObject("/courses/list", coursesVo, RestResult.class);
        if (null != pcListResult
            && ResultStatus.SUCCESS.code() == pcListResult.getCode())
        {
            pcList = pcListResult.getData();
        }
        
        return pcList;
    }
    
    public static CultureScheme getStudentMajor(String stuid)
    {
        @SuppressWarnings("unchecked")
        RestResult<CultureScheme> studentResult =
            ServicePathEnum.CULTURESERVICE.getForObject(
                "/studentCultureRel/queryStudentCultureScheme?stuid={stuid}",
                RestResult.class,
                stuid);
        return studentResult.getData();
    }
    
    public static String getCourseLabelNameById(Long labelId)
    {
        String labelName = null;
        try
        {
            @SuppressWarnings("rawtypes")
            RestResult result = ServicePathEnum.CULTURESERVICE
                .getForObject("/coursesLabel/{id}", RestResult.class, labelId);
            @SuppressWarnings("unchecked")
            HashMap<String, Object> mapResult =
                (HashMap<String, Object>)result.getData();
            if (mapResult != null)
            {
                labelName = (String)mapResult.get("label");
            }
        }
        catch (Exception e)
        {
            LOG.error("rpc call fail. [getCourseLabelNameById]");
            e.printStackTrace();
        }
        return labelName;
    }
    
    /**
     * 根据培养方案ids批量获取课程代码
     * */
    public static PageResult<CourseLabelRelation> findCourseIdsByTrainingIds(
        PageCondition<List<Long>> condition)
    {
        @SuppressWarnings("unchecked")
        PageResult<CourseLabelRelation> restResult =
            ServicePathEnum.CULTURESERVICE.postForObject(
                "/courseLabelRelation/findCourseLabRelationByTrainingIds",
                condition,
                PageResult.class);
        /* if(restResult!=null&&ResultStatus.SUCCESS.code() == restResult.getCode()){
            return restResult.getData();
        }*/
        return restResult;
    }
    
    /**
     * 根据学号查询培养计划中所有的课程号
     * 
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static List<String> getCourseCodes(String studentId)
    {
        @SuppressWarnings("unchecked")
        RestResult<List<String>> restResult = ServicePathEnum.CULTURESERVICE
            .getForObject("/studentCultureRel/courseCodes/{studentId}",
                RestResult.class,
                studentId);
        
        return restResult.getData();
    }
    
    /**查询实践课*/
    public static List<String> findPracticalCourse()
    {
        @SuppressWarnings("unchecked")
        RestResult<List<String>> list = ServicePathEnum.CULTURESERVICE
            .postForObject("/courses/findPracticalCourse",
                null,
                RestResult.class);
        return list.getData();
    }

    /**查询培养计划课程学期实践课*/
    public static List<PlanCourseDto> findCourseType(String studentId)
    {
        @SuppressWarnings("unchecked")
        RestResult<List<PlanCourseDto>> list = ServicePathEnum.CULTURESERVICE
                .getForObject("/studentCultureRel/findCourseType/{studentId}",
                        RestResult.class,
                        studentId);
        return list.getData();
    }
    
    /**查询本科生选课培养计划课程（包含实践课）*/
    public static List<PlanCourseDto> findUnGraduateCourse(String studentId)
    {
        @SuppressWarnings("unchecked")
        RestResult<List<PlanCourseDto>> list = ServicePathEnum.CULTURESERVICE
                .getForObject("/studentCultureRel/findUnGraduateCourse/{studentId}",
                        RestResult.class,
                        studentId);
        return list.getData();
    }
    
    /**查询研究生关联的第一外国语*/
    public static List<ElecFirstLanguageContrastVo> getStudentFirstForeignLanguage(String managerDeptId,Integer pageNum_,Integer pageSize_)
    {
    	@SuppressWarnings("unchecked")
    	RestResult<PageResult<ElecFirstLanguageContrastVo>> restResult =
        ServicePathEnum.CULTURESERVICE.getForObject(
                "/firstLanguageContrast/page?managerDeptId={managerDeptId}&pageSize_={pageSize_}&pageNum_={pageNum_}",
                 RestResult.class,managerDeptId,pageNum_,pageSize_);
    	Map<String, Object> json = (Map<String, Object>)JSONObject.toJSON(restResult.getData());
    	String object = json.get("list").toString();
    	List<ElecFirstLanguageContrastVo> parseArray = JSON.parseArray(object,ElecFirstLanguageContrastVo.class);
    	return parseArray;
    }
    
    /** 根据学生学号查询研究生关联的第一外国语 */
    public static StudentCultureRel findStudentCultureRelList(StudentCultureRel studentCultureRel)
    {
    	@SuppressWarnings("unchecked")
    	RestResult<StudentCultureRel> restResult =
        ServicePathEnum.CULTURESERVICE.getForObject("/studentCultureRel/findStudentCultureRelByStudentId?stuid={stuid}",
        		RestResult.class,studentCultureRel.getStudentId());
//    	Map<String, Object> json = (Map<String, Object>)JSONObject.toJSON(restResult.getData());
//    	String object = json.get("list").toString();
//    	List<StudentCultureRel> parseArray = JSON.parseArray(object,StudentCultureRel.class);
//    	List<StudentCultureRel> parseArray = restResult.getData().getList();
    	
    	return restResult.getData();
    }

}

