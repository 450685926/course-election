package com.server.edu.election.dto;

import java.util.List;

import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRule;

public class ElectionRoundsDto extends ElectionRounds
{
    private static final long serialVersionUID = 1L;
    
    private List<ElectionRule> electionRules;

    public List<ElectionRule> getElectionRules()
    {
        return electionRules;
    }

    public void setElectionRules(List<ElectionRule> electionRules)
    {
        this.electionRules = electionRules;
    }
    
}
