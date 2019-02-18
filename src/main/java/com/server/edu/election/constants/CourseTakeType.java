package com.server.edu.election.constants;

/**
 * 修读类别
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public enum CourseTakeType
{
    /**正常修读*/
    NORMAL(1),
    /**重修*/
    RETAKE(2),
    /**免修不免考*/
    MXBMK(3),
    /**免修*/
    MIAN_XIU(4);
    
    private int type;
    
    CourseTakeType(int type)
    {
        this.type = type;
    }
    
    public int type()
    {
        return this.type;
    }
}
