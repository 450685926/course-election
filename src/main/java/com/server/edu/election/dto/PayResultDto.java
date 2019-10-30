package com.server.edu.election.dto;

import com.server.edu.common.entity.PayResult;

/**
 * @author: kan yuanfeng
 * @date: 2019/10/30 11:24
 * @description: 财务对账专用实体
 */
public class PayResultDto extends PayResult {
    /**分表标识*/
    private Integer index;

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    /**是否支付 1是0否*/
    private Integer paid;

    public Integer getPaid() {
        return paid;
    }

    public void setPaid(Integer paid) {
        this.paid = paid;
    }
}
