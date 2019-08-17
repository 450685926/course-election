package com.server.edu.election.service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.ElectionApplyDto;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.vo.ElectionApplyVo;

/**
 * 
 * 选课申请
 * 
 * @version  [版本号, 2019年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ElectionApplyService
{
    /**
     * 选课申请管理列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageInfo<ElectionApplyVo> applyList(
        PageCondition<ElectionApplyDto> condition);
    
    /**
     * 学生选课申请结果列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageInfo<ElectionApplyVo> stuApplyCourseList(
            PageCondition<ElectionApplyDto> condition);
    
    /**
     * 回复
     * 
     * @param electionApply
     * @return
     * @see [类、类#方法、类#成员]
     */
    int reply(ElectionApply electionApply);
    
    /**
     * 删除
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    int delete(Long calendarId);
    
    /**
     * 同意
     * 
     * @param id
     * @return
     * @see [类、类#方法、类#成员]
     */
    int agree(Long id);
    
    /**
     * 通过
     * 
     * @param studentId
     * @param roundId
     * @param courseCode
     * @return
     * @see [类、类#方法、类#成员]
     */
    int apply(String studentId, Long roundId, String courseCode);
    
    /**
     * 取消申请
     * 
     * @param studentId
     * @param roundId
     * @param courseCode
     * @return
     * @see [类、类#方法、类#成员]
     */
    int cancelApply(String studentId, Long roundId, String courseCode);
    
    /**
     * 更新选课申请数据
     * 
     * @param studentId
     * @param roundId
     * @param courseCode
     * @see [类、类#方法、类#成员]
     */
    void update(String studentId, Long roundId, String courseCode);
}
