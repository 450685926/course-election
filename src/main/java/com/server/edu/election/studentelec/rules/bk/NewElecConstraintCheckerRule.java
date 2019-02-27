package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 选课限制检查器<br>
 * 采用全新的大而全的囊括所有门数/学分限制的ElectionConstraintBean来限制学生的选课行为<br>
 * 目前只限制学生的新选学分上限和重修门数上限
 */
@Component("NewElecConstraintCheckerRule")
public class NewElecConstraintCheckerRule extends AbstractRuleExceutor
{
    
    public static final String STATE_PARAM = "NEW_ELECT_CONS";
    
    public void prepare()
    {
        //		Student std = context.getStudent();
        ////		if(context.getState().getParams().get(STATE_PARAM) == null) {
        //			OqlBuilder<ElectionConstraintBean> query = OqlBuilder
        //					.from(ElectionConstraintBean.class, "cons")
        //					.where("cons.std.id = :stdId", std.getId())
        //					.where("cons.semester.id = :semesterId", context.getState().getSemesterId());
        //			ElectionConstraintBean cons = entityDao.uniqueResult(query);
        //			// unproxy hibernate
        //			if(cons!= null){
        //				cons.setSemester(Model.newInstance(Semester.class, cons.getSemester().getId()));
        //				cons.setStd(Model.newInstance(Student.class, cons.getStd().getId()));
        //				context.getState().getParams().put(STATE_PARAM, cons);
        //			}
    }
    
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        //		ElectionCourseContext electContext = (ElectionCourseContext) context;
        //		ElectState state = electContext.getState();
        //		Lesson lesson = electContext.getLesson();
        //
        //		ElectionConstraintBean cons = (ElectionConstraintBean) state.getParams().get(STATE_PARAM);
        //		if(cons == null) {
        //			electContext.addMessage(new ElectMessage("您缺少选课学分限制，无法选课。请联系管理员。", ElectRuleType.ELECTION, false, lesson));
        //			return false;
        //		}
        //		
        //		if(ElectRuleType.EXCHANGE.equals(electContext.getOp())) {
        //			return true;
        //		}
        //		
        //		// 总的学分上限（包含重修和新选的）
        //		if(cons.getMaxCredits() != null) {
        //			if(cons.getGotCredits() + lesson.getCourse().getCredits() > cons.getMaxCredits()) {
        //				electContext.addMessage(new ElectMessage("本学期选课学分已达总学分上限：" + cons.getMaxCredits(), ElectRuleType.ELECTION, false, lesson));
        //				return false;
        //			}
        //		}
        //		
        //		// 总的门数上限（包含重修和新选的）
        //		if(cons.getMaxCourseCount() != null) {
        //			if(cons.getGotCourseCount() + 1 > cons.getMaxCourseCount()) {
        //				electContext.addMessage(new ElectMessage("本学期选课门数已达总门数上限：" + cons.getMaxCourseCount(), ElectRuleType.ELECTION, false, lesson));
        //				return false;
        //			}
        //		}
        //		
        //		if(state.isRetakeCourse(lesson.getCourse().getId())) {
        //			// 重修门数上限
        //			if(cons.getMaxRetakeCourseCount() != null) {
        //				if(cons.getGotRetakeCourseCount() + 1 > cons.getMaxRetakeCourseCount()) {
        //					electContext.addMessage(new ElectMessage("本学期重修课门数已达上限：" + cons.getMaxRetakeCourseCount(), ElectRuleType.ELECTION, false, lesson));
        //					return false;
        //				}
        //			}
        //		} else {
        //			// 新选学分上限
        //			if(cons.getMaxNewCredits() != null) {
        //				if(cons.getGotNewCredits() + lesson.getCourse().getCredits() > cons.getMaxNewCredits()) {
        //					electContext.addMessage(new ElectMessage("本学期新选学分已达上限：" + cons.getMaxNewCredits(), ElectRuleType.ELECTION, false, lesson));
        //					return false;
        //				}
        //			}
        //		}
        return true;
    }
    
}
