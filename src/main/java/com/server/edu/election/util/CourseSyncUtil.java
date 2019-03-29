package com.server.edu.election.util;

import com.server.edu.dmskafka.dataSync.data.CourseSync;
import com.server.edu.election.entity.Course;

public class CourseSyncUtil
{
    public static Course convert(CourseSync c)
    {
        Course s = new Course();
        s.setAssessmentMode(c.getAssessmentMode());
        s.setCode(c.getCode());
        s.setCollege(c.getCollege());
        s.setCredits(c.getCredits());
        s.setCrossTerm(c.getCrossTerm());
        s.setFormLearning(c.getFormLearning());
        s.setId(c.getId());
        s.setIsElective(c.getIsElective());
        s.setLabel(c.getLabel());
        s.setManagerDeptId(c.getManagerDeptId());
        s.setName(c.getName());
        s.setNameEn(c.getNameEn());
        s.setPeriod(c.getPeriod());
        s.setRemark(c.getRemark());
        s.setTrainingLevel(c.getTrainingLevel());
        s.setWeekHour(c.getWeekHour());
        return s;
    }
}
