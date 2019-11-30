package com.server.edu.exam.dao;

import com.github.pagehelper.Page;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.entity.GraduateExamInfo;
import com.server.edu.exam.query.GraduateExamMessageQuery;
import com.server.edu.exam.query.GraduateExamStudentQuery;
import com.server.edu.exam.vo.ExamStudent;
import com.server.edu.exam.vo.GraduateExamInfoVo;
import com.server.edu.exam.vo.GraduateExamMessage;
import com.server.edu.exam.vo.MyGraduateExam;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface GraduateExamInfoDao extends Mapper<GraduateExamInfo> {
    /**
    * 研究生期末排考课程信息
    * @param examInfoVo
    * @return
    * @author bear
    * @date 2019/9/2 17:43
    */
    Page<GraduateExamInfoVo> listGraduateExamInfoFinal(GraduateExamInfoVo examInfoVo);

    /**
     * 研究生补缓考排考课程信息
     * @param examInfoVo
     * @return
     * @author bear
     * @date 2019/9/2 17:43
     */
    Page<GraduateExamInfoVo> listGraduateExamInfoMakeUp(GraduateExamInfoVo examInfoVo);

    /**
    * 查询个人考试信息
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/5 17:42
    */
    Page<MyGraduateExam> listMyExam(MyGraduateExam condition);

    /**
    * 查询排考信息
    * @param examMessage
    * @return
    * @author bear
    * @date 2019/9/6 16:55
    */
    Page<GraduateExamMessage> listGraduateExamMessage(GraduateExamMessageQuery examMessage);

    /**
    * 查询排考课程
    * @param selectDto
    * @return
    * @author bear
    * @date 2019/9/10 14:28
    */
    Page<SelectDto> ListCourse(SelectDto selectDto);

    /**
    * 查询应考学生
    * @param studentQuery
    * @return
    * @author bear
    * @date 2019/9/16 10:26
    */
    Page<GraduateExamStudentDto> listExamStudent(GraduateExamStudentQuery studentQuery);

    /**
    *
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/17 10:08
    */
    ExamStudentAddDto findStudentElcCourseTake(ExamStudentAddDto condition);

    /**
    * 通过课程Id,查找该学生是否已经排考
    * @param infoId 课程ID
    * @return
    * @author bear
    * @date 2019/9/17 10:44
    */
    int findStudentByInfoId(@Param("calendarId") Long calendarId,
                            @Param("courseCode") String courseCode,@Param("studentCode") String studentCode);

    /**
    * 通过课程查找课程下的考场
    * @param examInfoId 课程ID
    * @return
    * @author bear
    * @date 2019/9/17 11:45
    */
    List<ExamRoomDto> getExamRoomByExamInfoId(List<String> list);

    /**
     * 通过考场主键查询对应课程数
     * @param ids 考场主键
     * @return
     * @author bear
     * @date 2019/9/24 15:10
     */
    List<ExamInfoRoomDto> listExamInfos(List<Long> list);

    /**
    * 更新排考课程考场数
    * @param list 对应examInfoId的考场数
    * @return
    * @author bear
    * @date 2019/9/24 15:18
    */
    void updateExamInfoExamRooms(List<ExamInfoRoomDto> list);

    /**
     * 添加学生同时更新对应课程的考场数
     * */
    void updateActualNumber(@Param("list") List<ExamInfoRoomDto> list ,@Param("symbol") Integer symbol);

    void updateActualNumberById(@Param("examInfoId") Long examInfoId, @Param("symbol") Integer symbol);

    List<ExamStudent> listExamStus( @Param("id") Long id,@Param("examInfoId") Long examInfoId);

    GraduateExamStudentNumber getExamInfoNumber(List<Long> list);

     List<ExportExamInfoDto> getExamRoomIds(@Param("calendarId") Long calendarId, @Param("examType") Integer examType,@Param("projId") String projId,@Param("list") List<String> list);

    List<GraduateExamInfoVo> checkPublicExamTimeSame(GraduateExamInfo graduateExamInfo);

    List<SelectDto> findCourse(@Param("list") List<Long> ids);

    List<Long> findCourseMesaage(Long id);

    List<GraduateExamInfoVo> editGraduateExam(@Param("list") List<Long> list,@Param("mode") Integer mode);

    List<Long> findExamInfoIds(@Param("list") List<GraduateExamInfo> list);

    /**查询已发布的学生考试课程信息（可以不排考）*/
    Page<MyGraduateExam> listMyExamTimeFinal(MyGraduateExam condition);

    /**查询已发布的学生考试课程信息（可以不排考）*/
    Page<MyGraduateExam> listMyExamTimeMakeUp(MyGraduateExam condition);

    /**更换考场查询课程的所有id*/
    List<Long> findExamInfoIdsByCourseCode(ExamStudentAddDto condition);

    List<GraduateExamInfoVo> editGraduateExamMakeUp(@Param("list") List<Long> examInfoIds);
}