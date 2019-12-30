package com.server.edu.election.vo;

import java.io.Serializable;

public class TurnNumVo implements Serializable {
    private static final long serialVersionUID = 13L;

    private Long id;

    private int firstTurnNum;

    private int secondTurnNum;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFirstTurnNum() {
        return firstTurnNum;
    }

    public void setFirstTurnNum(int firstTurnNum) {
        this.firstTurnNum = firstTurnNum;
    }

    public int getSecondTurnNum() {
        return secondTurnNum;
    }

    public void setSecondTurnNum(int secondTurnNum) {
        this.secondTurnNum = secondTurnNum;
    }
}
