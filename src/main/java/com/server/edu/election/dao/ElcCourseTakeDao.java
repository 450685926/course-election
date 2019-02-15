package com.server.edu.election.dao;




import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.RebuildCoursePaymentCondition;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.entity.RollBookList;
import com.server.edu.election.vo.StudentSchoolTimetabVo;
import com.server.edu.election.vo.StudentVo;
import org.apache.ibatis.annotations.Param;
import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.RebuildCourseNoChargeList;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.vo.ElcCourseTakeVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ElcCourseTakeDao extends Mapper<ElcCourseTake>
{
    /**分页查询选课名单*/
    Page<ElcCourseTakeVo> listPage(@Param("query") ElcCourseTakeQuery take);
    
    /**
     * 查询学生的选退课信息
     * 
     * @param take
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<ElcCourseTakeVo> listHistoryPage(@Param("query") ElcCourseTakeQuery take);
    
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

    /**查询教学班对应老师姓名*/
    List<ClassTeacherDto> findTeacherByClassCode(Long teachingClassId);

    /**查询点名册中学生信息*/

    List<StudentVo> findStudentByTeachingClassId(Long id);

    /** 查询教学班时间地点*/

    List<ClassTeacherDto> findClassTimeAndRoom(Long id);

    /**查询学生课表*/
    List<StudentSchoolTimetab> findSchoolTimetab(@Param("calendarId") Long calendarId, @Param("studentCode") String studentCode);

    /**查询所有学生课表*/
    Page<StudentVo> findAllSchoolTimetab(ReportManagementCondition condition);
}