package com.server.edu.election.studentelec.rules;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.studentelec.context.ElecContext;


public interface RuleExecutor {
    void setType(ElectRuleType type);
    
    ElectRuleType getType();



    /**
     * 执行选课规则
     */
    boolean execute(ElecContext context);


}