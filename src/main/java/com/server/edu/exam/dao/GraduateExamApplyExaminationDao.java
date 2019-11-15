package com.server.edu.exam.dao;

import com.github.pagehelper.Page;
import com.server.edu.common.entity.ExamMakeUp;
import com.server.edu.exam.dto.ExamStudentAddDto;
import com.server.edu.exam.dto.GraduateExamStudentDto;
import com.server.edu.exam.entity.GraduateExamApplyExamination;
import com.server.edu.exam.vo.GraduateExamApplyExaminationVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface GraduateExamApplyExaminationDao extends Mapper<GraduateExamApplyExamination>,MySqlMapper<GraduateExamApplyExamination> {

    Page<GraduateExamApplyExaminationVo> listGraduateApply(GraduateExamApplyExaminationVo condition);

    void updateByList(@Param("list") List<Long> applyExamination, @Param("applyStatus") int applyStatus,@Param("aduitOpinions") String aduitOpinions);

    Page<GraduateExamApplyExaminationVo> listGraduateMakeUp(GraduateExamApplyExaminationVo condition);

    Page<ExamMakeUp> makeUpCourseList(ExamMakeUp condition);

    ExamStudentAddDto findStudentMakeUp(ExamStudentAddDto condition);
}