package com.server.edu.election.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.vo.ElcResultCountVo;
import com.server.edu.election.vo.TeachingClassVo;

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
     * 调整教学班容量
     * 
     * @param teachingClass
     * @see [类、类#方法、类#成员]
     */
    void adjustClassNumber(TeachingClass teachingClass);
    
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
	ElcResultCountVo elcResultCountByStudent(PageCondition<ElcResultQuery> condition);

	/**
	 * 未选课学生名单
	 * @param page
	 * @return
	 */
	PageResult<Student4Elc> getStudentPage(PageCondition<ElcResultQuery> page);

}
