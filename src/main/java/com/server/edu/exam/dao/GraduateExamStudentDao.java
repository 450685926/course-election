package com.server.edu.exam.dao;

import com.github.pagehelper.Page;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.entity.GraduateExamApplyExamination;
import com.server.edu.exam.entity.GraduateExamLog;
import com.server.edu.exam.entity.GraduateExamStudent;
import com.server.edu.exam.query.GraduateExamRoomsQuery;
import com.server.edu.exam.query.StudentQuery;
import com.server.edu.exam.vo.ExamStudent;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import com.server.edu.exam.vo.GraduateExamStudentVo;
import com.server.edu.exam.vo.MyGraduateExam;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface GraduateExamStudentDao extends Mapper<GraduateExamStudent>,MySqlMapper<GraduateExamStudent> {
    List<ExamStudent> listExamStudentById(@Param("examRoomId") Long examRoomId,@Param("examInfoId") Long examInfoId);

    Page<NoExamStudent> listStudent(StudentQuery studentQuery);

    Page<SelectDto> listSelectStudent(SelectDto selectDto);

    Page<NoExamStudent> listMakeUpStudent(StudentQuery studentQuery);

    /**批量审核不通过,回写不通过原因*/
    void updateExamStudentRemark(@Param("list") List<GraduateExamApplyExamination> list,@Param("aduitOpinions") String aduitOpinions);

    /**查找需要修改状态的学生并减少考场人数*/
    List<GraduateExamStudent> findGraduateStudent(@Param("list") List<GraduateExamApplyExamination > list,@Param("calendarId") Long calendarId);

    /**更新状态*/
    void updateSituationByIds(@Param("list") List<GraduateExamStudent> list,@Param("aduitOpinions") String aduitOpinions,@Param("applyType") Integer applyType);

    List<GraduateExamLog> selectStudentByExamRoomIds(List<Long> list);

    List<GraduateExamLog> findRoomIdByExamStudentId(List<Long> examStudentIds);

    /**自动排考批量插入*/
    void insertBatchs(List<GraduateExamStudentVo> list);

    /**查询任课教师所在考场人数*/
    List<GraduateTeachingClassDto> findTeacherStudentNumber(@Param("examInfoId") Long examInfoId,@Param("examRoomId") Long examRoomId);

    /**查询不及格成绩*/
    List<GraduateExamScore> findStudentScore(GraduateExamApplyExamination applyExamination);

    void updateStudentScoreMessage(@Param("list") List<GraduateExamApplyExamination> scores,@Param("aduitOpinions") String aduitOpinions);

    /**查询当前学生排考的所有课程时间*/
    List<ExamStudentInfoAndDate> listStudentInfo(@Param("list") List<GraduateExamStudent> list,@Param("calendarId") Long calendarId);

    /**代理申请缓考学生列表*/
    Page<SelectDto> listApplyListSlow(SelectDto condition);

    /**代理申请补考学生列表*/
    Page<SelectDto> listApplyListMakeUp(SelectDto condition);

    /**代理申请缓考课程列表*/
    Page<SelectDto> listApplyCourseListSlow(SelectDto selectDto);

    /**代理申请补考课程列表*/
    Page<SelectDto> listApplyCourseListMakeUp(SelectDto selectDto);



    /**
     * 班级排考
     * @param teachingClassIds 教学班ID
     * @return
     * @author bear
     * @date 2019/9/4 11:12
     */
    List<GraduateExamStudent> listStudentByClass(@Param("teachingClassIds") List<Long> teachingClassIds, @Param("examRoomIds") List<Long> examRoomIds,
                                          @Param("calendarId") Long calendarId,@Param("examType") Integer examType);

    /**
     * 通过学期，课程代码查询教学班
     * @param classQuery
     * @return
     * @author bear
     * @date 2019/9/9 10:57
     */
    Page<TeachingClassDto> listTeachingClass(GraduateExamRoomsQuery classQuery);

    int findStudentScoreByCondition(ExamStudentAddDto condition);
    /**
     * 查询已排考的课程
     * @param classQuery
     * @return
     * @author bear
     * @date 2019/9/9 10:57
     */
    List<String> findExamStuCourseCode(MyGraduateExam condition);
}