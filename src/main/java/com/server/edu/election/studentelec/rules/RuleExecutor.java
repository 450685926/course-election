package com.server.edu.election.studentelec.rules;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;

/**
 * 
 * 选课规则执行器
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月26日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface RuleExecutor<T extends TeachingClassCache>
{
    /**
     * 获取ProjectID
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    public String getProjectId();
    
    /**
     * 设置规则类型
     * 
     * @param type
     * @see [类、类#方法、类#成员]
     */
    void setType(ElectRuleType type);
    /**
     * 规则类型
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    ElectRuleType getType();
    /**
     * 获取规则序号
     * 
     * @return
     * @see [类、类#方法、类#成员]
     */
    int getOrder();
    
    /**
     *  检查选课规则
     * @param context 选课上下文
     * @param courseClass 教学班
     * @return true通过，false不通过
     */
    boolean checkRule(ElecContext context, T courseClass);
}