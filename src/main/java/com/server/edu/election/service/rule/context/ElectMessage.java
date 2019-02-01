package com.server.edu.election.service.rule.context;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.entity.TeachingClass;

public class ElectMessage
{
    
    private boolean success;
    
    private ElectRuleType type;
    
    private TeachingClass lesson;
    
    public ElectMessage(String key, ElectRuleType type, boolean success,
        TeachingClass lesson)
    {
        this.success = success;
        this.lesson = lesson;
        this.type = type;
    }
    
    public boolean isSuccess()
    {
        return success;
    }
    
    public ElectRuleType getType()
    {
        return type;
    }
    
    public TeachingClass getLesson()
    {
        return lesson;
    }
}