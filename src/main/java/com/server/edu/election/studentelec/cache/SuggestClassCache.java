package com.server.edu.election.studentelec.cache;

public class SuggestClassCache implements CacheEntity {
    /**若年级为空则为研究生专业 否则为本科专业*/
    private Integer grade;
    private String majorCode;
    private Long teachingClassId;

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    @Override
    public String getKey() {
        return grade!=null?String.format("%s-%s",majorCode,grade):majorCode;
    }
}
