package com.server.edu.election.query;

import com.server.edu.common.rest.StudentInfo;

public class ElecRoundStuQuery extends StudentInfo
{
    private static final long serialVersionUID = 1L;
    
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
