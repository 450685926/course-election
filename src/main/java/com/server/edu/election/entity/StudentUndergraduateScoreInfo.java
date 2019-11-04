package com.server.edu.election.entity;

import java.io.Serializable;
import javax.persistence.*;

@Table(name = "student_undergraduate_score_info_t")
public class StudentUndergraduateScoreInfo implements Serializable {
    /**
     * 主键
     */
    @Id
    @Column(name = "ID")
    private Long id;

    /**
     * 学年
     */
    @Column(name = "ACADEMIC_YEAR_")
    private Long academicYear;

    /**
     * 学期
     */
    @Column(name = "SEMESTER_")
    private Long semester;

    /**
     * 学号
     */
    @Column(name = "STUDENT_NUM_")
    private String studentNum;

    /**
     * 姓名
     */
    @Column(name = "STUDENT_NAME_")
    private String studentName;

    /**
     * 课程序号
     */
    @Column(name = "COURSE_NUM_")
    private String courseNum;

    /**
     * 课程代码
     */
    @Column(name = "COURSE_CODE_")
    private String courseCode;

    /**
     * 课程名称
     */
    @Column(name = "COURSE_NAME_")
    private String courseName;

    /**
     * 成绩性质
     */
    @Column(name = "SCORE_PROPERTY_")
    private String scoreProperty;

    /**
     * 成绩类型
     */
    @Column(name = "SCORE_TYPE_")
    private String scoreType;

    /**
     * 是否确认
     */
    @Column(name = "IS_CONFIRM_")
    private String isConfirm;

    /**
     * 成绩数值
     */
    @Column(name = "SCORE_VALUE_")
    private String scoreValue;

    /**
     * 成绩
     */
    @Column(name = "SCORE_")
    private String score;

    /**
     * 绩点
     */
    @Column(name = "GRADE_POINT_")
    private Double gradePoint;

    /**
     * 是否通过
     */
    @Column(name = "IS_PASS_")
    private String isPass;

    /**
     * 成绩性质名称
     */
    @Column(name = "SCORE_PROPERTY_NAME_")
    private String scorePropertyName;

    /**
     * 课程类别
     */
    @Column(name = "COURSE_CATEGORY_")
    private String courseCategory;

    /**
     * 课程性质
     */
    @Column(name = "COURSE_PROPERTY_")
    private String courseProperty;

    /**
     * 学分
     */
    @Column(name = "CREDIT_")
    private Double credit;

    /**
     * 教师名称
     */
    @Column(name = "TEARCHER_NAME_")
    private String tearcherName;

    /**
     * 课程类型
     */
    @Column(name = "COURES_TYPE_")
    private String couresType;

    /**
     * 考试方式
     */
    @Column(name = "EXAM_WAY_")
    private String examWay;

    /**
     * 考试方式名称
     */
    @Column(name = "EXAM_WAY_NAME_")
    private String examWayName;

    /**
     * 公选课类别
     */
    @Column(name = "PUBLIC_COURSES_TYPE_")
    private String publicCoursesType;

    /**
     * 教师代码
     */
    @Column(name = "TEARCHER_CODE")
    private String tearcherCode;

    private static final long serialVersionUID = 1L;

    /**
     * 获取主键
     *
     * @return ID - 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取学年
     *
     * @return ACADEMIC_YEAR_ - 学年
     */
    public Long getAcademicYear() {
        return academicYear;
    }

    /**
     * 设置学年
     *
     * @param academicYear 学年
     */
    public void setAcademicYear(Long academicYear) {
        this.academicYear = academicYear;
    }

    /**
     * 获取学期
     *
     * @return SEMESTER_ - 学期
     */
    public Long getSemester() {
        return semester;
    }

    /**
     * 设置学期
     *
     * @param semester 学期
     */
    public void setSemester(Long semester) {
        this.semester = semester;
    }

    /**
     * 获取学号
     *
     * @return STUDENT_NUM_ - 学号
     */
    public String getStudentNum() {
        return studentNum;
    }

    /**
     * 设置学号
     *
     * @param studentNum 学号
     */
    public void setStudentNum(String studentNum) {
        this.studentNum = studentNum == null ? null : studentNum.trim();
    }

    /**
     * 获取姓名
     *
     * @return STUDENT_NAME_ - 姓名
     */
    public String getStudentName() {
        return studentName;
    }

