package com.server.edu.election.dto;

import com.server.edu.election.entity.ElcCourseTake;

public class ElcCourseTakeDto extends ElcCourseTake
{
    private static final long serialVersionUID = 1L;
    
    /**1.实践课程 0.教学*/
    private Integer lessonType;
    
    /**
     * 校区（取字典X_XQ）
     */
    private String campus;
    
    public Integer getLessonType()
    {
        return lessonType;
    }
    
    public void setLessonType(Integer lessonType)
    {
        this.lessonType = lessonType;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }

}
