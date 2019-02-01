package com.server.edu.election.service.rule.filter;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

/**
 * 可选课的教学任务过滤器接口<br>
 * 用学生选课界面上，isElectable=false的教学任务在学生选课界面上是看不到的
 *
 */
public interface ElectableLessonFilter extends Comparable<AbstractElectRuleExecutor> {
	
	public boolean isElectable(TeachingClass lesson, ElectState state);
	
}