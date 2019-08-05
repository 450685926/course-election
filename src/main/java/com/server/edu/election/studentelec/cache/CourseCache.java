package com.server.edu.election.studentelec.cache;

import java.util.Set;

import com.server.edu.election.studentelec.context.ElecCourse;

/**
 * 课程缓存对象
 */
public class CourseCache extends ElecCourse
{
    
    private Set<Long> teachClassIds;
    
    public CourseCache()
    {
    }
    
    public CourseCache(ElecCourse course)
    {
        this.setCourseCode(course.getCourseCode());
        this.setCourseName(course.getCourseName());
        this.setCredits(course.getCredits());
        this.setNameEn(course.getNameEn());
        this.setNature(course.getNature());
    }
    
    public Set<Long> getTeachClassIds()
    {
        return teachClassIds;
    }
    
    public void setTeachClassIds(Set<Long> teachClassIds)
    {
        this.teachClassIds = teachClassIds;
    }
    
}
