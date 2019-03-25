package com.server.edu.election.dto;

import com.server.edu.election.entity.CourseOpen;

public class CourseOpenDto extends CourseOpen
{
    private Long roundId;
    
    private Long teachingClassId;
    
    private String teachingClassCode;

    private String campus;

    private String teachClassType;

    private Integer maxNumber;

    private Integer currentNumber;

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getTeachClassType() {
        return teachClassType;
    }

    public void setTeachClassType(String teachClassType) {
        this.teachClassType = teachClassType;
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

    public Long getRoundId()
    {
        return roundId;
    }
    
    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public String getTeachingClassCode()
    {
        return teachingClassCode;
    }
    
    public void setTeachingClassCode(String teachingClassCode)
    {
        this.teachingClassCode = teachingClassCode;
    }
    
}
