package com.server.edu.election.service.rule.context;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.server.edu.common.rest.StudentInfo;
import com.server.edu.election.dto.LessonDto;
import com.server.edu.election.entity.TeachingClass;

/**
 * 学生选课的状态数据
 * 
 */
public class ElectState implements Serializable
{
    
    private static final long serialVersionUID = 1L;
    
    /** 学生选课用的简要信息 */
    private StudentInfo std;
    
    private Long roundId;
    
    private Long semesterId;
    
    /** 替代关系 */
    //private List<ElectCourseSubstitution> courseSubstitutions = new ArrayList<>();
    
    /** 已选的课程 */
    private final List<LessonDto> electedCourses = new ArrayList<>();
    
    /** profile对应的可选任务Id */
    //private final List<Long> electableLessonIds = new ArrayList<>();
    
    /** 时间占用表 */
    //private TimeUnit table;
    
    /** 建议必修课 */
    private Set<Long> compulsoryCourseIds = new HashSet<>();
    
    private final Map<String, Object> params = new HashMap<>();
    
    private Set<Long> unElectableLessonIds = new HashSet<>();
    
    /** 不能退课的lesson.id */
    private Map<Long, String> unWithdrawableLessonIds = new HashMap<>();
    
    //private ElectConstraintWrapper<Float> creditConstraint; // 学分约束
    
    //private ElectConstraintWrapper<Float> totalCreditConstraint;// 总学分约束
    
    //private ElectConstraintWrapper<Integer> courseCountConstraint;// 课程数约束
    
    //private Map<CourseType, ElectConstraintWrapper<Integer>> courseTypeCourseCountConstraints = new HashMap<>();// 课程类型数量约束
    
    //private Set<Course> unlimitCourses = new HashSet<>();//无限制课程
    /**
     * 选课消息
     */
    private List<ElectMessage> electMessages = new ArrayList<>();
    
    public ElectState()
    {
        super();
    }
    
    public void electSuccess(TeachingClass lesson)
    {
        //		ElectCourseGroup group = coursePlan.getOrCreateGroup(lesson.getCourse(), lesson.getCourseType());
        //		group.addElectCourse(lesson.getCourse());
        //		electedCourseIds.put(lesson.getCourse().getId(), lesson.getId());
        //		if (null != creditConstraint) {
        //			creditConstraint.addElectedItem(lesson.getCourse().getCredits());
        //		}
        //		if (null != totalCreditConstraint) {
        //			totalCreditConstraint.addElectedItem(lesson.getCourse().getCredits());
        //		}
        //		if (null != courseCountConstraint) {
        //			courseCountConstraint.addElectedItem(1);
        //		}
        //		if (null != courseTypeCourseCountConstraints.get(lesson.getCourse().getCourseType())) {
        //			courseTypeCourseCountConstraints.get(lesson.getCourse().getCourseType()).addElectedItem(1);
        //		} else if (null != courseTypeCourseCountConstraints.get(lesson.getCourseType())) {
        //			courseTypeCourseCountConstraints.get(lesson.getCourseType()).addElectedItem(1);
        //		}
    }
    
    public void withdrawSuccess(TeachingClass lesson)
    {
        //		ElectCourseGroup group = coursePlan.getGroup(lesson.getCourse(), lesson.getCourseType());
        //		group.removeElectCourse(lesson.getCourse());
        //		electedCourseIds.remove(lesson.getCourse().getId());
        //		if (null != creditConstraint) {
        //			creditConstraint.subElectedItem(lesson.getCourse().getCredits());
        //		}
        //		if (null != totalCreditConstraint) {
        //			totalCreditConstraint.subElectedItem(lesson.getCourse().getCredits());
        //		}
        //		if (null != courseCountConstraint) {
        //			courseCountConstraint.subElectedItem(1);
        //		}
        //		if (null != courseTypeCourseCountConstraints.get(lesson.getCourse().getCourseType())) {
        //			courseTypeCourseCountConstraints.get(lesson.getCourse().getCourseType()).subElectedItem(1);
        //		} else if (null != courseTypeCourseCountConstraints.get(lesson.getCourseType())) {
        //			courseTypeCourseCountConstraints.get(lesson.getCourseType()).subElectedItem(1);
        //		}
    }
    
