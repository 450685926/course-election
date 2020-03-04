package com.server.edu.mutual.testvo;

public class CopyPreMajorParam {
    private String majorCode; //专业code,多个专业中间用‘,’分开
    private String oldMinorGrade; //需要复制的辅修年级
    private String newMinorGrade; //最终复制的辅修年级
    private String gradeLimit; //年级限制 中间也用‘,’分开

    public String getMajorCode() {
        return majorCode;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public String getOldMinorGrade() {
        return oldMinorGrade;
    }

    public void setOldMinorGrade(String oldMinorGrade) {
        this.oldMinorGrade = oldMinorGrade;
    }

    public String getNewMinorGrade() {
        return newMinorGrade;
    }

    public void setNewMinorGrade(String newMinorGrade) {
        this.newMinorGrade = newMinorGrade;
    }

    public String getGradeLimit() {
        return gradeLimit;
    }

    public void setGradeLimit(String gradeLimit) {
        this.gradeLimit = gradeLimit;
    }
}
