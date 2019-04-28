package com.server.edu.election.studentelec.context;

public class PlanCourse extends ElecCourse
{

    /**课程分类*/
    private Long label;

    /**实践周*/
    private Integer weekType;

    /**学期周学时*/
    private String semester;

    public Integer getWeekType() {
        return weekType;
    }

    public void setWeekType(Integer weekType) {
        this.weekType = weekType;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Long getLabel() {
        return label;
    }

    public void setLabel(Long label) {
        this.label = label;
    }


}
