package com.server.edu.election.studentelec.context;

import java.util.List;

import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.utils.ElecContextUtil;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public class ElecContext
{
    /**轮次*/
    private Long roundId;
    
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
    
    private ElecContextUtil contextUtil;
    
    public ElecContext(String studentId, Long roundId)
    {
        this.roundId = roundId;
        this.contextUtil = ElecContextUtil.create(roundId, studentId);
        
        studentInfo = contextUtil.getStudentInfo();
        respose = this.contextUtil.getElecRespose();
        completedCourses =
            this.contextUtil.getList("CompletedCourses", CompletedCourse.class);
        selectedCourses =
            this.contextUtil.getList("SelectedCourses", SelectedCourse.class);
        applyForDropCourses =
            this.contextUtil.getList("ApplyForDropCourses", ElecCourse.class);
        planCourses = this.contextUtil.getList("PlanCourses", ElecCourse.class);
    }
    
    /**
     * 保存到redis中
     * 
     */
    public void saveToCache()
    {
        this.contextUtil.save(StudentInfoCache.class.getSimpleName(),
            this.studentInfo);
        this.respose.setStatus(null);
        this.contextUtil.save(ElecRespose.class.getSimpleName(), this.respose);
        this.contextUtil.save("CompletedCourses", this.completedCourses);
        this.contextUtil.save("SelectedCourses", this.selectedCourses);
        this.contextUtil.save("ApplyForDropCourses", this.applyForDropCourses);
        this.contextUtil.save("PlanCourses", this.planCourses);
    }
    
    public StudentInfoCache getStudentInfo()
    {
        return studentInfo;
    }
    
    public Long getRoundId()
    {
        return roundId;
    }
    
    public List<CompletedCourse> getCompletedCourses()
    {
        return completedCourses;
    }
    
    public List<SelectedCourse> getSelectedCourses()
    {
        return selectedCourses;
    }
    
    public List<ElecCourse> getApplyForDropCourses()
    {
        return applyForDropCourses;
    }
    
    public List<ElecCourse> getPlanCourses()
    {
        return planCourses;
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
    
}
