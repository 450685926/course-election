package com.server.edu.election.studentelec.service;

import java.util.List;
import java.util.Map;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.vo.AllCourseVo;

/**
 * 研究生选课
 * 
 */
public interface ElecYjsService
{
    IElecContext doELec(ElecRequest request);
    /**
     * 保存选课数据到数据库, 需要判断是否启动了LimitCountCheckerRule校验规则，如果启用了还需要判断选课人数是否超过
     * 
     * @param context
     * @param courseClass
     * @see [类、类#方法、类#成员]
     */
    void saveElc(ElecContext context, TeachingClassCache courseClass,
        ElectRuleType type);
    
    /**
     * <ul>获取全部课程
     *   <li>全部课程指:在本次选课学期，学生学籍所在校区对应的培养层次所有的排课信息
     * @param AllCourseVo allCourseVo
     * @return
     */
    List<TeachingClassCache> arrangementCourses(AllCourseVo allCourseVo);
    
    /**
     *     获取被代理选课的学生列表
    * @param condition
    * @return
    */
    PageResult<NoSelectCourseStdsDto> findAgentElcStudentList(
        PageCondition<NoSelectCourseStdsDto> condition);
    
    /**
     * 统计研究生选课信息与本轮次选课情况
     * @param uid
     * @param roundId
     * @param map 
     * @return
     */
    Map<String, Object> getElectResultCount(String uid, Long roundId);
    
    /**
     * 向上下文中添加可选课程信息
     * @param c
     * @param roundId 
     * @param calendarId 
     * @return
     */
    ElecContext setData(String studentId, ElecContext c, Long roundId,
        Long calendarId);
}
