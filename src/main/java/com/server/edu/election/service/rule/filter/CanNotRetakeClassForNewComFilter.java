package com.server.edu.election.service.rule.filter;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

/**
 * 新选课程不出现重修班
 *
 */
public class CanNotRetakeClassForNewComFilter
    extends AbstractElectableLessonFilter
{
    private static final String RULE_PARAM_FOR_NEW_ELECT =
        "CAN_NOT_RETAKE_CLASS_FOR_NEW";
    
    @Override
    public int getOrder()
    {
        return AbstractElectRuleExecutor.Priority.FOURTH.ordinal();
    }
    
    /**
     * 在学生选课时，被onExecuteRuleReturn调用<br>
     * 在学生进入选课界面时被调用
     * FIXME 这个数据有多少???
     */
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        //		if(null == state.getParams().get(RULE_PARAM_FOR_NEW_ELECT)){
        //			OqlBuilder<Long> query = OqlBuilder.from(CourseGrade.class.getName(),"courseGrade");
        //			query.select("distinct courseGrade.course.id")
        //			.where("courseGrade.std.id =:StdId",state.getStd().getId());
        ////			.where("courseTake.semester.id =:semesterId",state.getSemesterId());
        //			List<Long> courseIds = entityDao.search(query);
        //			state.getParams().put(RULE_PARAM_FOR_NEW_ELECT,courseIds);
        //			
        //		}
        //		
        //		List<Long> electedCourseIds =(List<Long>)state.getParams().get(RULE_PARAM_FOR_NEW_ELECT);
        //		
        //		//Lesson lesson1 = entityDao.get(Lesson.class, lesson.getId());
        //		boolean elected = electedCourseIds.contains(lesson.getCourse().getId());
        //		if(!elected&&lesson.getTags().size()>0){
//      context.addMessage(new ElectMessage("新选课程不能选重修班", ElectRuleType.ELECTION, false, context.getLesson()));
        //			return false;
        //		}
        
        return true;
    }
    
}