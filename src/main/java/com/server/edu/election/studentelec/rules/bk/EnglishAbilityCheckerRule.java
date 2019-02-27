package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 英语课程能力等级检察器<br>
 * 如果课程有英语等级要求，那么学生必须满足要求才能选<br>
 * 学生有一个自己的英语等级，学生只能选一门自己等级的英语课程，不能多选<br>
 * 如果学生的英语等级是“大学英语免修级”（代码：006），那么学生不能选英语课<br>
 * 
 */
@Component("EnglishAbilityCheckerRule")
public class EnglishAbilityCheckerRule extends AbstractRuleExceutor
{
    
    private static final String PARAM_ENG_COURSES =
        "PARAM_ENG_COURSE_ABILITY_MAP";
    
    private static final String PARAM_ENG_STD = "PARAM_ENG_STD_ABILITIES";
    
    /**
     * 执行选课操作时
     */
    @Override
    public boolean checkRule(ElecContext context)
    {
        //        Map<Long, Set<String>> courseId2Abilities =
        //            (Map<Long, Set<String>>)state.getParams().get(PARAM_ENG_COURSES);
        //        String[] stdAbilities = (String[])state.getParams().get(PARAM_ENG_STD);
        
        //      FIXME 重复无用代码
        //      if(courseId2Abilities.keySet().isEmpty()) {
        //          return true;
        //      }
        //      if(CollectionUtils.isEmpty(courseId2Abilities.get(lesson.getCourse().getId()))) {
        //          return true;
        //      } 
        
        //      Set<String> courseAbilities = courseId2Abilities.get(lesson.getCourse().getId());
        //      if(CollectionUtils.isEmpty(courseAbilities)) {
        //          return true;
        //      }
        //      for(String stdAbility : stdAbilities) {
        //          if(stdAbility.equals("大学英语免修级")) {
        //              return false;
        //          }
        //          for(String courseAbility : courseAbilities) {
        //              if(stdAbility.equals(courseAbility)) {
        //                  return true;
        //              }
        //          }
        //      }
        return false;
        
        //        ElectionCourseContext electContext = (ElectionCourseContext)context;
        //		Lesson lesson = electContext.getLesson();
        //		ElectState state = electContext.getState();
        //		
        //		Map<Long, Set<String>> courseId2Abilities = (Map<Long, Set<String>>) state.getParams().get(PARAM_ENG_COURSES);
        //		String[] stdAbilities = (String[]) state.getParams().get(PARAM_ENG_STD);
        //		
        //		
        //		if(!isElectable(lesson, state)) {
        //			//如果该学生没有英语能力等级，则不能选任何英语课
        //			if(stdAbilities.length == 0){
        //				context.addMessage(new ElectMessage("你没有英语等级信息，不能选英语课程", ElectRuleType.ELECTION, false, lesson));
        //				return false;
        //			}
        //			for(String stdAbility : stdAbilities) {
        //				if(stdAbility.equals("大学英语免修级")) {
        //					context.addMessage(new ElectMessage("您的英语课程是免修的，不能选择英语课程", ElectRuleType.ELECTION, false, lesson));
        //					return false;
        //				}
        //			}
        //			context.addMessage(new ElectMessage("没有达到课程英语能力要求", ElectRuleType.ELECTION, false, lesson));
        //			return false;
        //		}
        //		
        //		Set<String> courseAbilities = courseId2Abilities.get(lesson.getCourse().getId());
        //		if(!CollectionUtils.isEmpty(courseAbilities)) {
        //			if(
        //				ElectRuleType.ELECTION.equals(electContext.getOp())
        //				&& CollectionUtils.isNotEmpty(
        //					CollectionUtils.intersection(state.getElectedCourseIds().keySet(), courseId2Abilities.keySet())
        //				)
        //			) {
        //				context.addMessage(new ElectMessage("不能选一门以上的英语课", ElectRuleType.ELECTION, false, lesson));
        //				return false;
        //			}
        //		}
        
    }
    
}