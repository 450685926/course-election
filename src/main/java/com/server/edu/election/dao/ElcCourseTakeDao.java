package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ClassCodeToTeacher;
import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.RebuildCoursePaymentCondition;
import com.server.edu.election.dto.ReportManagementCondition;
import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.RebuildCourseNoChargeList;
import com.server.edu.election.vo.RollBookList;
import com.server.edu.election.vo.StudentVo;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcCourseTakeDao
    extends Mapper<ElcCourseTake>, MySqlMapper<ElcCourseTake>
{
    /**分页查询选课名单*/
    Page<ElcCourseTakeVo> listPage(@Param("query") ElcCourseTakeQuery take);
    
    /**
     * 根据教学班ID，教学班code查询课程id与教学班id
     * 
     * @param teachingClassId
     * @param teachingClassCode 
     * @return
     * @see [类、类#方法、类#成员]
     */
    ElcCourseTakeVo getTeachingClassInfo(@Param("calendarId") Long calendarId,
        @Param("teachingClassId") Long teachingClassId,
        @Param("code") String teachingClassCode);
    
    /**判断申请免修免考课程是否已经选课*/
    int findIsEletionCourse(@Param("studentCode") String studentCode,
        @Param("calendarId") Long calendarId,
        @Param("courseCode") String courseCode);
    
    /**查询重修未缴费课程名单*/
    Page<RebuildCourseNoChargeList> findCourseNoChargeList(
        RebuildCoursePaymentCondition condition);
    
    /**查询学生重修未缴费总门数*/
    Page<StudentVo> findCourseNoChargeStudentList(
        RebuildCoursePaymentCondition condition);
    
    /**查询点名册教学班和老师*/
    Page<RollBookList> findRollBookList(ReportManagementCondition condition);
    
    /**查询教学班对应老师姓名*/
    List<ClassTeacherDto> findTeacherByClassCode(Long teachingClassId);
    
    /**查询点名册中学生信息*/
    
    List<StudentVo> findStudentByTeachingClassId(Long id);
    
    /** 查询教学班时间地点*/
    
    List<ClassTeacherDto> findClassTimeAndRoom(Long id);
    
    /**查询学生课表*/
    List<StudentSchoolTimetab> findSchoolTimetab(
        @Param("calendarId") Long calendarId,
        @Param("studentCode") String studentCode);
    
    /**查询所有学生课表*/
    Page<StudentVo> findAllSchoolTimetab(ReportManagementCondition condition);
    
    /**教师上课时间地点详情*/
    List<ClassTeacherDto> findStudentAndTeacherTime(Long teachingClassId);
    
    /**查询教师名称*/
    String findClassTeacherByTeacherCode(String teacherCode);
    
    /**查询教师对应教学班*/
    Page<ClassCodeToTeacher> findAllClassTeacher(ClassCodeToTeacher condition);
    
    /**查询某一学期所有教学班*/
    List<ClassTeacherDto> findAllTeachingClassId(Long calendarId);
    
    /**通过学生删除课程*/
    
    void deleteStudentById(List<String> list);
    
    /**查找当前学期下的可选课学生*/
    Page<Student> findStudentList(ElcCourseTakeQuery condition);
    
    /**从回收站回复到选课表*/
    
    void addCourseTakeFromRecycle(List<RebuildCourseNoChargeList> list);
    
    /**已选择课程信息*/
    List<ElcCourseTakeVo> findSelectedCourses(
        @Param("studentId") String studentId,
        @Param("calendarId") Long calendarId);
    
}