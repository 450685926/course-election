package com.server.edu.election.dao;

import com.server.edu.election.entity.TeachingClassTeacher;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.vo.TeachingClassTeacherVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Set;

public interface TeachingClassTeacherDao extends Mapper<TeachingClassTeacher> {
    String findTeacherName(String teacherCode);

    TeachingClassTeacherVo findTeacher(String teacherCode);

    List<TeachingClassTeacherVo> findTeachers(@Param("teacherCodes") Set<String> teacherCodes);

    List<String> findTeacherNames(@Param("teacherCodes") List<String> teacherCodes);

    List<String> findTeacherCodes(@Param("keyWord") String keyWord);

    List<String> findNamesByTeachingClassId(Long teachingClassId);

    List<TeachingClassCache> findTeacherClass(@Param("teachingClassIds") List<Long> teachingClassIds);
}