package com.server.edu.election.vo;

import com.server.edu.election.dto.ClassTeacherDto;
import com.server.edu.election.dto.StudentSchoolTimetab;
import com.server.edu.election.entity.Student;

import java.util.List;

/**
 * @description: 学生课表
 * @author: bear
 * @create: 2019-02-15 15:15
 */
public class StudentSchoolTimetabVo extends Student {
    private Double totalCredits;
    private List<StudentSchoolTimetab> list;
    private List<ClassTeacherDto> classList;

    public List<ClassTeacherDto> getClassList() {
        return classList;
    }

    public void setClassList(List<ClassTeacherDto> classList) {
        this.classList = classList;
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
