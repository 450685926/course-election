package com.server.edu.election.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.entity.ElcMedWithdraw;
import com.server.edu.election.vo.ElcCourseTakeVo;
public interface ElcMedWithdrawService {
    /**
     * 期中退课列表
     * 
     * @param id,projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
	PageInfo<ElcCourseTakeVo> page(PageCondition<ElcMedWithdraw> condition);
    /**
     * 期中退课
     * 
     * @param id,projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
	int medWithdraw(Long id,String projectId);
	
    /**
     * 取消期中退课
     * 
     * @param id,projectId
     * @return
     * @see [类、类#方法、类#成员]
     */
	int cancelMedWithdraw(Long id,String projectId);
}
