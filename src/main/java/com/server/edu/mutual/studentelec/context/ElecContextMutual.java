package com.server.edu.mutual.studentelec.context;

import java.util.List;
import java.util.Set;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.IElecContext;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.vo.ElcCouSubsVo;
import com.server.edu.mutual.vo.SelectedCourse;

/**
 * 执行“学生选课请求”时的上下文环境，组装成本对象，供各种约束调用
 *
 */
public class ElecContextMutual implements IElecContext
{
	/**选课申请课程*/
    public static final String ELEC_APPLY_COURSES = "elecApplyCourses";
    
    /**学期*/
    private Long calendarId;
    
    /** 个人信息 */
    private StudentInfoCache studentInfo;
    
    /** 已完成通過课程 */
    private Set<CompletedCourse> completedCourses;
    
    /** 本学期已选择课程(学生选课加互选课程) */
    private Set<SelectedCourse> selectedCourses;
    
    /** 免修申请课程 */
    private Set<ElecCourse> applyForDropCourses;
    
    /**课程组学分限制*/
    private Set<CourseGroup> courseGroups;
    
    /** 个人计划内课程 */
    private Set<PlanCourse> planCourses;
    
    /** 个人替代课程 */
    private Set<ElcCouSubsVo> replaceCourses;
    
    /** 通识选修课程 */
    private Set<ElecCourse> publicCourses;
    
    /**未通過課程*/
    private Set<CompletedCourse> failedCourse;
    
    /** 未修完的课程 */
    private Set<CompletedCourse>  unFinishedCourses;
    
    /**所有申请课程*/
    private Set<String> applyCourse;
    
    /**学生的选课申请课程*/
    private Set<ElectionApply> elecApplyCourses;
    
    /** 本学期已选择互选课程  */
    private Set<SelectedCourse> selectedMutualCourses;
    
    /** 未选择的互选课程  */
    private Set<SelectedCourse> unSelectedMutualCourses;
    
    /** 本科生可选的互选课程  */
    private List<SelectedCourse> optionalCourses;
    
    private ElecRequest request;
    
    private ElecRespose respose;
    
    private ElecContextUtil contextUtil;
    
    public ElecContextMutual(String studentId, Long calendarId,
        ElecRequest elecRequest)
    {
        this(studentId, calendarId);
        this.request = elecRequest;
    }
    
    public ElecContextMutual()
    {
        super();
    }
    
    public ElecContextMutual(String studentId, Long calendarId)
    {
        this.calendarId = calendarId;
        this.contextUtil = ElecContextUtil.create(studentId, this.calendarId);
        
        studentInfo = contextUtil.getStudentInfo();
        respose = this.contextUtil.getElecRespose();
        completedCourses =
            this.contextUtil.getSet("CompletedCourses", CompletedCourse.class);
        selectedCourses =
            this.contextUtil.getSet(SELECTED_COURSES, SelectedCourse.class);
        applyForDropCourses =
            this.contextUtil.getSet(APPLY_FOR_DROP_COURSES, ElecCourse.class);
        planCourses = this.contextUtil.getSet("PlanCourses", PlanCourse.class);
        publicCourses =
            this.contextUtil.getSet("publicCourses", ElecCourse.class);
        courseGroups =
            this.contextUtil.getSet("courseGroups", CourseGroup.class);
        failedCourse =
            this.contextUtil.getSet("failedCourse", CompletedCourse.class);
        applyCourse = ElecContextUtil.getApplyCourse(calendarId);
        elecApplyCourses =this.contextUtil.getSet(ELEC_APPLY_COURSES, ElectionApply.class);
        replaceCourses = this.contextUtil.getSet(REPLACE_COURSES, ElcCouSubsVo.class);
        selectedMutualCourses = this.contextUtil.getSet("selectedMutualCourses", SelectedCourse.class);
        unSelectedMutualCourses = this.contextUtil.getSet("unSelectedMutualCourses", SelectedCourse.class);
        optionalCourses = this.contextUtil.getList("optionalCourses", SelectedCourse.class);
        unFinishedCourses = this.contextUtil.getSet("unFinishedCourses", CompletedCourse.class);
    }
    
    /**
     * 保存到redis中
     * 
     */
    
