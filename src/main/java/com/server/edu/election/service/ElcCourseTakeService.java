package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.vo.ElcCourseTakeNameListVo;
import com.server.edu.election.vo.ElcCourseTakeVo;

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
     * 为指定学生加课
     * 
     * @param teachingClassIds
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    String add(ElcCourseTakeAddDto add);
    
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
     * @param teachingClassIds
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void withdraw(List<ElcCourseTake> value);


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


}
