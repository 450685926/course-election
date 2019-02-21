package com.server.edu.election.dao;

import com.server.edu.election.entity.ExemptionCourseMaterial;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description: 免修免考材料接口
 * @author: bear
 * @create: 2019-02-01 18:34
 */
public interface ExemptionCourseMaterialDao extends Mapper<ExemptionCourseMaterial> {


    void deleteExemptionCourseMaterial(List<Long> list);

    void addExemptionMaterial(List<ExemptionCourseMaterial> list);

    List<ExemptionCourseMaterial> findMaterialById(Long id);
}
