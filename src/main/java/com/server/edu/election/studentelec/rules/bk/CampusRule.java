package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * filter，过滤掉学生不能选的任务（校区不对），使得学生在界面上看不见
 *
 */
@Component("CampusRule")
public class CampusRule extends AbstractRuleExceutor
{
    @Override
    public int getOrder()
    {
        return RulePriority.SECOND.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContext context)
    {
        ElecRequest request = context.getRequest();
//        if (null == lesson.getCampus())
//        {
//            return true;
//        }
//        else
//        {
//            if (lesson.getCampus().equals(state.getStd().getCampus()))
//            {
//                return true;
//            }
//            new ElectMessage("只开放给"
//                + lesson.getCampus() + "校区的同学选课",
//                ElectRuleType.ELECTION, false, null);
//            return false;
//        }
        return true;
    }
    
    
}