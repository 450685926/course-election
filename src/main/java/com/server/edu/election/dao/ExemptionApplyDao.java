package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.vo.ExemptionApplyManageVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ExemptionApplyDao extends Mapper<ExemptionApplyManage> {

    Page<ExemptionApplyManageVo> findExemptionApply(ExemptionApplyCondition condition);

    //批量删除申请人
    void deleteExemptionApply(List<Long> ids);

    //批量审批
    void approvalExemptionApply(@Param("list") List<Long> list,
                                @Param("status") Integer status,@Param("score") String score,@Param("auditor") String auditor);

    /**查询是否重复申请*/

    ExemptionApplyManage applyRepeat(@Param("calendarId") Long calendarId,
                                       @Param("studentCode")String studentCode,
                                       @Param("courseCode")String courseCode);

    List<ElecCourse> findApplyRecord(@Param("calendarId") Long calendarId,
                                     @Param("studentCode")String studentCode);
}
