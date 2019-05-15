package com.server.edu.election.query;

import javax.validation.constraints.NotNull;

import com.server.edu.election.dto.Student4Elc;

public class ElecRoundStuQuery extends Student4Elc
{
    @NotNull
    private Long roundId;
    @NotNull
    private Integer mode;
    
    private String keyword;

    public Integer getMode() {
        return mode;
    }

    public void setMode(Integer mode) {
        this.mode = mode;
    }

    public Long getRoundId()
    {
        return roundId;
    }

    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }

    public String getKeyword()
    {
        return keyword;
    }

    public void setKeyword(String keyword)
    {
        this.keyword = keyword;
    }
    
}
