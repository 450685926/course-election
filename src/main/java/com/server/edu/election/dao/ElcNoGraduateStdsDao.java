package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.vo.ElcNoGraduateStdsVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ElcNoGraduateStdsDao extends Mapper<ElcNoGraduateStds> {

    /**查询结业生或留学结业生*/
    Page<ElcNoGraduateStdsVo> findOverseasOrGraduate(ElcNoGraduateStdsVo elcNoGraduateStds);

    /**查询结业生是否存在*/
    ElcNoGraduateStds findStudentByCode(String studentCode);

    /**新增结业生*/
    void addOverseasOrGraduate(String code);

    /**删除结业生*/
    void deleteOverseasOrGraduate(List<Long> list);
}