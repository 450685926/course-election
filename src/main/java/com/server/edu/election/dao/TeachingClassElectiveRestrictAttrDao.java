package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.entity.Student;
import org.apache.ibatis.annotations.Param;

import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.vo.RestrictStudent;

import tk.mybatis.mapper.common.Mapper;

public interface TeachingClassElectiveRestrictAttrDao
    extends Mapper<TeachingClassElectiveRestrictAttr>
{
    /**
     * 查询教学班的限制年级专业
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<SuggestProfessionDto> selectRestrictProfession(
        @Param("teachingClassId") Long teachingClassId);
    
    /**
     * 查询限制学生
     * 
     * @param teachingClassId
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<String> selectRestrictStudent(
        @Param("teachingClassId") Long teachingClassId);
    
    /**
     * 查询多个教学班的限制年级专业
     * 
     * @param teachingClassIds
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<SuggestProfessionDto> selectAllRestrictProfession(
        @Param("teachingClassIds") List<Long> teachingClassIds);
    
    /**
     * 查询多个教学班的限制学生
     * 
     * @param teachingClassIds
     * @return
     * @see [类、类#方法、类#成员]
     */
    List<RestrictStudent> selectAllRestrictStudent(
    		@Param("teachingClassIds") List<Long> teachingClassIds);

    void deleteByClassId(Long classId);

}