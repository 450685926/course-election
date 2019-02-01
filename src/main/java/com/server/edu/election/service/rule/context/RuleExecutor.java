package com.server.edu.election.service.rule.context;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dto.ElcCourseTakeDto;

public abstract interface RuleExecutor
{
    public int getOrder();
    
    public void setType(ElectRuleType type);
    
    public ElectRuleType getType();
    /**
     * 
     * 有一些选课规则本身需要一些数据才能够执行<br>
     * 那么为了避免在执行规则时都要从数据库中重新查询一遍所需数据<br>
     * 因此在规则构建的时候，会把这些数据保存在规则本身的成员变量中<br>
     * 凡是实现了本接口的规则，都有机会在构建规则的时候做一些数据初始化工作
     * 
     */
    public void prepare(ElectState state);
    
    /**
     * 执行选课
     * 
     * @param state
     * @return
     */
    public abstract boolean execute(ElectState state, ElcCourseTakeDto courseTake);
}