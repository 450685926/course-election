package com.server.edu.election.vo;

import com.server.edu.election.entity.ExemptionCourseMaterial;
import com.server.edu.election.entity.ExemptionCourseRule;

import java.util.List;

/**
 * @description: 免修免考申请规则
 * @author: bear
 * @create: 2019-02-01 15:59
 */
public class ExemptionCourseRuleVo extends ExemptionCourseRule {
    private String calendarName;

    private List<ExemptionCourseMaterial> list;

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public List<ExemptionCourseMaterial> getList() {
        return list;
    }

    public void setList(List<ExemptionCourseMaterial> list) {
        this.list = list;
    }
}
