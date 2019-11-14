package com.server.edu.exam.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.ExamMakeUp;
import com.server.edu.common.rest.PageResult;
import com.server.edu.exam.dto.SelectDto;
import com.server.edu.exam.entity.GraduateExamApplyExamination;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import com.server.edu.session.util.entity.Session;

import java.util.List;

public interface GraduateExamApplyExaminationService {
    void addGraduateApply(GraduateExamApplyExamination applyExamination);

    PageResult<GraduateExamApplyExaminationVo> listGraduateApply(PageCondition<GraduateExamApplyExaminationVo> applyExamination);

    void examineGraduateApply(List<Long> ids,Integer status,String aduitOpinions);

    void checkRepeat(GraduateExamApplyExamination applyExamination);

    void checkTime(GraduateExamApplyExamination applyExamination);

    void changeStatusToInfo(List<GraduateExamApplyExamination> applyExamination,Integer applyStatus,String aduitOpinions,Session session);

    /**补缓考代理申请学生下拉列表*/
    PageResult<SelectDto> applyStudentList(PageCondition<SelectDto> selectDto);

    /**补缓考代理申请课程下拉列表*/
    PageResult<SelectDto> applyCourseList(PageCondition<SelectDto> selectDto);

    /**补缓考对外提交接口*/
    PageResult<ExamMakeUp> makeUpCourseList(PageCondition<ExamMakeUp> condition);

}