    public static ElectState createState(
        StudentInfo std/*, ElectionProfile profile, EntityDao entityDao*/)
    {
        //		Iterator<StdCreditConstraint> it2 = entityDao.get(StdCreditConstraint.class,
        //		        new String[] { "semester", "std" }, profile.getSemester(), std).iterator();
        //		StdCreditConstraint constraint = it2.hasNext() ? it2.next() : null;
        //		OqlBuilder<StudentProgram> query = OqlBuilder.from(StudentProgram.class, "sp");
        //		query.where("sp.std=:std", std);
        //		StudentProgram stdProgram = entityDao.uniqueResult(query);
        ElectState state =
            new ElectState(std/*, stdProgram, profile, constraint*/);
        return state;
    }
    
    public ElectState(StudentInfo student/*, StudentProgram stdProgram, ElectionProfile profile,
                                         StdCreditConstraint constraint*/)
    {
        std = student;
        //		this.profileId = profile.getId();
        //		this.semesterId = profile.getSemester().getId();
    }
    
    public Set<String> getUnPassedCourseIds()
    {
        Set<String> unPassedCourseIds = new HashSet<>();
        if (null != electedCourses)
        {
            for (LessonDto lesson : electedCourses)
            {
                if (!lesson.isHisCoursePassed() && lesson.isHisFlag())
                {
                    unPassedCourseIds.add(lesson.getCourseCode());
                }
            }
        }
        return unPassedCourseIds;
    }
    
    public Set<String> getPassedCourseIds()
    {
        Set<String> passedCourseIds = new HashSet<>();
        if (null != electedCourses)
        {
            for (LessonDto lesson : electedCourses)
            {
                if (lesson.isHisCoursePassed() && lesson.isHisFlag())
                {
                    passedCourseIds.add(lesson.getCourseCode());
                }
            }
        }
        return passedCourseIds;
    }
    
    public boolean isRetakeCourse(String courseCode)
    {
        for (LessonDto lesson : electedCourses)
        {
            if (lesson.getCourseCode().equals(courseCode))
            {
                return true;
            }
        }
        //		for (ElectCourseSubstitution courseSubstitution : courseSubstitutions) {
        //			if (courseSubstitution.getSubstitutes().contains(courseId)) {
        //				for (Long originCourseId : courseSubstitution.getOrigins()) {
        //					if (hisCourses.containsKey(originCourseId)) { return true; }
        //				}
        //			}
        //		}
        return false;
    }
    
    public Long getOriginCourseId(long courseId)
    {
        //		for (ElectCourseSubstitution courseSubstitution : courseSubstitutions) {
        //			if (courseSubstitution.getSubstitutes().contains(courseId)) {
        //				for (Long originCourseId : courseSubstitution.getOrigins()) {
        //					if (hisCourses.containsKey(originCourseId)) { return originCourseId; }
        //				}
        //			}
        //		}
        return courseId;
    }
    
    public StudentInfo getStd()
    {
        return std;
    }
    
//    public TimeUnit getTable()
//    {
//        return table;
//    }
    
    public Set<Long> getCompulsoryCourseIds()
    {
        return compulsoryCourseIds;
    }
    
    public boolean isCoursePass(String courseId)
    {
        for (LessonDto lesson : electedCourses)
        {
            if (lesson.getCourseCode().equals(courseId)
                && lesson.isHisCoursePassed() && lesson.isHisFlag())
            {
                return true;
            }
        }
        return false;
    }
    
//    public void setTable(TimeUnit table)
//    {
//        this.table = table;
//    }
    
    public void setCompulsoryCourseIds(Set<Long> compulsoryCourseIds)
    {
        this.compulsoryCourseIds = compulsoryCourseIds;
    }
    
    public Map<String, Object> getParams()
    {
        return params;
    }
    
    public Set<Long> getUnElectableLessonIds()
    {
        return unElectableLessonIds;
    }
    
    public Map<Long, String> getUnWithdrawableLessonIds()
    {
        return unWithdrawableLessonIds;
    }
    
    public List<LessonDto> getElectedCourses()
    {
        return electedCourses;
    }
    
//    public List<Long> getElectableLessonIds()
//    {
//        return electableLessonIds;
//    }
    
    public Long getRoundId()
    {
        return roundId;
    }
    
    public Long getSemesterId()
    {
        return semesterId;
    }
    
    public List<ElectMessage> getElectMessages()
    {
        return electMessages;
    }
    
    public void setElectMessages(List<ElectMessage> electMessages)
    {
        this.electMessages = electMessages;
    }
    
}