package com.server.edu.election.service.rule.withdraw;

import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

public class AssignedWithdrawPrepare extends AbstractElectRuleExecutor
{
    public static final String ERROR_WITHDRAW_ASSIGNED =
        "error.elect.withdrawAssigned";
    
    @Override
    public boolean execute(ElectState context,ElcCourseTakeDto dto)
    {
//        ElectionCourseContext electContext = (ElectionCourseContext)context;
        boolean result = false;
//        Set<Long> assignedLessonIds =
//            (Set)electContext.getState().getParams().get("assignedLessonIds");
//        result =
//            !assignedLessonIds.contains(electContext.getLesson().getId());
//        if (!result)
//        {
//            electContext
//                .addMessage(new ElectMessage("error.elect.withdrawAssigned",
//                    ElectRuleType.WITHDRAW, false, electContext.getLesson()));
//        }
        return result;
    }
    
    @Override
    public void prepare(ElectState context)
    {
//        if (!context.isPreparedData(
//            PrepareContext.PreparedDataName.ASSIGNED_LESSON_IDS))
//        {
//            Set<Long> assignedLessonIds = Collections.emptySet();
//            for (CourseTake take : context.getTakes())
//            {
//                if (ElectionMode.ASSIGEND
//                    .equals(take.getElectionMode().getId()))
//                {
//                    assignedLessonIds.add(take.getLesson().getId());
//                    context.getState()
//                        .getUnWithdrawableLessonIds()
//                        .put(take.getLesson().getId(),
//                            "error.elect.withdrawAssigned");
//                }
//            }
//            context.getState()
//                .getParams()
//                .put("assignedLessonIds", assignedLessonIds);
//            context.addPreparedDataName(
//                PrepareContext.PreparedDataName.ASSIGNED_LESSON_IDS);
//        }
    }
    
}
