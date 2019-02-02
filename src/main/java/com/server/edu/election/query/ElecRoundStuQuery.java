package com.server.edu.election.query;

import javax.validation.constraints.NotNull;

import com.server.edu.election.dto.Student4Elc;

public class ElecRoundStuQuery extends Student4Elc
{
    @NotNull
    private Long roundId;
    
    private String studentId;
    
    private String name;
    
    /**学院*/
    private String faculty;
    /**专业*/
    private String profession;
    /**培养层次*/
    private String trainingLevel;
    /**是否留学生*/
    private String isOverseas;
    /**年级*/
    private String grade;

    public Long getRoundId()
    {
        return roundId;
    }

    public void setRoundId(Long roundId)
    {
        this.roundId = roundId;
    }

    @Override
    public String getStudentId()
    {
        return studentId;
    }

    @Override
    public void setStudentId(String studentId)
    {
        this.studentId = studentId;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
    }

    @Override
    public String getFaculty()
    {
        return faculty;
    }

    @Override
    public void setFaculty(String faculty)
    {
        this.faculty = faculty;
    }

    @Override
    public String getProfession()
    {
        return profession;
    }

    @Override
    public void setProfession(String profession)
    {
        this.profession = profession;
    }

    @Override
    public String getTrainingLevel()
    {
        return trainingLevel;
    }

    @Override
    public void setTrainingLevel(String trainingLevel)
    {
        this.trainingLevel = trainingLevel;
    }

    @Override
    public String getIsOverseas()
    {
        return isOverseas;
    }

    @Override
    public void setIsOverseas(String isOverseas)
    {
        this.isOverseas = isOverseas;
    }

    @Override
    public String getGrade()
    {
        return grade;
    }

    @Override
    public void setGrade(String grade)
    {
        this.grade = grade;
    }
    
}
