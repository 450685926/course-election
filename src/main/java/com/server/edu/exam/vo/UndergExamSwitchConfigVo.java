package com.server.edu.exam.vo;

import com.server.edu.exam.entity.UndergExamSwitchConfig;
import com.server.edu.exam.entity.UndergExamSwitchDepartRelation;

import java.util.List;
import java.util.Set;

/**
 * 功能描述：本科生排考开关 vo
 *
 * @ClassName UndergExamSwitchConfigVo
 * @Author zhaoerhu
 * @Date 2019/8/19 23:22
 */
public class UndergExamSwitchConfigVo extends UndergExamSwitchConfig {
    //排考开关关联学院code列表
    private List<UndergExamSwitchDepartRelation> relations;

    //排考开关删除id集合
    private Set<Long> delIdSet;

    public List<UndergExamSwitchDepartRelation> getRelations() {
        return relations;
    }

    public void setRelations(List<UndergExamSwitchDepartRelation> relations) {
        this.relations = relations;
    }

    public Set<Long> getDelIdSet() {
        return delIdSet;
    }

    public void setDelIdSet(Set<Long> delIdSet) {
        this.delIdSet = delIdSet;
    }
}
