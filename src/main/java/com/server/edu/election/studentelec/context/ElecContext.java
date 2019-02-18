package com.server.edu.election.studentelec.context;

import com.server.edu.election.studentelec.cache.StudentInfoCache;

import java.util.List;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public class ElecContext {
    /** 个人信息 */
    private StudentInfoCache studentInfo;

    /** 已完成课程 */
    private List<CompletedCourse> completedCourses;

    /** 已选择课程 */
    private List<SelectedCourse> selectedCourses;

    /** 免修申请课程 */
    private List<ElecCourse> applyForDropCourses;

    /** 个人计划内课程 */
    private List<ElecCourse> planCourses;

    private ElecRequest request;

    private ElecRespose respose;

    public StudentInfoCache getStudentInfo() {
        return studentInfo;
    }

    public void setStudentInfo(StudentInfoCache studentInfo) {
        this.studentInfo = studentInfo;
    }

    public List<CompletedCourse> getCompletedCourses() {
        return completedCourses;
    }

    public void setCompletedCourses(List<CompletedCourse> completedCourses) {
        this.completedCourses = completedCourses;
    }

    public List<SelectedCourse> getSelectedCourses() {
        return selectedCourses;
    }

    public void setSelectedCourses(List<SelectedCourse> selectedCourses) {
        this.selectedCourses = selectedCourses;
    }

    public List<ElecCourse> getApplyForDropCourses() {
        return applyForDropCourses;
    }

    public void setApplyForDropCourses(List<ElecCourse> applyForDropCourses) {
        this.applyForDropCourses = applyForDropCourses;
    }

    public List<ElecCourse> getPlanCourses() {
        return planCourses;
    }

    public void setPlanCourses(List<ElecCourse> planCourses) {
        this.planCourses = planCourses;
    }

    public ElecRequest getRequest() {
        return request;
    }

    public void setRequest(ElecRequest request) {
        this.request = request;
    }

    public ElecRespose getRespose() {
        return respose;
    }

    public void setRespose(ElecRespose respose) {
        this.respose = respose;
    }
}
