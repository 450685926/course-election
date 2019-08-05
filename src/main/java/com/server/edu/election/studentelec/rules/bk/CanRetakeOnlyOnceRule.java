package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.util.CollectionUtil;

/**
 * 通过课程 只能重修一次
 * 课程通过一次 只能重修一次
 * CanRetakeOnlyOnceChecker
 */
@Component("CanRetakeOnlyOnceRule")
public class CanRetakeOnlyOnceRule extends AbstractElecRuleExceutorBk
{
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();
        long successCount=0L;
        long failCount=0L;
        if(CollectionUtil.isNotEmpty(completedCourses)){
             successCount = completedCourses.stream().filter(c -> courseClass.getCourseCode()
                    .equals(c.getCourse().getCourseCode())).count();
        }

        if(CollectionUtil.isEmpty(failedCourse)){
            failCount = completedCourses.stream().filter(c -> courseClass.getCourseCode()
                    .equals(c.getCourse().getCourseCode())).count();
        }

        if(successCount==1 && failCount==0){
            return true;
        }
        if(successCount<1){
            return true;
        }

            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.canRetakeOnlyOnce"));
            return false;

    }
    
}
