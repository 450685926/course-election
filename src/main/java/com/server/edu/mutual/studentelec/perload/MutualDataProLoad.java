package com.server.edu.mutual.studentelec.perload;

/**
 * 本研互选-选课数据加载
 * 
 * 
 * @author  luoxiaoli
 * @version  [版本号, 2019年12月03日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public abstract class MutualDataProLoad<EC> implements Comparable<MutualDataProLoad<?>>
{
    /**执行优先级，越小越先执行*/
    public abstract int getOrder();
    /**对应的管理部门*/
    public abstract String getProjectIds();
    
    @Override
    public int compareTo(MutualDataProLoad<?> rule)
    {
        return this.getOrder() - rule.getOrder();
    }
    
    /**加载数据*/
    public abstract void load(EC context);
}
