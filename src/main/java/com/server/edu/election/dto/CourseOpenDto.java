package com.server.edu.election.dto;

import com.server.edu.election.entity.CourseOpen;

public class CourseOpenDto extends CourseOpen
{
    private Long roundId;
    
    public Long getRoundId()
    {
        return roundId;
    }

    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }
    
    
}