    @Override
    public void saveToCache()
    {
        this.contextUtil.updateMem(StudentInfoCache.class.getSimpleName(),
            this.studentInfo);
        this.respose.setStatus(null);
        this.contextUtil.updateMem(ElecRespose.class.getSimpleName(),
            this.respose);
        this.contextUtil.updateMem("CompletedCourses", this.completedCourses);
        this.contextUtil.updateMem(SELECTED_COURSES, this.selectedCourses);
        this.contextUtil.updateMem(APPLY_FOR_DROP_COURSES,
            this.applyForDropCourses);
        this.contextUtil.updateMem("PlanCourses", this.planCourses);
        this.contextUtil.updateMem("courseGroups", this.courseGroups);
        this.contextUtil.updateMem("publicCourses", this.publicCourses);
        this.contextUtil.updateMem("failedCourse", this.failedCourse);
        this.contextUtil.updateMem("elecApplyCourses", this.elecApplyCourses);
        this.contextUtil.updateMem(REPLACE_COURSES, this.replaceCourses);
        this.contextUtil.updateMem("selectedMutualCourses", this.selectedMutualCourses);
        this.contextUtil.updateMem("unSelectedMutualCourses", this.unSelectedMutualCourses);
        this.contextUtil.updateMem("optionalCourses", this.optionalCourses);
        this.contextUtil.updateMem("unFinishedCourses",this.unFinishedCourses);
        // 保存所有到redis
        this.contextUtil.saveAll();
    }
    
    @Override
    public void saveResponse()
    {
        this.respose.setStatus(null);
        this.contextUtil.saveOne(ElecRespose.class.getSimpleName(),
            this.respose);
    }
    
    /**
     * 清空CompletedCourses,SelectedCourses,ApplyForDropCourses,PlanCourses,courseGroups,publicCourses,failedCourse
     * 
     */
    
    @Override
    public void clear()
    {
        this.getCompletedCourses().clear();
        this.getSelectedCourses().clear();
        this.getApplyForDropCourses().clear();
        this.getPlanCourses().clear();
        this.getCourseGroups().clear();
        this.getPublicCourses().clear();
        this.getFailedCourse().clear();
        this.getRespose().getFailedReasons().clear();
        this.getRespose().getSuccessCourses().clear();
        this.getApplyCourse().clear();
        this.getElecApplyCourses().clear();
        this.getReplaceCourses().clear();
        this.getSelectedMutualCourses().clear();
        this.getUnSelectedMutualCourses().clear();
        this.getOptionalCourses().clear();
        this.getUnFinishedCourses().clear();
    }
    
    public void courseClear()
    {
    	this.getSelectedCourses().clear();
    }
    
    @Override
    public Long getCalendarId()
    {
        return calendarId;
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
    
    public Set<CourseGroup> getCourseGroups()
    {
        return courseGroups;
    }
    
    public Set<CompletedCourse> getFailedCourse()
    {
        return failedCourse;
    }
    
    public ElecRequest getRequest()
    {
        return request;
    }
    
    @Override
    public void setRequest(ElecRequest request)
    {
        this.request = request;
    }
    
    @Override
    public ElecRespose getRespose()
    {
        return respose;
    }
    
    public void setRespose(ElecRespose respose)
    {
        this.respose = respose;
    }
    
    public void setSelectedCourses(Set<SelectedCourse> selectedCourses)
    {
        this.selectedCourses = selectedCourses;
    }
    
    public Set<String> getApplyCourse()
    {
        return applyCourse;
    }
    
    public Set<ElectionApply> getElecApplyCourses()
    {
        return elecApplyCourses;
    }
    
    public Set<ElcCouSubsVo> getReplaceCourses()
    {
        return replaceCourses;
    }

	public Set<SelectedCourse> getSelectedMutualCourses() {
		return selectedMutualCourses;
	}

	public void setSelectedMutualCourses(Set<SelectedCourse> selectedMutualCourses) {
		this.selectedMutualCourses = selectedMutualCourses;
	}

	public Set<CompletedCourse> getUnFinishedCourses() {
		return unFinishedCourses;
	}

	public void setUnFinishedCourses(Set<CompletedCourse> unFinishedCourses) {
		this.unFinishedCourses = unFinishedCourses;
	}

	public Set<SelectedCourse> getUnSelectedMutualCourses() {
		return unSelectedMutualCourses;
	}

	public void setUnSelectedMutualCourses(Set<SelectedCourse> unSelectedMutualCourses) {
		this.unSelectedMutualCourses = unSelectedMutualCourses;
	}

	public List<SelectedCourse> getOptionalCourses() {
		return optionalCourses;
	}

	public void setOptionalCourses(List<SelectedCourse> optionalCourses) {
		this.optionalCourses = optionalCourses;
	}
    
}
