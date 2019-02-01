package com.server.edu.election.service.rule.filter;

import org.springframework.stereotype.Service;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

@Service
public class ElectableLessonOnlyRetakeFilter
    extends AbstractElectableLessonFilter
{
    
    //    @Autowired
    //	protected CourseGradePrepare courseGradePrepare;
    
    @Override
    public int getOrder()
    {
        return AbstractElectRuleExecutor.Priority.FIRST.ordinal();
    }
    
    // @Override
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        //return state.isRetakeCourse(lesson.getCourse().getId());
        //      if (!result) {
        //          context.addMessage(new ElectMessage("只能选择重修课", ElectRuleType.ELECTION, false, context.getLesson()));
        //      }
        return false;
    }
    
    @Override
    public void prepare(ElectState context)
    {
        //courseGradePrepare.prepare(context);
    }
    
}