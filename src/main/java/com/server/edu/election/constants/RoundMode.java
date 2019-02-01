package com.server.edu.election.constants;

public enum RoundMode
{
    /**普通选课*/
    NORMAL(1),
    /**实践选课*/
    ShiJian(2),
    /**结业生*/
    JieYe(3),
    /**留学结业生*/
    LiuXueJieYe(4);
    
    private int mode;
    
    RoundMode(int mode)
    {
        this.mode = mode;
    }
    
    public int mode()
    {
        return this.mode;
    }
}
