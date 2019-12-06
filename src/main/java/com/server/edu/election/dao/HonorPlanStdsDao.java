package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.HonorPlanStds;
import com.server.edu.election.query.HonorPlanStdsQuery;
import com.server.edu.election.vo.HonorPlanStdsVo;
import tk.mybatis.mapper.common.Mapper;


public interface HonorPlanStdsDao extends Mapper<HonorPlanStds> {

    Page<HonorPlanStdsVo> pageList(HonorPlanStdsQuery condition);
}


