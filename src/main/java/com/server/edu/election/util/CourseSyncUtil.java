package com.server.edu.election.util;


import com.server.edu.dmskafka.dataSync.data.CourseSync;
import com.server.edu.election.entity.Course;

public class CourseSyncUtil
{
    public static Course convert(CourseSync c)
    {
        Course s = new Course();
        s.setId(c.getId());
        s.setCode(c.getCode());
        s.setCredits(c.getCredits());
        s.setEnabled(c.getEnabled());
        s.setName(c.getName());
        s.setNameEn(c.getNameEn());
        s.setWeeks(c.getWeeks());
        s.setWeekHour(c.getWeekHour());
        s.setPeriod(c.getPeriod());
        s.setCrossTerm(c.getCrossTerm());
        s.setTerm(c.getTerm());
        s.setTrainingLevel(c.getTrainingLevel());
        s.setFormLearning(c.getFormLearning());
        s.setIsElective(c.getIsElective());
        s.setCollege(c.getCollege());
        s.setNature(c.getNature());
        s.setLabel(c.getLabel());
        s.setTeachingLanguage(c.getTeachingLanguage());
        s.setScoreType(c.getScoreType());
        s.setAssessmentMode(c.getAssessmentMode());
        s.setHeadTeacher(c.getHeadTeacher());
        s.setTeachers(c.getTeachers());
        s.setManagerDeptId(c.getManagerDeptId());
        s.setStatus(c.getStatus());
        s.setRemark(c.getRemark());
        s.setTeachMode(c.getTeachMode());
        return s;
    }
}
