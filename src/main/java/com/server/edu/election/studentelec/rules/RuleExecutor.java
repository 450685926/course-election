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
public interface RuleExecutor
{
    void setType(ElectRuleType type);
    
    ElectRuleType getType();
    
    int getOrder();
    
    /**
     *  检查选课规则
     * @param context 选课上下文
     * @param courseClass 教学班
     * @return true通过，false不通过
     */
    boolean checkRule(ElecContext context, TeachingClassCache courseClass);
}