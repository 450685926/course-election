package com.server.edu.election.dto;

import java.io.Serializable;

/**
 * @description: 选课限制男女生人数
 * @author: bear
 * @create: 2019-05-06 14:57
 */
public class ElcCourseLimitDto implements Serializable{
    private Integer sex;
    private Integer number;

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
