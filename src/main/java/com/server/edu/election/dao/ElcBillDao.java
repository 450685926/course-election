package com.server.edu.election.dao;

import com.server.edu.election.dto.PayOrderDto;
import com.server.edu.election.dto.StudentRePaymentDto;
import com.server.edu.election.entity.ElcBill;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface ElcBillDao extends Mapper<ElcBill>, MySqlMapper<ElcBill> {
    void updatePayBatch(List<ElcBill> elcBillList);

    List<StudentRePaymentDto> payDetail(StudentRePaymentDto condition);

    List<StudentRePaymentDto> payDetailById(StudentRePaymentDto condition);

    List<PayOrderDto> findToBaseById(@Param("ids") List<Long> ids);
}