    /**
     * 设置姓名
     *
     * @param studentName 姓名
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName == null ? null : studentName.trim();
    }

    /**
     * 获取课程序号
     *
     * @return COURSE_NUM_ - 课程序号
     */
    public String getCourseNum() {
        return courseNum;
    }

    /**
     * 设置课程序号
     *
     * @param courseNum 课程序号
     */
    public void setCourseNum(String courseNum) {
        this.courseNum = courseNum == null ? null : courseNum.trim();
    }

    /**
     * 获取课程代码
     *
     * @return COURSE_CODE_ - 课程代码
     */
    public String getCourseCode() {
        return courseCode;
    }

    /**
     * 设置课程代码
     *
     * @param courseCode 课程代码
     */
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode == null ? null : courseCode.trim();
    }

    /**
     * 获取课程名称
     *
     * @return COURSE_NAME_ - 课程名称
     */
    public String getCourseName() {
        return courseName;
    }

    /**
     * 设置课程名称
     *
     * @param courseName 课程名称
     */
    public void setCourseName(String courseName) {
        this.courseName = courseName == null ? null : courseName.trim();
    }

    /**
     * 获取成绩性质
     *
     * @return SCORE_PROPERTY_ - 成绩性质
     */
    public String getScoreProperty() {
        return scoreProperty;
    }

    /**
     * 设置成绩性质
     *
     * @param scoreProperty 成绩性质
     */
    public void setScoreProperty(String scoreProperty) {
        this.scoreProperty = scoreProperty == null ? null : scoreProperty.trim();
    }

    /**
     * 获取成绩类型
     *
     * @return SCORE_TYPE_ - 成绩类型
     */
    public String getScoreType() {
        return scoreType;
    }

    /**
     * 设置成绩类型
     *
     * @param scoreType 成绩类型
     */
    public void setScoreType(String scoreType) {
        this.scoreType = scoreType == null ? null : scoreType.trim();
    }

    /**
     * 获取是否确认
     *
     * @return IS_CONFIRM_ - 是否确认
     */
    public String getIsConfirm() {
        return isConfirm;
    }

    /**
     * 设置是否确认
     *
     * @param isConfirm 是否确认
     */
    public void setIsConfirm(String isConfirm) {
        this.isConfirm = isConfirm == null ? null : isConfirm.trim();
    }

    /**
     * 获取成绩数值
     *
     * @return SCORE_VALUE_ - 成绩数值
     */
    public String getScoreValue() {
        return scoreValue;
    }

    /**
     * 设置成绩数值
     *
     * @param scoreValue 成绩数值
     */
    public void setScoreValue(String scoreValue) {
        this.scoreValue = scoreValue == null ? null : scoreValue.trim();
    }

    /**
     * 获取成绩
     *
     * @return SCORE_ - 成绩
     */
    public String getScore() {
        return score;
    }

    /**
     * 设置成绩
     *
     * @param score 成绩
     */
    public void setScore(String score) {
        this.score = score == null ? null : score.trim();
    }

    /**
     * 获取绩点
     *
     * @return GRADE_POINT_ - 绩点
     */
    public Double getGradePoint() {
        return gradePoint;
    }

    /**
     * 设置绩点
     *
     * @param gradePoint 绩点
     */
    public void setGradePoint(Double gradePoint) {
        this.gradePoint = gradePoint;
    }

    /**
     * 获取是否通过
     *
     * @return IS_PASS_ - 是否通过
     */
    public String getIsPass() {
        return isPass;
    }

    /**
     * 设置是否通过
     *
     * @param isPass 是否通过
     */
    public void setIsPass(String isPass) {
        this.isPass = isPass == null ? null : isPass.trim();
    }

    /**
     * 获取成绩性质名称
     *
     * @return SCORE_PROPERTY_NAME_ - 成绩性质名称
     */
    public String getScorePropertyName() {
        return scorePropertyName;
    }

    /**
     * 设置成绩性质名称
     *
     * @param scorePropertyName 成绩性质名称
     */
    public void setScorePropertyName(String scorePropertyName) {
        this.scorePropertyName = scorePropertyName == null ? null : scorePropertyName.trim();
    }

    /**
     * 获取课程类别
     *
     * @return COURSE_CATEGORY_ - 课程类别
     */
    public String getCourseCategory() {
        return courseCategory;
    }

    /**
     * 设置课程类别
     *
     * @param courseCategory 课程类别
     */
    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory == null ? null : courseCategory.trim();
    }

    /**
     * 获取课程性质
     *
     * @return COURSE_PROPERTY_ - 课程性质
     */
    public String getCourseProperty() {
        return courseProperty;
    }

    /**
     * 设置课程性质
     *
     * @param courseProperty 课程性质
     */
    public void setCourseProperty(String courseProperty) {
        this.courseProperty = courseProperty == null ? null : courseProperty.trim();
    }

    /**
     * 获取学分
     *
     * @return CREDIT_ - 学分
     */
    public Double getCredit() {
        return credit;
    }

    /**
     * 设置学分
     *
     * @param credit 学分
     */
    public void setCredit(Double credit) {
        this.credit = credit;
    }

    /**
     * 获取教师名称
     *
     * @return TEARCHER_NAME_ - 教师名称
     */
    public String getTearcherName() {
        return tearcherName;
    }

    /**
     * 设置教师名称
     *
     * @param tearcherName 教师名称
     */
    public void setTearcherName(String tearcherName) {
        this.tearcherName = tearcherName == null ? null : tearcherName.trim();
    }

    /**
     * 获取课程类型
     *
     * @return COURES_TYPE_ - 课程类型
     */
    public String getCouresType() {
        return couresType;
    }

    /**
     * 设置课程类型
     *
     * @param couresType 课程类型
     */
    public void setCouresType(String couresType) {
        this.couresType = couresType == null ? null : couresType.trim();
    }

    /**
     * 获取考试方式
     *
     * @return EXAM_WAY_ - 考试方式
     */
    public String getExamWay() {
        return examWay;
    }

    /**
     * 设置考试方式
     *
     * @param examWay 考试方式
     */
    public void setExamWay(String examWay) {
        this.examWay = examWay == null ? null : examWay.trim();
    }

    /**
     * 获取考试方式名称
     *
     * @return EXAM_WAY_NAME_ - 考试方式名称
     */
    public String getExamWayName() {
        return examWayName;
    }

    /**
     * 设置考试方式名称
     *
     * @param examWayName 考试方式名称
     */
    public void setExamWayName(String examWayName) {
        this.examWayName = examWayName == null ? null : examWayName.trim();
    }

    /**
     * 获取公选课类别
     *
     * @return PUBLIC_COURSES_TYPE_ - 公选课类别
     */
    public String getPublicCoursesType() {
        return publicCoursesType;
    }

    /**
     * 设置公选课类别
     *
     * @param publicCoursesType 公选课类别
     */
    public void setPublicCoursesType(String publicCoursesType) {
        this.publicCoursesType = publicCoursesType == null ? null : publicCoursesType.trim();
    }

    /**
     * 获取教师代码
     *
     * @return TEARCHER_CODE - 教师代码
     */
    public String getTearcherCode() {
        return tearcherCode;
    }

    /**
     * 设置教师代码
     *
     * @param tearcherCode 教师代码
     */
    public void setTearcherCode(String tearcherCode) {
        this.tearcherCode = tearcherCode == null ? null : tearcherCode.trim();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", academicYear=").append(academicYear);
        sb.append(", semester=").append(semester);
        sb.append(", studentNum=").append(studentNum);
        sb.append(", studentName=").append(studentName);
        sb.append(", courseNum=").append(courseNum);
        sb.append(", courseCode=").append(courseCode);
        sb.append(", courseName=").append(courseName);
        sb.append(", scoreProperty=").append(scoreProperty);
        sb.append(", scoreType=").append(scoreType);
        sb.append(", isConfirm=").append(isConfirm);
        sb.append(", scoreValue=").append(scoreValue);
        sb.append(", score=").append(score);
        sb.append(", gradePoint=").append(gradePoint);
        sb.append(", isPass=").append(isPass);
        sb.append(", scorePropertyName=").append(scorePropertyName);
        sb.append(", courseCategory=").append(courseCategory);
        sb.append(", courseProperty=").append(courseProperty);
        sb.append(", credit=").append(credit);
        sb.append(", tearcherName=").append(tearcherName);
        sb.append(", couresType=").append(couresType);
        sb.append(", examWay=").append(examWay);
        sb.append(", examWayName=").append(examWayName);
        sb.append(", publicCoursesType=").append(publicCoursesType);
        sb.append(", tearcherCode=").append(tearcherCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}