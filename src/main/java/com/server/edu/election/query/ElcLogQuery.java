package com.server.edu.election.query;

import java.util.List;

import com.server.edu.election.entity.ElcLog;

public class ElcLogQuery extends ElcLog
{
    private static final long serialVersionUID = 1L;
    
    private List<String> studentIds;
    
    public List<String> getStudentIds()
    {
        return studentIds;
    }
    
    public void setStudentIds(List<String> studentIds)
    {
        this.studentIds = studentIds;
    }
    
}
