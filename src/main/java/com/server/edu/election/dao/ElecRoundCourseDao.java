package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.query.ElecRoundCourseQuery;

/**
 * 可选教学任务
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月1日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface ElecRoundCourseDao
{
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
     * 查询未添加的教学任务(本科生)
     * 
     * @param query
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<CourseOpenDto> listUnAddPage(
        @Param("query") ElecRoundCourseQuery query,
        @Param("list") List<String> list);
    
    /**
     * 查询未添加的教学任务(研究生)
     * 
     * @param query
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<CourseOpenDto> listUnAddPageGraduate(@Param("query") ElecRoundCourseQuery query);
    
    /**
     * 查询已添加的教学任务中所有的教学班
     * 
     * @param query
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<CourseOpenDto> listTeachingClassPage(ElecRoundCourseQuery query);
    
    /**
     * 查询已经添加过的课程
     * 
     * @param roundId
     * @param courseCodes
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<String> listAddedCourse(@Param("roundId") Long roundId,
        @Param("courseCodes") List<String> courseCodes);
    
    /**
     * 添加可选课学生名单
     * 
     * @param roundId
     * @param studentId
     * @see [类、类#方法、类#成员]
     */
    void add(@Param("roundId") Long roundId,
        @Param("courseCode") String courseCode);
    
    /**
     * 删除可选课学生名单
     * 
     * @param roundId
     * @param courseCodes
     * @see [类、类#方法、类#成员]
     */
    void delete(@Param("roundId") Long roundId,
        @Param("courseCodes") List<String> courseCodes);
    
    /**
     * 删除指定轮次的学生名单
     * 
     * @see [类、类#方法、类#成员]
     */
    void deleteByRoundId(@Param("roundId") Long roundId);
    
    /**
     * 查询轮次所有可选课程
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<CourseOpenDto> selectCorseRefTeachClassByRoundId(
        @Param("roundId") Long roundId, @Param("calendarId") Long calendarId);
    /**
     * 查询学期的所有教学班
     * 
     * @param calendarId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<CourseOpenDto> selectTeachingClassByCalendarId(
        @Param("calendarId") Long calendarId);

}
