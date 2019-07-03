package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ElcNumberSetDto;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.dto.TeacherClassTimeRoom;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.vo.TeachingClassVo;

import tk.mybatis.mapper.common.Mapper;

/**
 * 教学班
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2018年11月28日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface TeachingClassDao extends Mapper<TeachingClass>
{
    /**
     * 分页查询教学班
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    Page<TeachingClassVo> listPage(ElcResultQuery condition);
    /**
     * 对选课人数进行自增，不会判断限制人数
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    int increElcNumber(@Param("teachingClassId") Long teachingClassId);
    
    /**
     * 对选课人数进行自减，选课人数需要大于0
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    int decrElcNumber(@Param("teachingClassId") Long teachingClassId);
    /**
     * 
     * 对选课人数进行自增，只有在限制人数大于选课人数时才增加
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    int increElcNumberAtomic(@Param("teachingClassId") Long teachingClassId);
    
    /**
     * 查询教学班并且人数超过总限制
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    TeachingClass selectOversize(@Param("teachingClassId") Long teachingClassId);
    /**
     * 查询教学班的配课年级专业
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<SuggestProfessionDto> selectSuggestProfession(@Param("teachingClassId") Long teachingClassId);
    /**
     * 查询配课学生
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<String> selectSuggestStudent(@Param("teachingClassId") Long teachingClassId);
    
    /**
     * 建议课表查询
     * 
     * @param stu
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<ElecCourse> selectSuggestCourse(@Param("stu") StudentInfoCache stu);


    /**通过teachingClassId查询时间*/
    List<TeacherClassTimeRoom> findTeachingClassIdTime(Long teachingClassId);

    
    /**获取上课时间*/
    List<TeacherClassTimeRoom> getClassTimes(List<Long> list);
    
    List<TeachingClassVo>  findTeachingClass(ElcResultQuery condition);
    
    List<TeachingClassVo> selectDrawClasss(ElcNumberSetDto elcNumberSetDto);
    
    int batchDecrElcNumber(List<TeachingClassVo> list);

}
