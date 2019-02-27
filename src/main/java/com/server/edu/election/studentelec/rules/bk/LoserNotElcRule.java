package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 预警学生不能选课
 * 
 */
@Component("LoserNotElcRule")
public class LoserNotElcRule extends AbstractRuleExceutor
{
    private static final String PARAM = "IS_LOSER";
    
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
//        ElectionCourseContext electContext = (ElectionCourseContext)context;
//        
//        Boolean isLoser =
//            (Boolean)electContext.getState().getParams().get("IS_LOSER");
//        if (Boolean.TRUE.equals(isLoser))
//        {
//            return isLoser.booleanValue();
//        }
//        Student std = electContext.getStudent();
//        OqlBuilder<LoserStudent> query =
//            OqlBuilder.from(LoserStudent.class, "loser");
//        query.where("loser.std.id = :stdId", std.getId())
//            .where("loser.semester.id = :semesterId",
//                electContext.getState().getSemesterId());
//        
//        LoserStudent loserStudent =
//            (LoserStudent)this.entityDao.uniqueResult(query);
//        isLoser = Boolean.valueOf(loserStudent != null);
//        electContext.getState().getParams().put("IS_LOSER", isLoser);
//        if (isLoser.booleanValue())
//        {
//            context.addMessage(new ElectMessage("您的不及格课程过多，不能选课",
//                ElectRuleType.GENERAL, false, null));
//        }
//        return !isLoser.booleanValue();
        return false;
    }
}
