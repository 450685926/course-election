package com.server.edu.mutual.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class MakePlanVO extends PlanCourseTabVo 
{
	  /**
     * 
     */
    private static final long serialVersionUID = 1L;
    //培养计划课程表中的id
    private Long ID;
    //课程编码
    private String courseCode;
    //课程名称
    private String  courseName;
    private Integer chosen;
    private Long courseGroupId;
    private Long parentGroupId;
    private Long groupId;
    private Long modeId;
    private String groupName;
    private String modelName;
    private String directionCode;
    private String directionName;
    //课程区分 0：教学周:1：实践周
    private Integer weekType;
    //学分
    private Double credts;
    //考试形式
    @Code2Text(transformer="X_KSLX")
    private String examMode;

    @Override
    public Double getPeriod() {
        return period;
    }

    @Override
    public void setPeriod(Double period) {
        this.period = period;
    }

    //总学时
    private Double period;
    //周数
    private Integer weeks;
    //是否必修
    private String compulsory;
    //上机时间
    private Double computeTime;
    //实验实数
    private Double experimentTime;
    private String semester1;
    private String semester2;
    private String semester3;
    private String semester4;
    private String semester5;
    private String semester6;
    private String semester7;
    private String semester8;
    private String semester9;
    private String semester10;
    private String semester11;
    private String semester12;
    private String semester13;
    private String semester14;
    private String semester15;
    private String semester16;
    //备注
    private String remark;
    public Long getID()
    {
        return ID;
    }
    public void setID(Long iD)
    {
        ID = iD;
    }
    public Integer getChosen()
    {
        return chosen;
    }
    public void setChosen(Integer chosen)
    {
        this.chosen = chosen;
    }
    public Long getCourseGroupId()
    {
        return courseGroupId;
    }
    public void setCourseGroupId(Long courseGroupId)
    {
        this.courseGroupId = courseGroupId;
    }
    public Long getParentGroupId()
    {
        return parentGroupId;
    }
    public void setParentGroupId(Long parentGroupId)
    {
        this.parentGroupId = parentGroupId;
    }
    public String getGroupName()
    {
        return groupName;
    }
    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }
    public String getModelName()
    {
        return modelName;
    }
    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }
    public String getDirectionCode()
    {
        return directionCode;
    }
    public void setDirectionCode(String directionCode)
    {
        this.directionCode = directionCode;
    }
    public String getDirectionName()
    {
        return directionName;
    }
    public void setDirectionName(String directionName)
    {
        this.directionName = directionName;
    }
    public String getExamMode()
    {
        return examMode;
    }
    public void setExamMode(String examMode)
    {
        this.examMode = examMode;
    }
    public Double getComputeTime()
    {
        return computeTime;
    }
    public void setComputeTime(Double computeTime)
    {
        this.computeTime = computeTime;
    }
    public Double getExperimentTime()
    {
        return experimentTime;
    }
    public void setExperimentTime(Double experimentTime)
    {
        this.experimentTime = experimentTime;
    }
    public String getSemester1()
    {
        return semester1;
    }
    public void setSemester1(String semester1)
    {
        this.semester1 = semester1;
    }
    public String getSemester2()
    {
        return semester2;
    }
    public void setSemester2(String semester2)
    {
        this.semester2 = semester2;
    }
    public String getSemester3()
    {
        return semester3;
    }
    public void setSemester3(String semester3)
    {
        this.semester3 = semester3;
    }
    public String getSemester4()
    {
        return semester4;
    }
    public void setSemester4(String semester4)
    {
        this.semester4 = semester4;
    }
    public String getSemester5()
    {
        return semester5;
    }
    public void setSemester5(String semester5)
    {
        this.semester5 = semester5;
    }
    public String getSemester6()
    {
        return semester6;
    }
    public void setSemester6(String semester6)
    {
        this.semester6 = semester6;
    }
    public String getSemester7()
    {
        return semester7;
    }
    public void setSemester7(String semester7)
    {
        this.semester7 = semester7;
    }
    public String getSemester8()
    {
        return semester8;
    }
    public void setSemester8(String semester8)
    {
        this.semester8 = semester8;
    }
    public String getSemester9()
    {
        return semester9;
    }
    public void setSemester9(String semester9)
    {
        this.semester9 = semester9;
    }
    public String getSemester10()
    {
        return semester10;
    }
    public void setSemester10(String semester10)
    {
        this.semester10 = semester10;
    }
    public String getSemester11()
    {
        return semester11;
    }
    public void setSemester11(String semester11)
    {
        this.semester11 = semester11;
    }
    public String getSemester12()
    {
        return semester12;
    }
    public void setSemester12(String semester12)
    {
        this.semester12 = semester12;
    }
    public String getSemester13()
    {
        return semester13;
    }
    public void setSemester13(String semester13)
    {
        this.semester13 = semester13;
    }
    public String getSemester14()
    {
        return semester14;
    }
    public void setSemester14(String semester14)
    {
        this.semester14 = semester14;
    }
    public String getSemester15()
    {
        return semester15;
    }
    public void setSemester15(String semester15)
    {
        this.semester15 = semester15;
    }
    public String getSemester16()
    {
        return semester16;
    }
    public void setSemester16(String semester16)
    {
        this.semester16 = semester16;
    }
    
    public Long getGroupId()
    {
        return groupId;
    }
    public void setGroupId(Long groupId)
    {
        this.groupId = groupId;
    }
    public Long getModeId()
    {
        return modeId;
    }
    public void setModeId(Long modeId)
    {
        this.modeId = modeId;
    }
    
    public String getCourseCode()
    {
        return courseCode;
    }
    public void setCourseCode(String courseCode)
    {
        this.courseCode = courseCode;
    }
    public String getCourseName()
    {
        return courseName;
    }
    public void setCourseName(String courseName)
    {
        this.courseName = courseName;
    }
    public Integer getWeekType()
    {
        return weekType;
    }
    public void setWeekType(Integer weekType)
    {
        this.weekType = weekType;
    }
    public Double getCredts()
    {
        return credts;
    }
    public void setCredts(Double credts)
    {
        this.credts = credts;
    }


    public Integer getWeeks()
    {
        return weeks;
    }
    public void setWeeks(Integer weeks)
    {
        this.weeks = weeks;
    }
    public String getCompulsory()
    {
        return compulsory;
    }
    public void setCompulsory(String compulsory)
    {
        this.compulsory = compulsory;
    }
    public String getRemark()
    {
        return remark;
    }
    public void setRemark(String remark)
    {
        this.remark = remark;
    }
    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }
    @Override
    public String toString()
    {
        return "MakePlanVO [ID=" + ID + ", chosen=" + chosen
            + ", courseGroupId=" + courseGroupId + ", parentGroupId="
            + parentGroupId + ", groupId=" + groupId + ", modeId=" + modeId
            + ", groupName=" + groupName + ", modelName=" + modelName
            + ", directionCode=" + directionCode + ", directionName="
            + directionName + ", examMode=" + examMode + ", computeTime="
            + computeTime + ", experimentTime=" + experimentTime
            + ", semester1=" + semester1 + ", semester2=" + semester2
            + ", semester3=" + semester3 + ", semester4=" + semester4
            + ", semester5=" + semester5 + ", semester6=" + semester6
            + ", semester7=" + semester7 + ", semester8=" + semester8
            + ", semester9=" + semester9 + ", semester10=" + semester10
            + ", semester11=" + semester11 + ", semester12=" + semester12
            + ", semester13=" + semester13 + ", semester14=" + semester14
            + ", semester15=" + semester15 + ", semester16=" + semester16
            + ", getID()=" + getID() + ", getChosen()=" + getChosen()
            + ", getCourseGroupId()=" + getCourseGroupId()
            + ", getParentGroupId()=" + getParentGroupId() + ", getGroupName()="
            + getGroupName() + ", getModelName()=" + getModelName()
            + ", getDirectionCode()=" + getDirectionCode()
            + ", getDirectionName()=" + getDirectionName() + ", getExamMode()="
            + getExamMode() + ", getComputeTime()=" + getComputeTime()
            + ", getExperimentTime()=" + getExperimentTime()
            + ", getSemester1()=" + getSemester1() + ", getSemester2()="
            + getSemester2() + ", getSemester3()=" + getSemester3()
            + ", getSemester4()=" + getSemester4() + ", getSemester5()="
            + getSemester5() + ", getSemester6()=" + getSemester6()
            + ", getSemester7()=" + getSemester7() + ", getSemester8()="
            + getSemester8() + ", getSemester9()=" + getSemester9()
            + ", getSemester10()=" + getSemester10() + ", getSemester11()="
            + getSemester11() + ", getSemester12()=" + getSemester12()
            + ", getSemester13()=" + getSemester13() + ", getSemester14()="
            + getSemester14() + ", getSemester15()=" + getSemester15()
            + ", getSemester16()=" + getSemester16() + ", getGroupId()="
            + getGroupId() + ", getModeId()=" + getModeId()
            + ", getParentLabID()=" + getParentLabID() + ", getParentLabName()="
            + getParentLabName() + ", getLabID()=" + getLabID()
            + ", getLabName()=" + getLabName() + ", getCourseCode()="
            + getCourseCode() + ", getScore()=" + getScore()
            + ", getSelCourse()=" + getSelCourse() + ", getCourseName()="
            + getCourseName() + ", getCredits()=" + getCredits()
            + ", getPeriod()=" + getPeriod() + ", getTerm()=" + getTerm()
            + ", getCollege()=" + getCollege() + ", getRemark()=" + getRemark()
            + ", getCompulsory()=" + getCompulsory() + ", getIsPass()="
            + getIsPass() + ", toString()=" + super.toString() + ", getClass()="
            + getClass() + ", hashCode()=" + hashCode() + "]";
    }


}
