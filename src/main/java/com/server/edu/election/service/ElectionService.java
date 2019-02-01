package com.server.edu.election.service;

import java.util.List;

import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.rule.context.ElectMessage;
import com.server.edu.election.service.rule.context.ElectState;

public interface ElectionService {

	/**
	 * 获取学生适用的轮次
	 * 
	 * @param stdId学生ID
	 * @return
	 */
	public List<ElectionRounds> getProfiles(Long stdId);

	//public boolean isElectable(Lesson lesson, ElectState state);

	/**
	 * 进行批量选课/退课
	 * @param contexts
	 * @return
	 */
	//public Collection<List<Message>> batchOperator(Map<ElectRuleType, List<ElectionCourseContext>> contexts);

	/**
	 * 针对进入选课前的通用检查
	 * 
	 * @param context
	 * @return
	 */
	public List<ElectMessage> generalCheck(ElectionRounds profile, ElectState context);

	/**
     * 选退课规则数据准备
     * 
     * @param rounds 轮次
     * @param state
     * @param lessonType 1.实践课程 0.教学
     * @see [类、类#方法、类#成员]
     */
	public void prepare(ElectionRounds rounds, ElectState state, List<ElcCourseTakeDto> courseTake, Integer lessonType);

}