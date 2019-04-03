package com.server.edu.election.util;

import com.server.edu.dmskafka.dataSync.data.StudentInfoSync;
import com.server.edu.dmskafka.dataSync.data.StudentStatusSync;
import com.server.edu.election.entity.Student;

public class StuInfoSyncUtil
{
    public static Student convertInfo(StudentInfoSync b)
    {
        Student s = new Student();
        s.setName(b.getName());
        s.setSex(b.getSex());
        s.setStudentCode(b.getStudentId());
        return s;
    }
    
    public static Student convertStatus(StudentStatusSync b)
    {
        Student s = new Student();
        s.setCampus(b.getCampus());
        s.setDegreeCategory(b.getDegreeCategory());
        s.setEnrolSeason(b.getEnrolSeason());
        s.setFaculty(b.getFaculty());
        s.setFormLearning(b.getFormLearning());
        s.setGrade(b.getGrade());
        s.setIsOverseas(b.getIsOverseas());
        s.setProfession(b.getProfession());
        s.setRegistrationStatus(b.getRegistrationStatus());
        s.setResearchDirection(b.getResearchDirection());
        s.setSpcialPlan(b.getSpcialPlan());
        s.setStudentCategory(b.getStudentCategory());
        s.setStudentCode(b.getStudentId());
        s.setTrainingCategory(b.getTrainingLevel());
        s.setTrainingLevel(b.getTrainingLevel());
        
        return s;
    }
}
