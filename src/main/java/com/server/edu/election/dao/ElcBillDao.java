package com.server.edu.election.dao;

import com.server.edu.election.entity.ElcBill;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface ElcBillDao extends Mapper<ElcBill> {
    void updatePayBatch(List<ElcBill> elcBillList);
}