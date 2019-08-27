package com.server.edu.election.vo;

import java.util.List;

import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.entity.Student;

/**
 * @description: 学生课表
 * @author: bear
 * @create: 2019-02-15 15:15
 */
public class StudentSchoolTimetabVo extends Student {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Double totalCredits;
    private List<StudentSchoolTimetab> list;
    private List<TimeTable> timeTables;
    private List<ClassTeacherDto> teacherDtos;
    private String teacherName;

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<ClassTeacherDto> getTeacherDtos() {
        return teacherDtos;
    }

    public void setTeacherDtos(List<ClassTeacherDto> teacherDtos) {
        this.teacherDtos = teacherDtos;
    }

    public List<TimeTable> getTimeTables() {
        return timeTables;
    }

    public void setTimeTables(List<TimeTable> timeTables) {
        this.timeTables = timeTables;
    }

    public Double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(Double totalCredits) {
        this.totalCredits = totalCredits;
    }

    public List<StudentSchoolTimetab> getList() {
        return list;
    }

    public void setList(List<StudentSchoolTimetab> list) {
        this.list = list;
    }
}
