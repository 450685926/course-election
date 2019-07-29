package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.vo.CourseOpenVo;

import com.server.edu.election.vo.FailedCourseVo;
import com.server.edu.election.vo.RebuildCourseVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface CourseOpenDao extends Mapper<CourseOpen> {
	List<CourseOpenVo> selectCourseList(CourseOpen courseOpen);
	List<CourseOpenVo> selectCourseSuggestSwitch(CourseOpenDto dto);


	/**
	 * 根据课程编号查询名称和培养层次
	 * @param courseCode
	 * @return
	 */
	CourseOpen queryNameAndTrainingLevelByCode(String courseCode);

	List<FailedCourseVo> findFailedCourseInfo(@Param("courseCodes") List<String> failedCourseCodes, @Param("calendarId") Long calendarId);

	List<RebuildCourseVo> findRebuildCourses(
			@Param("courseCodes") List<String> failedCourseCodes, @Param("calendarId") Long calendarId, @Param("keyWord") String keyWord);
}