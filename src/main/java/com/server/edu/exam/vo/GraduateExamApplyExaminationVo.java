package com.server.edu.exam.vo;

import com.server.edu.dictionary.annotation.Code2Text;
import com.server.edu.dictionary.annotation.CodeI18n;
import com.server.edu.exam.entity.GraduateExamApplyExamination;

import java.util.List;

/**
 * @description:
 * @author: bear
 * @create: 2019-08-29 14:21
 */

@CodeI18n
public class GraduateExamApplyExaminationVo extends GraduateExamApplyExamination {

    @Code2Text(transformer = "X_YX")
    private String collenge;
    @Code2Text(transformer = "X_XQ")
    private String campus;
    private String studentName;
    private String courseName;
    private String keyword;
    private String isAdmin;
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    private List<String> facultys;

    public List<String> getFacultys() {
        return facultys;
    }

    public void setFacultys(List<String> facultys) {
        this.facultys = facultys;
    }

    public String getCollenge() {
        return collenge;
    }

    public void setCollenge(String collenge) {
        this.collenge = collenge;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
}
