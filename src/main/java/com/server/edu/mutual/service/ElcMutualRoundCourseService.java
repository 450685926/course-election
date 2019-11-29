package com.server.edu.mutual.service;

import java.util.List;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.query.ElecRoundCourseQuery;

public interface ElcMutualRoundCourseService {

	/**
	 * 分页查询未添加课程信息
	 * @param query
	 * @return
	 */
	PageResult<CourseOpenDto> listUnAddPage(PageCondition<ElecRoundCourseQuery> condition);
 
	/**
	 * 添加课程
	 * @param roundId
	 * @param teachingClassIds
	 */
	void add(Long roundId, List<Long> teachingClassIds);

	/**
     * 添加所有
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
    void addAll(ElecRoundCourseQuery condition);
 
    /**
     * 已添加任务列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    PageResult<CourseOpenDto> listPage(PageCondition<ElecRoundCourseQuery> condition);

	    /**
     * 删除
     * 
     * @param roundId
     * @param studentCodes
     * @see [类、类#方法、类#成员]
     */
    void delete(Long roundId, List<Long> teachingClassIds);

    /**
     * 删除全部
     * 
     * @param condition
     * @see [类、类#方法、类#成员]
     */
    void deleteAll(Long roundId);

}
