package com.server.edu.election.studentelec.preload;

import com.server.edu.election.studentelec.context.ElecContext;

/**
 * 选课数据加载
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月25日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface DataProLoad
{
    /**执行优先级，越小越先执行*/
    int order();
    /**加载数据*/
    void load(ElecContext context);
}
