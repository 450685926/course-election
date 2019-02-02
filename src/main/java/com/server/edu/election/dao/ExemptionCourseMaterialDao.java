package com.server.edu.election.dao;

import com.server.edu.election.entity.ExemptionCourseMaterial;

import java.util.List;

/**
 * @description: 免修免考材料接口
 * @author: bear
 * @create: 2019-02-01 18:34
 */
public interface ExemptionCourseMaterialDao {
    int deleteByPrimaryKey(Long id);

    int insert(ExemptionCourseMaterial record);

    int insertSelective(ExemptionCourseMaterial record);

    ExemptionCourseMaterial selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExemptionCourseMaterial record);

    int updateByPrimaryKey(ExemptionCourseMaterial record);

    void deleteExemptionCourseMaterial(List<Long> list);

    void addExemptionMaterial(List<ExemptionCourseMaterial> list);
}
