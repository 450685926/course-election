package com.server.edu.election.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.election.entity.ExemptionApplyManage;

/**
 * @description: 免修免考返回结果
 * @author: bear
 * @create: 2019-02-02 17:32
 */
@CodeI18n
public class ExemptionApplyManageVo extends ExemptionApplyManage{

    @Code2Text(transformer="X_YX")
    private String faculty;

    @Code2Text(transformer="G_ZY")
    private String profession;

    private String calendarName;

    private String examineResultStr;

    public String getExamineResultStr() {
        return examineResultStr;
    }

    public void setExamineResultStr(String examineResultStr) {
        this.examineResultStr = examineResultStr;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }
}
