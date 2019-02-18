package com.server.edu.election.service.rule.filter;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.ElectState;

/**
 * 限制不能选择重修课
 *
 */
public class ElectableLessonNoRetakeFilter extends AbstractElectableLessonFilter
{
    
    public static final String PARAM = "NORETAKE";
    
    //    protected CourseGradePrepare courseGradePrepare;
    //    
    //    protected SemesterService semesterService;
    
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        // return !state.isRetakeCourse(lesson.getCourse().getId());
        //        if (!result)
        //        {
        //            context.addMessage(new ElectMessage("本轮次不开放重修课",
        //                ElectRuleType.ELECTION, false, context.getLesson()));
        //        }
        return false;
    }
    
    @Override
    public void prepare(ElectState electContext)
    {
        //        // 在ElectState中放入这个后，学生就不能选重修课了
        //        ElectState state = electContext.getState();
        //        state.getParams().put(PARAM, true);
        //        courseGradePrepare.run(electContext);
        //        
        //        // add by qj，在不允许重修（也就是新选课）的轮次，上个学期新选的但成绩没有出来的课程，本轮次是不允许出现的
        //        // 为了获得这个效果，暂时设置这些课程为不通过
        //        Semester prevSemester = semesterService
        //            .getPrevSemester(electContext.getProfile().getSemester());
        //        if (prevSemester != null)
        //        {
        //            OqlBuilder takedNoGradeQuery = OqlBuilder
        //                .from(CourseTake.class, "take")
        //                .select("take.lesson.course.id")
        //                .where("take.lesson.semester.id=:semesterId",
        //                    prevSemester.getId())
        //                .where("take.std.id=:stdId", state.getStd().getId())
        //                .where("not exists(select grade.id from "
        //                    + CourseGrade.class.getName() + " grade"
        //                    + " where grade.course.id=take.lesson.course.id and grade.semester.id=take.lesson.semester.id"
        //                    + " and grade.std.id=take.std.id and grade.status = :published)",
        //                    Grade.Status.PUBLISHED)
        //                .cacheable();
        //            List<Long> ids = entityDao.search(takedNoGradeQuery);
        //            for (Long id : ids)
        //            {
        //                if (null == state.getHisCourses().get(id))
        //                {
        //                    state.getHisCourses().put(id, false);
        //                }
        //            }
        //        }
    }
    
}