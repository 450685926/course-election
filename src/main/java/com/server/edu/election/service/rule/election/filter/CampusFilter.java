package com.server.edu.election.service.rule.election.filter;

import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectMessage;
import com.server.edu.election.service.rule.context.ElectState;
import com.server.edu.election.service.rule.filter.AbstractElectableLessonFilter;

/**
 * filter，过滤掉学生不能选的任务（校区不对），使得学生在界面上看不见
 *
 */
public class CampusFilter extends AbstractElectableLessonFilter
{
    
    @Override
    public int getOrder()
    {
        //在过滤教学任务时的执行顺序
        return AbstractElectRuleExecutor.Priority.SECOND.ordinal();
    }
    
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        if (null == lesson.getCampus())
        {
            return true;
        }
        else
        {
            if (lesson.getCampus().equals(state.getStd().getCampus()))
            {
                return true;
            }
            new ElectMessage("只开放给"
                + lesson.getCampus() + "校区的同学选课",
                ElectRuleType.ELECTION, false, null);
            return false;
        }
    }
    
    
}