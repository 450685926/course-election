package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.AddCourseDto;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.dto.ElcCourseTakeWithDrawDto;
import com.server.edu.election.dto.StuHonorDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.vo.CourseConflictVo;
import com.server.edu.election.vo.ElcCourseTakeNameListVo;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcStudentVo;
import com.server.edu.util.excel.export.ExcelResult;

/**
 * 已选课名单
 */
public interface ElcCourseTakeService
{
    /**
     * 分页查询已选课名单
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcCourseTakeVo> listPage(
        PageCondition<ElcCourseTakeQuery> page);
    
    
    /**
     * 分页查询荣誉课程已选课名单
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcCourseTakeVo> honorPage(
        PageCondition<ElcCourseTakeQuery> page);
    
    /**
     * 分页查询荣誉课程已选课名单
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcCourseTakeVo> stuHonorPage(
        PageCondition<StuHonorDto> page);

    /**
     * 研究生课程维护模块学生选课记录列表
     *
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<ElcCourseTakeVo> graduatePage(
            PageCondition<ElcCourseTakeQuery> page);

    /***
     * 研究生课程维护模块学生选课记录列表导出查询专用
     * @param ids
     * @return
     */
    List<ElcCourseTakeVo> getExportGraduatePage(List<Long> ids);


    /**
     * 为指定学生加课
     * 
     * @param teachingClassIds
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    String add(ElcCourseTakeAddDto add);

    String addCourseBk(ElcCourseTakeAddDto add);

    /**
     * 
     * 通过Excel添加
     * @param calendarId
     * @param datas
     * @see [类、类#方法、类#成员]
     */
    String addByExcel(Long calendarId, List<ElcCourseTakeAddDto> datas,Integer mode);
    
    /**
     * 为指定学生退课
     * 
     * @param value
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void withdraw(List<ElcCourseTake> value);
    
    /**
     * 为指定学生退课
     * 
     * @param value
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void newWithdraw(List<ElcCourseTake> value);
    
    /**
     * 为指定教学班学生退课
     * 
     * @param studentId
     * @param teachingClassId
     * @param status
     * @see [类、类#方法、类#成员]
     */
    void withdrawByTeachingClassId(Long teachingClassId, String status);


    /**
    *@Description: 加课学生名单
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/23 14:15
    */
    PageResult<Student> findStudentList(PageCondition<ElcCourseTakeQuery> condition);


    /**学籍异动选课信息*/
    List<ElcCourseTakeVo> page2StuAbnormal(ElcCourseTakeQuery query);

    /**学籍异动退课*/
    void withdraw2StuAbnormal(ElcCourseTakeQuery query);

    
    int editStudyType(ElcCourseTakeDto elcCourseTakeDto);

    /** 研究生选课名单 */
	PageResult<ElcCourseTakeNameListVo> courseTakeNameListPage(PageCondition<ElcCourseTakeQuery> condition);
    

    /***查询学生选课列表
     * @return*/
	List<String> findAllByStudentId(String studentId);

	/**
     * 为指定学研究生退课
	 * @param b 
     * 
     * @param teachingClassIds
     * @param studentId
     */
	void graduateWithdraw(ElcCourseTakeWithDrawDto value, String currentRole, boolean adminFlag, String projId);

	/**
     * 为指定学研究生加退课
	 * @param projId 
	 * @param isTeacher 
	 * @param teachingClassIds
     * @param studentId
     */
//	String graduateAdd(ElcCourseTakeAddDto value,String currentRole, boolean adminFlag, String projId);
	String graduateAdd(ElcCourseTakeAddDto value);

	/**
	 * 个人培养计划中有该课程且又没有选课的学生名单
	 * @param data
	 * @param condition 
	 * @return
	 */
	PageResult<Student4Elc> getGraduateStudentForCulturePlan(PageCondition<ElcResultQuery> condition);

    /**
     * 课程维护研究生加课查询学生个人全部选课信息
     * @param condition
     * @return
     */
    PageResult<ElcCourseTakeVo> allSelectedCourse(PageCondition<Student> condition);

    /**
     * 课程维护研究生加课查询研究生可以添加的课程
     * @param condition
     * @return
     */
    PageResult<ElcStudentVo> addCourseList(PageCondition<ElcCourseTakeQuery> condition);

    /**
     * 课程维护研究生加课
     * @param courseDto
     * @return
     */
    String addCourse(AddCourseDto courseDto);

    void forceAdd(AddCourseDto courseDto);

    /**
     * 课程维护研究生退课
     * @param value
     * @return
     */
    Integer removedCourse(List<ElcCourseTake> value);

    PageResult<ElcStudentVo> removedCourseList(PageCondition<ElcCourseTakeQuery> studentId);

	Integer getRetakeNumber(String studentId,Long calendarId);

    ExcelResult export(ElcCourseTakeQuery query);

    /**
     * 个人培养计划中有该课程且又没有通过的课程
     * @param data
     * @param condition
     * @return
     */
    PageResult<Student4Elc> getGraduateStudentForCulturePlan4Retake(PageCondition<ElcResultQuery> condition);
}
