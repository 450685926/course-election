package com.server.edu.election.dao;



import com.server.edu.election.dto.RebuildCoursePaymentCondition;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.entity.RollBookList;
import com.server.edu.election.vo.StudentVo;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.RebuildCourseNoChargeList;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.vo.ElcCourseTakeVo;


import tk.mybatis.mapper.common.Mapper;

public interface ElcCourseTakeDao extends Mapper<ElcCourseTake>
{
    /**分页查询选课名单*/
    Page<ElcCourseTakeVo> listPage(@Param("query") ElcCourseTakeQuery take);
    
    /**
     * 根据教学班ID查询课程信息
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    Long getCourseIdByClassId(@Param("teachingClassId") Long teachingClassId);
    
    /**判断申请免修免考课程是否已经选课*/
    int findIsEletionCourse(@Param("studentCode") String studentCode,
        @Param("calendarId") Long calendarId,
        @Param("courseCode") String courseCode);
    
    /**查询重修未缴费课程名单*/
    Page<RebuildCourseNoChargeList> findCourseNoChargeList(
        RebuildCoursePaymentCondition  condition);

    /**查询学生重修未缴费总门数*/
    Page<StudentVo> findCourseNoChargeStudentList(
            RebuildCoursePaymentCondition  condition);

    /**查询点名册教学班和老师*/
    Page<RollBookList> findRollBookList(ReportManagementCondition condition);
}