package com.server.edu.election.studentelec.cache;

import java.util.Set;

public class Course2TeachingClassCache{
    private Long courseId;
    private String name;
    private Set<Long> teachingClassIds;

    public Course2TeachingClassCache(Long courseId, String name, Set<Long> teachingClassIds) {
        this.courseId = courseId;
        this.name = name;
        this.teachingClassIds = teachingClassIds;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Long> getTeachingClassIds() {
        return teachingClassIds;
    }

    public void setTeachingClassIds(Set<Long> teachingClassIds) {
        this.teachingClassIds = teachingClassIds;
    }
}
