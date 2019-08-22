package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.query.ExemptionQuery;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.vo.ExemptionApplyManageVo;
import com.server.edu.election.vo.ExemptionStudentCountVo;

import tk.mybatis.mapper.common.Mapper;

public interface ExemptionApplyDao extends Mapper<ExemptionApplyManage> {

    Page<ExemptionApplyManageVo> findExemptionApply(ExemptionApplyCondition condition);

    //批量删除申请人
    void deleteExemptionApply(List<Long> ids);

    //批量审批
    void approvalExemptionApply(@Param("list") List<Long> list,
                                @Param("status") Integer status,@Param("score") String score,@Param("auditor") String auditor);

    /**查询是否重复申请
     * @param examineResult */

    List<ExemptionApplyManage> applyRepeat(@Param("calendarId") Long calendarId,
                                       @Param("studentCode")String studentCode,
                                       @Param("courseCode")String courseCode);
    /**查询是否重复申请
     * @param examineResult */
    
    ExemptionApplyManage applyRepeatGradute(@Param("calendarId") Long calendarId,
    		@Param("studentCode")String studentCode, @Param("courseCode")String courseCode);
    
    List<ExemptionApplyManage> applyRecord(@Param("calendarId") Long calendarId,
    		@Param("studentCode")String studentCode,
    		@Param("courseCode")String courseCode);

    List<ElecCourse> findApplyRecord(@Param("calendarId") Long calendarId,
                                     @Param("studentCode")String studentCode);

	Page<ExemptionStudentCountVo> exemptionCount(@Param("query")ExemptionQuery query);

	/**
	 * 研究生免修免考申请管理
	 * @param condition
	 * @return
	 */
	Page<ExemptionApplyManageVo> findGraduteExemptionApply(ExemptionQuery condition);

	/**
	 * 研究生免修免考课程
	 * @param calendarId
	 * @param studentId
	 * @return
	 */
	List<ExemptionApplyManage> findGraduteApplyRecord(@Param("calendarId") Long calendarId,
            @Param("studentCode")String studentCode);

	/**
	 * 研究生免修免考课程
	 * @param calendarId
	 * @param studentId
	 * @param applyCourseCodes
	 * @return
	 */
	List<ElecCourse> findApplyCourse(@Param("list") List<String> list);

}
