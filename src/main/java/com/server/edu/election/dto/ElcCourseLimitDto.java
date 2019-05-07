package com.server.edu.election.dto;

import java.io.Serializable;

/**
 * @description: 选课限制男女生人数
 * @author: bear
 * @create: 2019-05-06 14:57
 */
public class ElcCourseLimitDto implements Serializable{
    private Integer maleNum;
    private Integer feMaleNum;

    public Integer getMaleNum() {
        return maleNum;
    }

    public void setMaleNum(Integer maleNum) {
        this.maleNum = maleNum;
    }

    public Integer getFeMaleNum() {
        return feMaleNum;
    }

    public void setFeMaleNum(Integer feMaleNum) {
        this.feMaleNum = feMaleNum;
    }
}
