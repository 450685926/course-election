package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;

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

    void deleteByClassId(Long classId);
}