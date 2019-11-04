package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.dto.*;
import com.server.edu.exam.query.GraduateExamStudentQuery;
import com.server.edu.exam.query.TeachingClassQuery;
import com.server.edu.exam.vo.GraduateExamRoomVo;
import com.server.edu.util.excel.export.ExcelResult;

import java.util.List;

public interface GraduateExamStudentService {
    /**
    * 查询考场下拉
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/10 9:53
    */
    PageResult<SelectDto> listRoom(PageCondition<SelectDto> condition);

    /**
    * 应考学生添加学生
    * @param
    * @return
    * @author bear
    * @date 2019/9/10 10:43
    */
    PageResult<SelectDto> listStudent(PageCondition<SelectDto> condition);

    /**
    * 应考学生选择的 排考课程下拉表
    * @param
    * @return
    * @author bear
    * @date 2019/9/10 14:25
    */
    PageResult<SelectDto> listCourse(PageCondition<SelectDto> condition);

    /**
    * 应考学生管理列表
    * @param
    * @return
    * @author bear
    * @date 2019/9/16 10:15
    */
    PageResult<GraduateExamStudentDto> listExamStudent(PageCondition<GraduateExamStudentQuery> condition);

    /**
    * 根据条件导出应考学生
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/16 11:04
    */
    ExcelResult export(GraduateExamStudentQuery condition);

    /**
    * 批量删除应考学生
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/16 16:06
    */
    void deleteExamStudent(List<GraduateExamStudentDto> condition);

    /**
    * 批量更换应考学生考场
    * @param
    * @return
    * @author bear
    * @date 2019/9/16 16:38
    */
    void changeExamStudentRoom(List<GraduateExamStudentDto> condition, Long examRoomId);

    /**
    * 添加应考学生
    * @param condition
    * @return
    * @author bear
    * @date 2019/9/17 10:01
    */
    Restrict addExamStudent(ExamStudentAddDto condition);

    /**
    * 通过Id查找考场
    * @param examInfoId 排考主键
    * @return
    * @author bear
    * @date 2019/9/17 11:38
    */
    List<ExamRoomDto> getExamRoomByExamInfoId(Long examInfoId);

    /**
    * examSituation 考试情况 1 正常 2 缓考 3 补考 4 重修 5旷考
    * @param condition 应考学生实体
    * @return 
    * @author bear
    * @date 2019/9/29 14:57
    */
    void setExamStudentSituatiion(List<GraduateExamStudentDto> condition, Integer examSituation);
}
