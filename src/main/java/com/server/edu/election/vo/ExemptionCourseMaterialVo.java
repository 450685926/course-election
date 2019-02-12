package com.server.edu.election.vo;

import com.server.edu.election.entity.ExemptionCourseMaterial;

import java.util.List;

/**
 * @description: 申请条件限制
 * @author: bear
 * @create: 2019-02-12 17:29
 */
public class ExemptionCourseMaterialVo {
    private Double score;
    private Double minimumPassScore;

    private List<ExemptionCourseMaterial> list;

    public List<ExemptionCourseMaterial> getList() {
        return list;
    }

    public void setList(List<ExemptionCourseMaterial> list) {
        this.list = list;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Double getMinimumPassScore() {
        return minimumPassScore;
    }

    public void setMinimumPassScore(Double minimumPassScore) {
        this.minimumPassScore = minimumPassScore;
    }
}
