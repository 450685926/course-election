package com.server.edu.election.service.rule;

import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

public class LoserGoodbyeRule extends AbstractElectRuleExecutor
{
    private static final String PARAM = "IS_LOSER";
    
    @Override
    public boolean execute(ElectState context,ElcCourseTakeDto dto)
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
