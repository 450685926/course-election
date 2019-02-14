package com.server.edu.election.dto;

import com.server.edu.election.entity.CourseOpen;

public class CourseOpenDto extends CourseOpen
{
    private Long roundId;
    
    private String teachingClassCode;
    
    public Long getRoundId()
    {
        return roundId;
    }

    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }

    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }

    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }
    
    
}
