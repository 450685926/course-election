package com.server.edu.election.constants;

/**
 * 选课对象
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public enum ChooseObj
{
    /**学生*/
    STU(1),
    /**教务员代选*/
    DEPART_ADMIN(2),
    /**管理员代选*/
    ADMIN(3);
    
    private int type;
    
    ChooseObj(int type)
    {
        this.type = type;
    }
    
    public int type()
    {
        return this.type;
    }
}
