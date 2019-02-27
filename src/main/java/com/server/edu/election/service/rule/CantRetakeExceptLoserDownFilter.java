package com.server.edu.election.service.rule;

import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.service.rule.context.ElectState;
import com.server.edu.election.service.rule.filter.AbstractElectableLessonFilter;

/**
 * 不能重修，但是留降级学生除外
 *
 */
public class CantRetakeExceptLoserDownFilter
    extends AbstractElectableLessonFilter
{
    
    public static final String PARAM = "NORETAKE";
    
    public static final String PARAM1 = "LD";
    
    //	protected CourseGradePrepare courseGradePrepare;
    //	protected SemesterService semesterService;
    
    @Override
    public void prepare(ElectState electContext)
    {
        //		// 在ElectState中放入这个后，学生就不能选重修课了
        //		ElectState state = electContext.getState();
        //		state.getParams().put(PARAM, true);
        //		courseGradePrepare.run(electContext);
        //		Semester semester = electContext.getProfile().getSemester();
        //		// 判断是否为留降级学生
        //		/*OqlBuilder query = OqlBuilder.from(LoserDownStudent.class, "l");
        //		query.select("l.id")
        //		.where("l.std.id=:stdId", electContext.getStudent().getId())
        //		.where("l.semester.id=:semesterId or l.semester.id =:semesterId1", semester.getId(),semesterService.getPrevSemester(semester).getId());
        //		List res = entityDao.search(query);*/
        //		//学籍异动信息
        //		OqlBuilder<StdAlteration> builder = OqlBuilder.from(
        //				StdAlteration.class, "stdAlteration");
        //		builder.where("stdAlteration.effective = 1");
        //		builder.where("stdAlteration.type.name = '编级'");
        //		builder.where("stdAlteration.std.id = :stdId",electContext.getStudent().getId());
        //		builder.where("stdAlteration.semester.id=:semesterId or stdAlteration.semester.id =:semesterId1", semester.getId(),semesterService.getPrevSemester(semester).getId());
        //		List res = entityDao.search(builder);
        //		
        //		electContext.getState().getParams().put(PARAM1, CollectionUtils.isNotEmpty(res));
        //		
        //		// add by qj，在不允许重修（也就是新选课）的轮次，上个学期新选的但成绩没有出来的课程，本轮次是不允许出现的
        //		// 为了获得这个效果，暂时设置这些课程为不通过
        //		Semester prevSemester = semesterService.getPrevSemester(semester);
        //		if(prevSemester != null) {
        //			OqlBuilder takedNoGradeQuery = OqlBuilder
        //					.from(CourseTake.class, "take")
        //					.select("take.lesson.course.id")
        //					.where("take.lesson.semester.id=:semesterId", prevSemester.getId())
        //					.where("take.std.id=:stdId", electContext.getStudent().getId())//没有判断学生
        //					.where("not exists(select grade.id from " + CourseGrade.class.getName() + " grade" +
        //							" where grade.course.id=take.lesson.course.id and grade.semester.id=take.lesson.semester.id" +
        //							" and grade.std.id=take.std.id and grade.status = :published)",Grade.Status.PUBLISHED
        //							).cacheable();
        //			List<Long> ids = entityDao.search(takedNoGradeQuery);
        //			for(Long id : ids) {
        //				if(null == state.getHisCourses().get(id)) {
        //					state.getHisCourses().put(id, true);//这里是否需要考虑留降级预警学生，直接设置false针对普通学生是适用的
        //				}
        //			}
        //		}
    }
    
    @Override
    public boolean isElectable(TeachingClass lesson, ElectState state)
    {
        // result = (Boolean)state.getParams().get(PARAM1) || !state.isRetakeCourse(lesson.getCourse().getId());
        //      if (!result) {
        //      context.addMessage(new ElectMessage("本轮次不对非留降级学生开放重修课", ElectRuleType.ELECTION, false, context.getLesson()));
        //      }
        //return result
        return false;
    }
    
}