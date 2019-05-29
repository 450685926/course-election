package com.server.edu.election.dao;

import com.server.edu.election.entity.ElectionConstants;

import tk.mybatis.mapper.common.Mapper;

public interface ElectionConstantsDao extends Mapper<ElectionConstants> {
    /**重修门数上限*/
    String findRebuildCourseNumber();
    /**新选学分上限*/
    String findNewCreditsLimit();
    /**预警学生不及格学分*/
    String findMaxFailCredits();
    /**查询体育课程*/
    String findPECourses();
    /**查询英语课程*/
    String findEnglishCourses();
}