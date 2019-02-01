package com.server.edu.election.service.rule.filter;

import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

public abstract class AbstractElectableLessonFilter
    extends AbstractElectRuleExecutor implements ElectableLessonFilter
{
    public TeachingClass getTeachingClass(Long teachingClassId)
    {
        // TODO
        return null;
    }
    
    /**
     * 执行规则验证
     **/
    @Override
    public boolean execute(ElectState state, ElcCourseTakeDto courseTake)
    {
        return isElectable(getTeachingClass(courseTake.getTeachingClassId()),
            state);
    }
    
}
