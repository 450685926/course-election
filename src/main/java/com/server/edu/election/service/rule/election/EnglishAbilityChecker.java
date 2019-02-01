package com.server.edu.election.service.rule.election;

import java.util.Map;
import java.util.Set;

import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.ElectState;
import com.server.edu.election.service.rule.filter.AbstractElectableLessonFilter;

/**
 * 英语课程能力等级检察器<br>
 * 如果课程有英语等级要求，那么学生必须满足要求才能选<br>
 * 学生有一个自己的英语等级，学生只能选一门自己等级的英语课程，不能多选<br>
 * 如果学生的英语等级是“大学英语免修级”（代码：006），那么学生不能选英语课<br>
 * 
 */
public class EnglishAbilityChecker extends AbstractElectableLessonFilter
{
    
    private static final String PARAM_ENG_COURSES =
        "PARAM_ENG_COURSE_ABILITY_MAP";
    
    private static final String PARAM_ENG_STD = "PARAM_ENG_STD_ABILITIES";
    
    @Override
    @SuppressWarnings("unchecked")
    public void prepare(ElectState context)
    {
        //		List<Object[]> courseAndAbilities = (List)entityDao.search(
        //				OqlBuilder.from(Course.class, "c")
        //				.select("c.id, ability.name")
        //				.join("c.abilityRates", "ability")
        //				.where("ability.name like '%大学英语%'")
        //				.orderBy("ability.name").cacheable(true)
        //			);
        //		
        //		Map<Long, Set<String>> courseId2Abilities = new HashMap<>();
        //		for(Object[] courseAndAbility : courseAndAbilities) {
        //			Long courseId = (Long) courseAndAbility[0];
        //			String ability = (String) courseAndAbility[1];
        //			Set<String> abilities = courseId2Abilities.get(courseId);
        //			if(abilities == null) {
        //				abilities = new HashSet<>();
        //				courseId2Abilities.put(courseId, abilities);
        //			}
        //			abilities.add(ability);
        //		}
        //		context.getState().getParams().put(PARAM_ENG_COURSES, courseId2Abilities);
        //		
        //		List<String> stdAbilities = (List)entityDao.search(OqlBuilder
        //			.from(StdCourseAbility.class, "a")
        //			.select("a.abilityRate.name")
        //			.where("a.published = true")
        //			.where("a.std.id = :stdId", context.getStudent().getId())
        //			.where("a.abilityRate.name like '%大学英语%'")
        //			.orderBy("a.abilityRate.name")
        //			.cacheable(true)
        //		);
        //		context.getState().getParams().put(PARAM_ENG_STD, stdAbilities.toArray(new String[]{}));
        
    }
    
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        Map<Long, Set<String>> courseId2Abilities =
            (Map<Long, Set<String>>)state.getParams().get(PARAM_ENG_COURSES);
        String[] stdAbilities = (String[])state.getParams().get(PARAM_ENG_STD);
        
        //		FIXME 重复无用代码
        //		if(courseId2Abilities.keySet().isEmpty()) {
        //			return true;
        //		}
        //		if(CollectionUtils.isEmpty(courseId2Abilities.get(lesson.getCourse().getId()))) {
        //			return true;
        //		} 
        
        //		Set<String> courseAbilities = courseId2Abilities.get(lesson.getCourse().getId());
        //		if(CollectionUtils.isEmpty(courseAbilities)) {
        //			return true;
        //		}
        //		for(String stdAbility : stdAbilities) {
        //			if(stdAbility.equals("大学英语免修级")) {
        //				return false;
        //			}
        //			for(String courseAbility : courseAbilities) {
        //				if(stdAbility.equals(courseAbility)) {
        //					return true;
        //				}
        //			}
        //		}
        return false;
    }
    
    /**
     * 执行选课操作时
     */
    @Override
    public boolean execute(ElectState context, ElcCourseTakeDto courseTake)
    {
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
        
        return true;
    }
    
}