package com.server.edu.election.constants;

import java.util.Objects;

public enum RoundMode
{
    /**普通选课 1*/
    NORMAL(1),
    /**实践选课 2*/
    ShiJian(2),
    /**结业生 3*/
    JieYe(3),
    /**留学结业生 4*/
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
    
    public boolean eq(int mode) {
        return Objects.equals(this.mode, mode);
    }
}
