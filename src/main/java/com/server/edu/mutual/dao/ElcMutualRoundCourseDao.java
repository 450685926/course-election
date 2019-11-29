package com.server.edu.mutual.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElectionRoundsCour;
import com.server.edu.election.query.ElecRoundCourseQuery;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcMutualRoundCourseDao extends Mapper<ElectionRoundsCour>,MySqlMapper<ElectionRoundsCour>{

	/**
	 *  本科生未添加课程列表
	 * @param query
	 * @return
	 */
	Page<CourseOpenDto> listUnAddPage(ElecRoundCourseQuery query);

	/**
	 *    研究生未添加课程列表
	 * @param query
	 * @return
	 */
	Page<CourseOpenDto> listUnAddPageGraduate(ElecRoundCourseQuery query);

    /**
     * 查询已经添加过的课程
     * 
     * @param roundId
     * @param courseCodes
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<Long> listAddedCourse(@Param("roundId") Long roundId,
        @Param("teachingClassIds") List<Long> teachingClassIds);

    /**
     * 批量保存
     * 
     * @param list
     * @return
     * @see [类、类#方法、类#成员]
     */
    int batchInsert(List<ElectionRoundsCour> list);

    /**
     * 分页查询已添加的教学任务(本科生)
     * 
     * @param query
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<CourseOpenDto> listPage(@Param("query") ElecRoundCourseQuery query);

	    /**
     * 分页查询已添加的教学任务(研究生)
     * 
     * @param query
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<CourseOpenDto> listPageGraduate(@Param("query") ElecRoundCourseQuery query);

    /**
     * 删除指定轮次的学生名单
     * 
     * @see [类、类#方法、类#成员]
     */
    void deleteByRoundId(@Param("roundId") Long roundId);
	 
}
