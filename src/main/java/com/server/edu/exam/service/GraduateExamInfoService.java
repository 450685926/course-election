package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.entity.*;
import com.server.edu.exam.query.GraduateExamAutoStudent;
import com.server.edu.exam.query.GraduateExamRoomsQuery;
import com.server.edu.exam.query.StudentQuery;
import com.server.edu.exam.query.TeachingClassQuery;
import com.server.edu.exam.vo.ExamStudent;
import com.server.edu.exam.vo.GraduateExamInfoVo;
import com.server.edu.exam.vo.GraduateExamRoomVo;
import com.server.edu.util.async.AsyncResult;
import com.server.edu.util.excel.export.ExcelResult;

import java.util.List;

public interface GraduateExamInfoService {

    /**
    * 研究生排考信息
    * @param
    * @return 
    * @author bear
    * @date 2019/9/2 15:12
    */
    PageResult<GraduateExamInfoVo> listGraduateExamInfo(PageCondition<GraduateExamInfoVo> condition);

    /**
    *
    * @param ids 主键
    * @param type 0 撤回 1 发布
    * @return
    * @author bear
    * @date 2019/9/3 9:36
    */
    void releaseOrRebackGraduateExamInfo(List<Long> ids, String type);

    /**
    * 保存排考时间
    * @param examInfo notice 1 学院通知
    * @return
    * @author bear
    * @date 2019/9/3 10:47
    */
    RestResult<ExamSaveTimeRebackDto> insertTime(List<GraduateExamInfo> examInfo);

    /**
    * 保存排考教室
    * @param room
    * @return
    * @author bear
    * @date 2019/9/3 11:33
    */
    void insertRoom(List<GraduateExamRoom> room,String examInfoIds);

    /**
    * 根据condition 中examinfoid 查询考场
    * @param condition id 关联
    * @return
    * @author bear
    * @date 2019/9/3 14:34
    */
    PageResult<GraduateExamRoomVo> listExamRoomByExamInfoId(PageCondition<GraduateExamRoomsQuery> condition);

    /**
    * 选择监考老师
    * @param teachers examRoomId 关联ID
    * @return 
    * @author bear
    * @date 2019/9/4 10:27
    */
    void insertTeacher(List<GraduateExamTeacher> teachers,Long examRoomId,String examInfoIds);

    /**
    * 选择班级排考
    * @param condition examRoomId 关联教室
    * @return
    * @author bear
    * @date 2019/9/4 10:45
    */
    Restrict insertTeachingClass(GraduateExamRoomsQuery condition);

    /**
    *   学生排考
    * @param examStudents examRoomId 关联教室
    * @return
    * @author bear
    * @date 2019/9/4 14:58
    */
    Restrict insertStudent(List<NoExamStudent> examStudents ,Long examRoomId);

    /**
    * 清空考场
    * @param  ids roomId
    * @return
    * @author bear
    * @date 2019/9/4 15:20
    */
    void cleanStudentByRoomId(List<Long> ids);

    /**
    * 删除考场
    * @param ids roomId
    * @return
    * @author bear
    * @date 2019/9/4 15:43
    */
    void deleteRoom(List<Long> ids,String examInfoIds);

    /**
    *  根据考场ID查询考场名单
    * @param  id 关联examRoom
    * @return
    * @author bear
    * @date 2019/9/5 16:08
    */
    List<ExamStudent> listExamStudentById(Long examRoomId,Long examInfoId);

    /**
    * 通过学期，课程查询教学班
    * @param teachingClassQuery
    * @return
    * @author bear
    * @date 2019/9/9 10:51
    */
    PageResult<TeachingClassDto> listTeachingClass(PageCondition<GraduateExamRoomsQuery> teachingClassQuery);

    /**
     * 通过学期，课程查询未排考学生
     * @param studentQuery
     * @return
     * @author bear
     * @date 2019/9/9 10:51
     */
    PageResult<NoExamStudent> listStudent(PageCondition<StudentQuery> studentQuery);

    /**
    * 导出
    * @param
    * @return
    * @author bear
    * @date 2019/9/18 9:07
    */
    ExcelResult export(GraduateExamInfoVo condition);

    void insertExamLog(List<GraduateExamStudent> listStudent,Integer examType);

    public void updateActualNumber(List<GraduateExamStudent> listStudent ,Integer symbol);

    /**
    * 自动分配
    * @param roomsQuery
    * @return
    * @author bear
    * @date 2019/10/8 15:42
    */
    String autoAllocationExamRoom(GraduateExamAutoStudent roomsQuery);

    /**
    * 查询人数
    * @param studentNumber examInfoIds
    * @return
    * @author bear
    * @date 2019/10/9 17:22
    */
    GraduateExamStudentNumber getExamStudentNumber(GraduateExamStudentNumber studentNumber);


    /**
    * 排考学生冲突检测
    * @param
    * @return
    * @author bear
    * @date 2019/10/18 16:16
    */
    Restrict checkExamStudentsConflict(List<GraduateExamStudent> list);

    /**编辑排考设置*/
    EditGraduateExam editGraduateExam(Long id);

    RestResult<ExamSaveTimeRebackDto> saveExamTimeAndDeleteExamRoom(List<GraduateExamInfo> examInfo);
}
