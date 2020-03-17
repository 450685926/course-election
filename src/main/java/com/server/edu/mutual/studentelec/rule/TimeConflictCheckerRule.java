package com.server.edu.mutual.studentelec.rule;

import java.util.ArrayList;
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
import com.server.edu.mutual.studentelec.context.ElecContextMutual;
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
    public boolean checkRule(ElecContextMutual context,TeachingClassCache courseClass)
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
                        					String key = courseClass.getCourseCode() + "[" + courseClass.getCourseName() + "]";
                        					respose.getFailedReasons()
                        					.put(key,I18nUtil.getMsg("ruleCheck.timeConflict",
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
    	// 0.周次判空
    	if (CollectionUtil.isEmpty(a.getWeeks()) || CollectionUtil.isEmpty(b.getWeeks())) {
    		return false;
    	}
    	
        // 1.星期几不相同肯定不会冲突
        if (a.getDayOfWeek() != b.getDayOfWeek())
        {
            return false;
        }
        
        // 2.两次操作课程一样
        if (StringUtils.equals(String.valueOf(a.getTeachClassId()), String.valueOf(b.getTeachClassId()))) {
        	return true;
		}
        
        // 3.周是否冲突
        boolean sameTime = false;
    	for (Integer w : b.getWeeks())
        {
            int binarySearch = Collections.binarySearch(a.getWeeks(), w);
            if (binarySearch >= 0)
            {
            	 sameTime = true;
            }
        }
    	
    	if (sameTime)
        {
    		// 4.判断节次是否冲突
    		List<Integer> aTime = new ArrayList<Integer>();
            for(int i = a.getTimeStart(); i <= a.getTimeEnd(); i++) {
            	aTime.add(i);
            }
            
            for(int i = b.getTimeStart(); i <= b.getTimeEnd(); i++) {
            	if(aTime.contains(i)) {
            		return true;
            	}
            }
        }
		
        return false;
    }    
}