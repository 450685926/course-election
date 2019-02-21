package com.server.edu.election.dto;

import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.annotation.Code2Text;

/**
 * 教学
 * 
 * 
 */
public class LessonDto
{
    /**
     * 课程代码
     */
    private String courseCode;
    
    /**
     * 教学班ID
     */
    private Long teachingClassId;
    
    /**
     * 校区（取字典X_XQ）
     */
    @Code2Text(DictTypeEnum.X_XQ)
    private String campus;
    
    /**
     * 开班人数
     */
    private Integer number;
    
    /**1.实践课程 0.教学*/
    private Integer lessonType;
    
    /**是否历史已选的课程*/
    private boolean hisFlag;
    /**历史已选的课程是否通过*/
    private boolean hisCoursePassed;
    /**是否本学期已选的课程*/
    private boolean electedFlag;
    /**是否成绩为优的课程*/
    private boolean goodFlag;
    
    public String getCourseCode()
    {
        return courseCode;
    }
    
    public void setCourseCode(String courseId)
    {
        this.courseCode = courseId;
    }
    
    public Long getTeachingClassId()
    {
        return teachingClassId;
    }
    
    public void setTeachingClassId(Long teachingClassId)
    {
        this.teachingClassId = teachingClassId;
    }
    
    public String getCampus()
    {
        return campus;
    }
    
    public void setCampus(String campus)
    {
        this.campus = campus;
    }
    
    public Integer getNumber()
    {
        return number;
    }
    
    public void setNumber(Integer number)
    {
        this.number = number;
    }
    
    public Integer getLessonType()
    {
        return lessonType;
    }
    
    public void setLessonType(Integer lessonType)
    {
        this.lessonType = lessonType;
    }

    public boolean isHisFlag()
    {
        return hisFlag;
    }

    public void setHisFlag(boolean hisFlag)
    {
        this.hisFlag = hisFlag;
    }
    
    public boolean isHisCoursePassed()
    {
        return hisCoursePassed;
    }

    public void setHisCoursePassed(boolean hisCoursePassed)
    {
        this.hisCoursePassed = hisCoursePassed;
    }

    public boolean isElectedFlag()
    {
        return electedFlag;
    }

    public void setElectedFlag(boolean electedFlag)
    {
        this.electedFlag = electedFlag;
    }

    public boolean isGoodFlag()
    {
        return goodFlag;
    }

    public void setGoodFlag(boolean goodFlag)
    {
        this.goodFlag = goodFlag;
    }

    
}
