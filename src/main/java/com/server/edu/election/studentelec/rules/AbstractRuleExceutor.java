package com.server.edu.election.studentelec.rules;

import org.springframework.beans.factory.annotation.Autowired;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;

public abstract class AbstractRuleExceutor
    implements RuleExecutor, Comparable<RuleExecutor>
{
    @Autowired
    protected RoundDataProvider dataProvider;
    
    @Override
    public int getOrder()
    {
        return RulePriority.THIRD.ordinal();
    }
    
    @Override
    public int compareTo(RuleExecutor rule)
    {
        return this.getOrder() - rule.getOrder();
    }
    
    private String projectId;
    
    private ElectRuleType type;
    
    public String getProjectId()
    {
        return projectId;
    }

    public void setProjectId(String projectId)
    {
        this.projectId = projectId;
    }

    @Override
    public void setType(ElectRuleType type)
    {
        this.type = type;
    }
    
    @Override
    public ElectRuleType getType()
    {
        return this.type;
    }
    
}
