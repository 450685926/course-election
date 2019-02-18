package com.server.edu.election.studentelec.cache;

public class TeachingClassCache implements CacheEntity {
    private Long teachingClassId;
    private Long courseId;
    /** 是否实践课*/
    private boolean isPractice = false;
    /** 是否重修班*/
    private boolean isRetraining = false;
    /** 是否公选课*/
    private boolean isPublicElective = false;
    /** 最大人数 */
    private Integer maxNumber;
    /** 当前人数 */
    private Integer currentNumber;

    @Override
    public String getKey() {
        return String.valueOf(teachingClassId);
    }

    public Long getTeachingClassId() {
        return teachingClassId;
    }

    public void setTeachingClassId(Long teachingClassId) {
        this.teachingClassId = teachingClassId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public boolean isPractice() {
        return isPractice;
    }

    public void setPractice(boolean practice) {
        isPractice = practice;
    }

    public boolean isRetraining() {
        return isRetraining;
    }

    public void setRetraining(boolean retraining) {
        isRetraining = retraining;
    }

    public boolean isPublicElective() {
        return isPublicElective;
    }

    public void setPublicElective(boolean publicElective) {
        isPublicElective = publicElective;
    }

    public Integer getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Integer maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Integer getCurrentNumber() {
        return currentNumber;
    }

    public void setCurrentNumber(Integer currentNumber) {
        this.currentNumber = currentNumber;
    }
}
