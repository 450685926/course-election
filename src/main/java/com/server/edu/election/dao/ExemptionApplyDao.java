package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.vo.ExemptionApplyManageVo;

public interface ExemptionApplyDao {
    int deleteByPrimaryKey(Long id);

    int insert(ExemptionApplyManage record);

    int insertSelective(ExemptionApplyManage record);

    ExemptionApplyManage selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ExemptionApplyManage record);

    int updateByPrimaryKey(ExemptionApplyManage record);

    Page<ExemptionApplyManageVo> findExemptionApply(ExemptionApplyCondition condition);
}
