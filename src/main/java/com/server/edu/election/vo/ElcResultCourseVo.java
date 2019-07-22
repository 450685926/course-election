package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ElcResultDto;
import com.server.edu.election.studentelec.context.ElcCourseResult;

/**
 * 选课（统计）
 * @author qiangliz
 *
 */
public class ElcResultCourseVo
{
	/**
	 * 可选课程
	 */
	private List<ElcCourseResult>  optionalCourses;
	
	/**
	 * 已完成课程
	 */
	private List<ElcCourseResult>   selectedCourses;
	
	
	public ElcResultCourseVo(List<ElcCourseResult> optionalCourses, List<ElcCourseResult> selectedCourses) {
		super();
		this.optionalCourses = optionalCourses;
		this.selectedCourses = selectedCourses;
	}

	public List<ElcCourseResult> getOptionalCourses() {
		return optionalCourses;
	}

	public void setOptionalCourses(List<ElcCourseResult> optionalCourses) {
		this.optionalCourses = optionalCourses;
	}

	public List<ElcCourseResult> getSelectedCourses() {
		return selectedCourses;
	}

	public void setSelectedCourses(List<ElcCourseResult> selectedCourses) {
		this.selectedCourses = selectedCourses;
	}
	
}
