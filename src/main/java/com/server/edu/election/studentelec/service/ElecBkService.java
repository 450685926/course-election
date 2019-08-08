package com.server.edu.election.studentelec.service;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;

/**
 * 本科生选课
 * 
 */
public interface ElecBkService
{
    /**
     * 进行选课
     * 
     * @param request
     * @param context
     * @see [类、类#方法、类#成员]
     */
    IElecContext doELec(ElecRequest request);
    /**
     * 保存选课数据到数据库, 需要判断是否启动了LimitCountCheckerRule校验规则，如果启用了还需要判断选课人数是否超过
     * 
     * @param context
     * @param teachClass
     * @see [类、类#方法、类#成员]
     */
    void saveElc(ElecContextBk context, TeachingClassCache teachClass,
        ElectRuleType type);
    
}
