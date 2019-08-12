package com.server.edu.election.dao;

import java.util.List;
import java.util.Set;

import com.server.edu.election.dto.*;
import com.server.edu.election.vo.*;
import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.studentelec.context.ElecCourse;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcCourseTakeDao
    extends Mapper<ElcCourseTake>, MySqlMapper<ElcCourseTake>
{
    /**分页查询选课名单*/
    Page<ElcCourseTakeVo> listPage(@Param("query") ElcCourseTakeQuery take);

    /**研究生课程维护模块分页查询选课记录*/
    Page<ElcCourseTakeVo> graduatePage(@Param("query") ElcCourseTakeQuery take);

    /**研究生课程维护模块分页查询选课记录导出查询*/
    List<ElcCourseTakeVo> getExportGraduatePage(@Param("ids") List<Long> ids);

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
            RebuildCourseDto condition);
    
    /**查询学生重修未缴费总门数*/
    Page<StudentVo> findCourseNoChargeStudentList(
            RebuildCourseDto condition);
    
    /**查询点名册教学班和老师*/
    Page<RollBookList> findRollBookList(ReportManagementCondition condition);
    
    /**查询教学班对应老师姓名*/
    List<ClassTeacherDto> findTeacherByClassCode(Long teachingClassId);
    
    /**查询点名册中学生信息*/
    List<StudentVo> findStudentByTeachingClassId(Long id);

    /** 查询教学班时间地点*/
    List<ClassTeacherDto> findClassTimeAndRoom(List<Long> list);

    /**研究生查询教学班安排*/
    List<TimeTableMessage> findClassTime(List<Long> list);

    /** 查询教学安排*/
    List<ClassTeacherDto> findClassTimeAndRoomStr(Long teachingClassId);

    /** 以教学班为单位查询教学安排*/
    List<TimeTableMessage> findClassTimeAndRoomById(Long teachingClassId);
    
    /**查询学生课表*/
    List<StudentSchoolTimetab> findSchoolTimetab(
        @Param("calendarId") Long calendarId,
        @Param("studentCode") String studentCode);
    
    /**查询所有学生课表*/
    Page<StudentVo> findAllSchoolTimetab(ReportManagementCondition condition);

    Page<StudentVo> findSchoolTimetabByRole(ReportManagementCondition condition);

    /**教师上课时间地点详情*/
    List<ClassTeacherDto> findStudentAndTeacherTime(Long teachingClassId);
    
    /**查询教师名称*/
    String findClassTeacherByTeacherCode(String teacherCode);

    /**查询教师名称*/
    List<String> findTeacherNameByTeacherCode(@Param("teacherCodes") String[] teacherCodes);

    /**查询教师对应教学班*/
    Page<ClassCodeToTeacher> findAllClassTeacher(ClassCodeToTeacher condition);
    
    /**查询某一学期所有教学班*/
    List<ClassTeacherDto> findAllTeachingClassId(Long calendarId);

    /**查询某一学期教师对应的教学班*/
    List<ClassTeacherDto> findTeachingClassId(@Param("calendarId") Long calendarId, @Param("teacherCode") String teacherCode);

    /**研究生查询教师的教学安排*/
    List<ClassTeacherDto> findTeachingClassIds(@Param("calendarId") Long calendarId, @Param("teacherCode") String teacherCode);

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
    
    /**未申请期中退课课程信息*/
    List<ElcCourseTakeVo> findUnApplyCourses(
        @Param("studentId") String studentId,
        @Param("calendarId") Long calendarId);



    Page<RollBookList> findAllTeachingClass(RollBookConditionDto condition);


    List<RollBookList> findTeacherName(List<Long> list);

    List<String> findTeacherNameById(Long teachingClassId);

    /**点名册*/
    Page<RollBookList> findClassByTeacherCode(RollBookConditionDto condition);

    /**查询学生课表*/
    List<StudnetTimeTable> findStudentTable(@Param("calendarId") Long calendarId,@Param("studentId") String studentId);

    Page<ClassCodeToTeacher> findAllTeacherTimeTable(ClassCodeToTeacher condition);

    /**研究生教师课表查询*/
    Page<ClassCodeToTeacher> findTeacherTimeTableByRole(ClassCodeToTeacher condition);

    List<TeacherTimeTable> findTeacherTimetable(@Param("calendarId") Long calendarId,@Param("teacherCode") String teacherCode);
    
    List<ElecCourse> selectApplyCourses(@Param("studentId") String studentId,
            @Param("calendarId") Long calendarId,@Param("apply") Integer apply);

    ElcCourseLimitDto findSexNumber(Long teachClassId);

    /**预警学生选课*/
    List<LoserStuElcCourse> findStudentElcCourse(@Param("calendarId")Long calendarId, @Param("studentId") String studentId);

    /**
     * 查询未选课学生（本科生）
     */
    Page<NoSelectCourseStdsDto> findNoSelectCourseStds(NoSelectCourseStdsDto stdsDto);

    /**
     * 查询未选课学生(研究生)
     */
    Page<NoSelectCourseStdsDto> findNoSelectCourseGraduteStds(NoSelectCourseStdsDto condition);
    
    /**查询学生未缴费明细*/
    List<ElcCourseTakeVo> findStuRebuildCourse(StudentRePaymentDto studentRePaymentDto);

    
    int editStudyType(@Param("courseTakeType") Integer courseTakeType,@Param("ids") List<Long> ids, @Param("caladerId") Long caladerId);

    /**查询学生选课列表
     * @return*/
    List<String> findAllByStudentId(@Param("studentId")String studentId);
    
    /**查询学生选课列表
     * @return*/
    List<ElcCourseTakeVo> findAllByStudentId4Course(@Param("studentId")String studentId);

	/**
	 * 获取教务员/管理员代选课的学生名单
	 * @param noSelectCourseStds
	 * @return
	 */
	Page<NoSelectCourseStdsDto> findAgentElcStudentList(NoSelectCourseStdsDto noSelectCourseStds);

    Page<ElcStudentVo> findElcStudentInfo(ElcStudentDto elcStudentDto);

    Page<ElcStudentVo> findAddCourseList(
            @Param("courseCodes") List<String> list, @Param("calendarId") Long calendarId, @Param("keyWord") String keyWord);

    Page<ElcStudentVo> findRemovedCourseList(@Param("calendarId") Long calendarId,@Param("studentId") String studentId);

    List<ElcStudentVo> findCourseInfo(@Param("list") List<Long> list);

    Integer saveCourseTask(List<ElcCourseTake> list);

    int deleteCourseTask(@Param("list") List<Long> list,@Param("studentId") String studentId);

    int deleteByCourseTask(@Param("list") List<ElcCourseTake> value);

    List<String> findSelectedCourseCode(@Param("studentId") String studentId, @Param("calendarId") Long calendarId);

    List<String> findCourseCode(@Param("list") List<String> teachingClassIds);

    List<ElcStudentCourseDto> findElcStudentCourse(ElcCourseTakeQuery condition);

    Page<ElcCourseTakeVo> allSelectedCourse(@Param("studentId") String studentId);

    int courseCount(@Param("courseCode") String courseCode, @Param("studentId") String studentId);

    List<Long> findTeachingClassIdByStudentId(@Param("studentId") String studentId, @Param("calendarId") Long calendarId);

    /**
     * 研究生查询学生课程安排，比较课程安排是否冲突使用,不区分老师
     * @param list
     * @return
     */
    List<TimeTableMessage> findCourseArrange(@Param("list") List<Long> list);

    /**研究生查询教师课程安排*/
    List<TimeTableMessage> findTeachingArrange(@Param("teachingClassIds") Set<Long> ids, @Param("teacherCode") String teacherCode);

    List<TimeTableMessage> findCourseArrangeByTeachingClassId(Long teachingClassId);

    /**根据教学班id查询学生是否选课*/
    int findCount(@Param("studentId")String studentId,  @Param("calendarId") Long calendarId,  @Param("teachingClassId") Long teachingClassId);

    /**根据学生id查询学生已重修的门数*/
    Set<String> findRetakeCount(String studentId);
}