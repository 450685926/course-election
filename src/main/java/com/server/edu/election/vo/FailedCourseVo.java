package com.server.edu.election.vo;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.election.entity.CourseOpen;

/**
 * 不及格课程实体类
 */
public class FailedCourseVo extends CourseOpen {
    /**学期全名*/
    private String calendarName;

    @Code2Text(DictTypeEnum.X_KSLX)
    private String assessmentMode;

    /**是否已经选课*/
    private boolean isSelected;

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getAssessmentMode() {
        return assessmentMode;
    }

    public void setAssessmentMode(String assessmentMode) {
        this.assessmentMode = assessmentMode;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
