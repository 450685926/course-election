package com.server.edu.election.dto;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;

@CodeI18n
public class SuggestProfessionDto
{
	private Long teachingClassId;
	
    private Integer grade;

    @Code2Text(transformer = "G_ZY")
    private String profession;
    
    private Integer number;
    
    public Long getTeachingClassId() {
		return teachingClassId;
	}

	public void setTeachingClassId(Long teachingClassId) {
		this.teachingClassId = teachingClassId;
	}

	public Integer getGrade()
    {
        return grade;
    }
    
    public void setGrade(Integer grade)
    {
        this.grade = grade;
    }
    
    public String getProfession()
    {
        return profession;
    }
    
    public void setProfession(String profession)
    {
        this.profession = profession;
    }
    
    public Integer getNumber()
    {
        return number;
    }
    
    public void setNumber(Integer number)
    {
        this.number = number;
    }
    
}
