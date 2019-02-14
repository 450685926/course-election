package com.server.edu.election.dto;

import java.util.List;

import org.hibernate.validator.constraints.NotEmpty;

import com.server.edu.common.validator.AddGroup;
import com.server.edu.election.entity.ElcCourseTake;

public class ElcCourseTakeDto extends ElcCourseTake
{
    private static final long serialVersionUID = 1L;
    
    /**
     * 教学班ID
     */
    @NotEmpty(groups = {AddGroup.class})
    private List<Long> teachingClassIds;
    
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

    public List<Long> getTeachingClassIds()
    {
        return teachingClassIds;
    }

    public void setTeachingClassIds(List<Long> teachingClassIds)
    {
        this.teachingClassIds = teachingClassIds;
    }
    
}
