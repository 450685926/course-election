package com.server.edu.election.studentelec.service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;

/**
 * 选课请求的主入口
 */
public interface StudentElecService
{
    /**
     * 加载学生数据
     * @return 当前状态
     */
    RestResult<ElecRespose> loading(Long roundId, String studentId);
    
    /**
     * 选课数据提交
     * <li>每个请求需要加锁</li>
     * <li>上一次提交的请求没有处理完之前不能执行新请求</li>
     * @param elecRequest
     * @return
     */
    RestResult<ElecRespose> elect(ElecRequest elecRequest);
    
    /**
     * 获取学生选课结果
     * 
     * @param roundId
     * @param studentId
     * @return
     * @see [类、类#方法、类#成员]
     */
    ElecRespose getElectResult(Long roundId, String studentId);
    
    /**
     * 保存选课数据到数据库, 需要判断是否启动了LimitCountCheckerRule校验规则，如果启用了还需要判断选课人数是否超过
     * 
     * @param context
     * @param courseClass
     * @see [类、类#方法、类#成员]
     */
    void saveElc(ElecContext context, TeachingClassCache courseClass,
        ElectRuleType type);
    
    /**根据轮次查询学生信息*/
    Student findStuRound(Long roundId, String studentId);
}
