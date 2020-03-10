package com.server.edu.mutual.studentelec.rule;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.RulePriority;
import com.server.edu.mutual.studentelec.context.ElecContextMutualBk;
import com.server.edu.mutual.vo.SelectedCourse;
import com.server.edu.util.CollectionUtil;

/**
 * 时间冲突检查
 * TimeConflictChecker
 */
@Component("mutualTimeConflictCheckerRule")
public class TimeConflictCheckerRule extends AbstractMutualElecRuleExceutor
{
    
    public static final Boolean CHECK_CONFLICT = true;
    
    public static final Boolean CHECK_UN_CONFLICT = false;
    
    @Override
    public int getOrder()
    {
        return RulePriority.THIRD.ordinal();
    }
    
    @Override
    public boolean checkRule(ElecContextMutualBk context,TeachingClassCache courseClass)
    {
    	Long teachClassId = courseClass.getTeachClassId();//通过teachingClassId查询时间
        if (teachClassId != null)
        {
            /*List<ClassTimeUnit> teachingClassTime =
                teachingClassDao.findTeachingClassIdTime(teachClassId);*/
            List<ClassTimeUnit> teachingClassTime = courseClass.getTimes();
            if (CollectionUtil.isNotEmpty(teachingClassTime))
            {
                Set<SelectedCourse> selectedCourses = context.getSelectedCourses(); // 已经选择的课程（本研互选课程和普通选课课程）
                if (CollectionUtil.isNotEmpty(selectedCourses))
                {
                    for (SelectedCourse selectedCours : selectedCourses)
                    {
                        TeachingClassCache course = selectedCours.getCourse();
                        if (course != null) {
                        	if (StringUtils.equalsIgnoreCase(course.getCourseCode(),courseClass.getCourseCode())){
                        		continue;
                        	}

                        	List<ClassTimeUnit> times = course.getTimes();
                        	if (CollectionUtil.isNotEmpty(times)) {
                        		for (ClassTimeUnit v0 : teachingClassTime)
                        		{
                        			for (ClassTimeUnit v1 : times)
                        			{
                        				if (conflict(v0, v1))
                        				{
                        					ElecRespose respose = context.getRespose();
                        					respose.getFailedReasons()
                        					.put(courseClass.getCourseCodeAndClassCode(),
                        							I18nUtil.getMsg("ruleCheck.timeConflict",
                        									String.format("%s(%s)",course.getCourseName(),course.getCourseCode())));
                        					return false;
                        				}
                        			}
                        		}
                        	}
						}
                    }
                    
                }
                
            }
        }
        return true;
    }
    
    /**
     * 判断两个上课时间是否冲突
     * 
     * @param a
     * @param b
     * @return true冲突, false不冲突
     * @see [类、类#方法、类#成员]
     */
    public static boolean conflict(ClassTimeUnit a, ClassTimeUnit b)
    {
        // 星期几不相同肯定不会冲突
        if (a.getDayOfWeek() != b.getDayOfWeek())
        {
            return false;
        }
        // 星期相同时比较上课节次是否有重合
        int aTimeStart = a.getTimeStart();
        int aTimeEnd = a.getTimeEnd();
        int bTimeStart = b.getTimeStart();
        int bTimeEnd = b.getTimeEnd();
        boolean sameTime = false;
        // b 是否在 a 内  a=[2,6], b=[3,5]
        if ((aTimeStart <= bTimeStart && aTimeEnd >= bTimeStart)
            || (aTimeStart <= bTimeEnd && aTimeEnd >= bTimeEnd))
        {
        	sameTime = true;
        }
        // a 是否在 b 内 a=[3,5], b=[2,6]
        if ((bTimeStart <= aTimeStart && bTimeEnd >= aTimeStart)
            || (bTimeStart <= aTimeEnd && bTimeEnd >= aTimeEnd))
        {
        	sameTime = true;
        }
        // 节次有冲突时还要判断上课周是否有重合，a[1,2] 1-8; b[1,2] 9-10 是可以选的
        if (sameTime && CollectionUtil.isNotEmpty(a.getWeeks())
            && CollectionUtil.isNotEmpty(b.getWeeks()))
        {
            for (Integer w : b.getWeeks())
            {
                int binarySearch = Collections.binarySearch(a.getWeeks(), w);
                if (binarySearch >= 0)
                {
                	if(StringUtils.isNotBlank(a.getTeacherCode()) && StringUtils.isNotBlank(b.getTeacherCode()) && a.getTeacherCode().equals(b.getTeacherCode())) {
                		return true;
                	}
                }
            }
        }
        
        return false;
    }
    
}