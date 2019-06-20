package com.server.edu.election.studentelec.rules.bk;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 时间冲突检查
 * TimeConflictChecker
 */
@Component("TimeConflictCheckerRule")
public class TimeConflictCheckerRule extends AbstractElecRuleExceutor
{
    
    public static final Boolean CHECK_CONFLICT = true;
    
    public static final Boolean CHECK_UN_CONFLICT = false;
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        Long teachClassId = courseClass.getTeachClassId();//通过teachingClassId查询时间
        if (teachClassId != null)
        {
            /*List<ClassTimeUnit> teachingClassTime =
                teachingClassDao.findTeachingClassIdTime(teachClassId);*/
            List<ClassTimeUnit> teachingClassTime = courseClass.getTimes();
            if (CollectionUtil.isNotEmpty(teachingClassTime))
            {
                Set<SelectedCourse> selectedCourses =
                    context.getSelectedCourses();//已经选择的课程，时间班级
                if (CollectionUtil.isNotEmpty(selectedCourses))
                {
                    for (SelectedCourse selectedCours : selectedCourses)
                    {
                        List<ClassTimeUnit> times = selectedCours.getTimes();
                        for (ClassTimeUnit v0 : teachingClassTime)
                        {
                            for (ClassTimeUnit v1 : times)
                            {
                                if (conflict(v0, v1))
                                {
                                    ElecRespose respose = context.getRespose();
                                    respose.getFailedReasons()
                                        .put(
                                            courseClass
                                                .getCourseCodeAndClassCode(),
                                            I18nUtil.getMsg(
                                                "ruleCheck.timeConflict"));
                                    return false;
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
        // b 是否在 a 内  a=[2,6], b=[3,5]
        if ((aTimeStart <= bTimeStart && aTimeEnd >= bTimeStart)
            || (aTimeStart <= bTimeEnd && aTimeEnd >= bTimeEnd))
        {
            return true;
        }
        // a 是否在 b 内 a=[3,5], b=[2,6]
        if ((bTimeStart <= aTimeStart && bTimeEnd >= aTimeStart)
            || (bTimeStart <= aTimeEnd && bTimeEnd >= aTimeEnd))
        {
            return true;
        }
        // 节次不冲突判断上课周是否有重合
        if (CollectionUtil.isNotEmpty(a.getWeeks())
            && CollectionUtil.isNotEmpty(b.getWeeks()))
        {
            for (Integer w : b.getWeeks())
            {
                int binarySearch = Collections.binarySearch(a.getWeeks(), w);
                if (binarySearch >= 0)
                {
                    return true;
                }
            }
        }
        
        return false;
    }
    
}