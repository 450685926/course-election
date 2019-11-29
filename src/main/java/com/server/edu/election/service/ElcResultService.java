package com.server.edu.election.service;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.BatchAutoRemoveDto;
import com.server.edu.election.dto.ReserveDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElcScreeningLabel;
import com.server.edu.election.entity.ElcTeachingClassBind;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.entity.TeachingClassChange;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.vo.ElcResultCountVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.util.async.AsyncResult;
import com.server.edu.util.excel.export.ExcelResult;

public interface ElcResultService
{
    /**
     * 教学班信息
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<TeachingClassVo> listPage(PageCondition<ElcResultQuery> page);
    
    /**
     * 研究生教学班信息
     * 
     * @param page
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<TeachingClassVo> graduatePage(PageCondition<ElcResultQuery> page);
    
    /**
     * 调整教学班容量
     * 
     * @param teachingClassVo
     * @see [类、类#方法、类#成员]
     */
    void adjustClassNumber(TeachingClassVo teachingClassVo);
    
    /**
     * 设置教学班预留人数
     * 
     * @param teachingClass
     * @see [类、类#方法、类#成员]
     */
    void setReserveNum(TeachingClass teachingClass);
    
    /**
     * 设置教学班预留人数比例
     * 
     * @param reserveDto
     * @see [类、类#方法、类#成员]
     */
    void setReserveProportion(ReserveDto reserveDto);
    /**
     * 批量设置教学班预留人数
     * 
     * @param reserveDto
     * @see [类、类#方法、类#成员]
     */
    void batchSetReserveNum(ReserveDto reserveDto);
    /**
     * 释放教学班预留人数
     * 
     * @param reserveDto
     * @see [类、类#方法、类#成员]
     */
    void release(ReserveDto reserveDto);
    
    /**
     * 释放所有教学班预留人数
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
    void releaseAll(ElcResultQuery condition);
    
    /**
     * 自动剔除超过人数
     * 
     * @param dto
     * @see [类、类#方法、类#成员]
     */
    void autoRemove(AutoRemoveDto dto);

    /**
     * 从学生维度统计学生选课结果
     * @param condition
     * @return
     */
	ElcResultCountVo elcResultCount(PageCondition<ElcResultQuery> condition);

	/**
	 * 未选课学生名单
	 * @param page
	 * @return
	 */
	PageResult<Student4Elc> getStudentPage(PageCondition<ElcResultQuery> page);

	/**
	 * 选课结果导出
	 * @param condition
	 * @return
	 */
	RestResult<String> elcResultCountsExport(ElcResultQuery condition);

	/**
	 * 未选课名单导出
	 * @param condition
	 * @return
	 */
	RestResult<String> exportOfNonSelectedCourse(ElcResultQuery condition);
	
	void saveElcLimit(TeachingClassVo teachingClassVo);
	
	void saveProportion(TeachingClassVo teachingClassVo);
	void saveScreeningLabel(ElcScreeningLabel elcScreeningLabel);
	void updateScreeningLabel(ElcScreeningLabel elcScreeningLabel);
	void saveClassBind(ElcTeachingClassBind elcTeachingClassBind);
	void updateClassBind(ElcTeachingClassBind elcTeachingClassBind);
	void updateClassRemark(Long id, String remark);
	/**
	 * 班级信息导出
	 * @param condition
	 * @return
	 */
	ExcelResult teachClassPageExport(ElcResultQuery condition);
	
	/**
	 * 教学班转移
	 * @param condition
	 * @return
	 */
	void changeStudentClass(TeachingClassChange condition);
	
	AsyncResult autoBatchRemove(BatchAutoRemoveDto dto);
	


}
