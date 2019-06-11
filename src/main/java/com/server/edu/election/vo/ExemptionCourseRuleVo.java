package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.entity.ExemptionCourseMaterial;
import com.server.edu.election.entity.ExemptionCourseRule;

/**
 * @description: 免修免考申请规则
 * @author: bear
 * @create: 2019-02-01 15:59
 */
public class ExemptionCourseRuleVo extends ExemptionCourseRule {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String calendarName;

    private List<ExemptionCourseMaterial> list;

    private String keyWord;

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

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
