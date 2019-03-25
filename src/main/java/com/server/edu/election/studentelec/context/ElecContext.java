package com.server.edu.election.studentelec.context;

import java.util.Set;

import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.utils.ElecContextUtil;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public class ElecContext
{
    /**学期*/
    private Long calendarId;
    
    /** 个人信息 */
    private StudentInfoCache studentInfo;
    
    /** 已完成通過课程 */
    private Set<CompletedCourse> completedCourses;
    
    /** 本学期已选择课程 */
    private Set<SelectedCourse> selectedCourses;
    
    /** 免修申请课程 */
    private Set<ElecCourse> applyForDropCourses;
    
    /**课程组学分限制*/
    private Set<CourseGroup> courseGroups;
    
    /** 个人计划内课程 */
    private Set<PlanCourse> planCourses;
    
    /** 通识选修课程 */
    private Set<ElecCourse> publicCourses;
    
    private ElecRequest request;
    
    private ElecRespose respose;
    
    private ElecContextUtil contextUtil;

    /**未通過課程*/
    private Set<CompletedCourse> failedCourse;

    
    public ElecContext(String studentId, Long calendarId)
    {
        this.calendarId = calendarId;
        this.contextUtil = ElecContextUtil.create(studentId, this.calendarId);
        
        studentInfo = contextUtil.getStudentInfo();
        respose = this.contextUtil.getElecRespose();
        completedCourses =
            this.contextUtil.getSet("CompletedCourses", CompletedCourse.class);
        selectedCourses =
            this.contextUtil.getSet("SelectedCourses", SelectedCourse.class);
        applyForDropCourses =
            this.contextUtil.getSet("ApplyForDropCourses", ElecCourse.class);
        planCourses = this.contextUtil.getSet("PlanCourses", PlanCourse.class);
        publicCourses =
            this.contextUtil.getSet("publicCourses", ElecCourse.class);
        courseGroups =
            this.contextUtil.getSet("courseGroups", CourseGroup.class);
            this.contextUtil.getSet("failedCourse", CompletedCourse.class);
    }
    
    public ElecContext(String studentId, Long calendarId,
        ElecRequest elecRequest)
    {
        this(studentId, calendarId);
        this.request = elecRequest;
    }
    
    /**
     * 保存到redis中
     * 
     */
    public void saveToCache()
    {
        this.contextUtil.save(StudentInfoCache.class.getSimpleName(),
            this.studentInfo);
        this.saveResponse();
        this.contextUtil.save("CompletedCourses", this.completedCourses);
        this.contextUtil.save("SelectedCourses", this.selectedCourses);
        this.contextUtil.save("ApplyForDropCourses", this.applyForDropCourses);
        this.contextUtil.save("PlanCourses", this.planCourses);
        this.contextUtil.save("courseGroups", this.courseGroups);
        this.contextUtil.save("publicCourses", this.publicCourses);
        this.contextUtil.save("failedCourse", this.failedCourse);
    }
    
    public void saveResponse()
    {
        this.respose.setStatus(null);
        this.contextUtil.save(ElecRespose.class.getSimpleName(), this.respose);
    }
    
    public StudentInfoCache getStudentInfo()
    {
        return studentInfo;
    }
    
    public Set<CompletedCourse> getCompletedCourses()
    {
        return completedCourses;
    }
    
    public Set<SelectedCourse> getSelectedCourses()
    {
        return selectedCourses;
    }
    
    public Set<ElecCourse> getApplyForDropCourses()
    {
        return applyForDropCourses;
    }
    
    public Set<PlanCourse> getPlanCourses()
    {
        return planCourses;
    }
    
    public Set<ElecCourse> getPublicCourses()
    {
        return publicCourses;
    }
    
    public ElecRequest getRequest()
    {
        return request;
    }
    
    public void setRequest(ElecRequest request)
    {
        this.request = request;
    }
    
    public ElecRespose getRespose()
    {
        return respose;
    }
    
    public void setRespose(ElecRespose respose)
    {
        this.respose = respose;
    }
    
    public Set<CourseGroup> getCourseGroups()
    {
        return courseGroups;
    }
    
    public void setCourseGroups(Set<CourseGroup> courseGroups)
    {
        this.courseGroups = courseGroups;
    }

    public Set<CompletedCourse> getFailedCourse() {
        return failedCourse;
    }

    public void setFailedCourse(Set<CompletedCourse> failedCourse) {
        this.failedCourse = failedCourse;
    }
}
