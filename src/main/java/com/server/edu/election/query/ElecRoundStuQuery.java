package com.server.edu.election.query;

import javax.validation.constraints.NotNull;

import com.server.edu.election.dto.Student4Elc;

public class ElecRoundStuQuery extends Student4Elc
{
    @NotNull
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
