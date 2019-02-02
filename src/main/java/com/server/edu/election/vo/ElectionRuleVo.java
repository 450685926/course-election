package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.entity.ElectionParameter;
import com.server.edu.election.entity.ElectionRule;

public class ElectionRuleVo extends ElectionRule
{
    private static final long serialVersionUID = 1L;
    
    private List<ElectionParameter> parameters;
    
    public List<ElectionParameter> getParameters()
    {
        return parameters;
    }
    
    public void setParameters(List<ElectionParameter> parameters)
    {
        this.parameters = parameters;
    }
}
