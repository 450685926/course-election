package com.server.edu.election.studentelec.rules.bk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 时间冲突检查
 * 实施中可以设置三个参数，顺序不能混淆
 * 1、检查的冲突节次数量 取值必须是正整数,如果小于零系统会认为是0
 * 2、检查类型  用来设置是检查冲突节次还是检查空闲节次 取值为 YES、Y、 T、TRUE、真、是
 * 3、是否连续 表示检查的时候小节按照连续还是不连续来进行检查
 */
@Component("TimeConflictCheckerRule")
public class TimeConflictCheckerRule extends AbstractRuleExceutor
{
    
    public static final Boolean CHECK_CONFLICT = true;
    
    public static final Boolean CHECK_UN_CONFLICT = false;
    
    @Override
    @SuppressWarnings("unchecked")
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        //		ElectionCourseContext electContext = (ElectionCourseContext) context;
        //		Lesson electLesson = electContext.getLesson();
        //		
        //		int unitCount = ((Integer) electContext.getState().getParams().get("MAX_TIME_CONFLICT_COUNT")).intValue();
        //		if(unitCount<0) unitCount = 0;
        //		boolean checkType = ((Boolean) electContext.getState().getParams().get("TIME_CONFLICT_CHECK_TYPE")).booleanValue();
        //		boolean isContinuous = ((Boolean) electContext.getState().getParams().get("TIME_CONFLICT_IS_CONTINUOUS")).booleanValue();
        //		
        //		Collection<Lesson> electedLessons = (Collection<Lesson>) electContext.getParams().get(Params.CONFLICT_COURSE_TAKES.toString());
        //		Collection<Lesson> electingLessons = (Collection<Lesson>) electContext.getParams().get(Params.CONFLICT_LESSONS.toString());
        //		List<Lesson> conflictCourseTake = getConflictLessons(electLesson,electedLessons,unitCount,checkType,isContinuous);
        //		List<Lesson> conflictLessons = getConflictLessons(electLesson,electingLessons,unitCount,checkType,isContinuous);
        //		
        //		if (!conflictLessons.isEmpty() || !conflictCourseTake.isEmpty()) {
        //			StringBuilder builder = new StringBuilder("与以下课程冲突 :");
        //			for (Lesson lesson : conflictLessons) {
        //				builder.append("<dd>").append(lesson.getCourse().getName()).append("[").append(lesson.getNo()).append("]").append("</dd>");
        //			}
        //			for (Lesson lesson : conflictCourseTake) {
        //				builder.append("<dd>").append(lesson.getCourse().getName()).append("[").append(lesson.getNo()).append("]").append("</dd>");
        //			}
        //			electContext.addMessage(new ElectMessage(builder.toString(),ElectRuleType.ELECTION,false,electContext.getLesson()));
        //			return false;
        //		}
        return true;
    }
    
    public boolean isConflict(TeachingClass lesson, TeachingClass lesson2,
        int unitCount, boolean checkType, boolean isContinuous)
    {
        //		if (null == lesson || null == lesson2 || lesson.equals(lesson2)) //|| lesson.getCourse().getCode().equals(lesson2.getCourse().getCode()))
        //			return false;
        //		Set<CourseActivity> activities = lesson.getCourseSchedule().getActivities();
        //		Set<CourseActivity> activities2 = lesson2.getCourseSchedule().getActivities();
        //		if(activities.isEmpty() || activities2.isEmpty()){
        //			return false;
        //		}
        //		@SuppressWarnings("unused")
        //		int allUnitCount=0; //总节次
        //		int conflictCount = 0;//冲突节次
        //		int maxConflict=0;//最大连续冲突节次
        //		int maxUnConflict=0;//最大连续空闲节次
        //		for (CourseActivity courseActivity : activities) {
        //			CourseTime time = courseActivity.getTime();
        //			final int timeOneDayUnit= time.getEndUnit() - time.getStartUnit() + 1;
        //			int oneDayUnConfictCount = timeOneDayUnit;
        //			allUnitCount+=timeOneDayUnit;
        //			
        //			for (CourseActivity courseActivity2 : activities2) {
        //				CourseTime time2 = courseActivity2.getTime();
        //				//同周同一天有课程
        //				if ((time.getWeekStateNum() & time2.getWeekStateNum()) > 0 && time.getWeekday().equals(time2.getWeekday())) {
        //					//节次有冲突
        //					if(time.getStartUnit() <= time2.getEndUnit() && time.getEndUnit() >= time2.getStartUnit()){
        //						oneDayUnConfictCount = 0;
        //						if(unitCount<2 && checkType){
        //							return true;//禁止任何冲突
        //						}else {
        //							//计算出冲突和空闲的节次数量
        //							int minStart = Math.min(time.getStartUnit(), time2.getStartUnit());
        //							int maxStart = Math.max(time.getStartUnit(), time2.getStartUnit());
        //							int minEnd = Math.min(time.getEndUnit(), time2.getEndUnit());
        //							int maxEnd = Math.max(time.getEndUnit(), time2.getEndUnit());
        //							int oneDayConflictCount  = (minEnd - maxStart+1);
        //							conflictCount+=oneDayConflictCount;
        //							if(oneDayConflictCount>maxConflict) {
        //								maxConflict = oneDayConflictCount;
        //							}
        //							if(minStart==time.getStartUnit()){
        //								int leftOneDayUnConflictCount = (maxStart - minStart);
        //								if(!isContinuous || leftOneDayUnConflictCount>1){
        //									oneDayUnConfictCount += leftOneDayUnConflictCount;									
        //								}
        //							}
        //							if(maxEnd==time.getEndUnit()){
        //								int rightOneDayUnConflictCount = (maxEnd - minEnd);
        //								if(!isContinuous || rightOneDayUnConflictCount>1){
        //									oneDayUnConfictCount += rightOneDayUnConflictCount;
        //								}
        //							}
        //							if(oneDayUnConfictCount>maxUnConflict) {
        //								maxUnConflict =oneDayUnConfictCount;
        //							}
        //						}
        //					}
        //				}
        //			}
        //			if(oneDayUnConfictCount>maxUnConflict) {
        //				maxUnConflict =oneDayUnConfictCount;
        //			}
        //		}
        //		//检查冲突
        //		if(checkType){
        //			//冲突节次如果大于等于指定数量则冲突
        //			if(conflictCount >=unitCount && conflictCount>0){
        //				return true;
        //			}
        //		}else {
        //			//空闲节次如果大于等于指定数量则不冲突
        //			if(maxUnConflict>=unitCount && maxUnConflict>0){
        //				return !(unitCount>0);
        //			}else{
        //				return true;
        //			}
        //		}
        return false;
    }
    
    public List<TeachingClass> getConflictLessons(TeachingClass lesson,
        Collection<TeachingClass> lessons, int conflictTimeCount,
        boolean checkType, boolean isContinuous)
    {
        Set<TeachingClass> temp = new HashSet<>();
        List<TeachingClass> result = new ArrayList<>();
        if (null != lessons)
        {
            for (TeachingClass lesson2 : lessons)
            {
                if (temp.contains(lesson2))
                {
                    continue;
                }
                if (isConflict(lesson,
                    lesson2,
                    conflictTimeCount,
                    checkType,
                    isContinuous))
                {
                    result.add(lesson2);
                }
                temp.add(lesson2);
            }
        }
        return result;
    }
    
    public List<TeachingClass> getConflictLessonsWithCourseTakes(
        TeachingClass lesson, Collection<ElcCourseTake> courseTakes,
        int timeConflictCount, boolean checkType, boolean isContinuous)
    {
        Set<TeachingClass> temp = new HashSet<>();
        List<TeachingClass> result = new ArrayList<>();
        if (null != courseTakes)
        {
            for (ElcCourseTake courseTake : courseTakes)
            {
                //				Lesson lesson2 = courseTake.getLesson();
                //				if (temp.contains(lesson2)) {
                //					continue;
                //				}
                //				if (isConflict(lesson, lesson2,timeConflictCount,checkType,isContinuous)) {
                //					result.add(lesson2);
                //				}
                //				temp.add(lesson2);
            }
        }
        return result;
    }
    
}