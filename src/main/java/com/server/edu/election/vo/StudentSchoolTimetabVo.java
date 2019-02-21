package com.server.edu.election.vo;

import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TimeTable;

import java.util.List;

/**
 * @description: 学生课表
 * @author: bear
 * @create: 2019-02-15 15:15
 */
public class StudentSchoolTimetabVo extends Student {
    private Double totalCredits;
    private List<StudentSchoolTimetab> list;
    private List<TimeTable> timeTables;
    private List<ClassTeacherDto> teacherDtos;

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
