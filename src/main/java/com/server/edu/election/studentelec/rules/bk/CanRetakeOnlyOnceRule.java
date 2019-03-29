package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 通过课程只能重修一次
 * 
 */
@Component("CanRetakeOnlyOnceRule")
public class CanRetakeOnlyOnceRule extends AbstractElecRuleExceutor
{
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        
        if (courseClass.getTeachClassType() != null
            && CollectionUtil.isNotEmpty(completedCourses))
        {
            if (StringUtils.isNotBlank(courseClass.getTeachClassType()))
            {
                List<CompletedCourse> list = completedCourses.stream()
                    .filter(c -> courseClass.getCourseCode()
                        .equals(c.getCourseCode()))
                    .collect(Collectors.toList());
                
                if (CollectionUtil.isNotEmpty(list))
                {
                    if (Constants.REBUILD_CALSS
                        .equals(courseClass.getTeachClassType()))
                    {
                        ElecRespose respose = context.getRespose();
                        respose.getFailedReasons()
                            .put(courseClass.getCourseCodeAndClassCode(),
                                I18nUtil.getMsg("ruleCheck.canRetakeOnlyOnce"));
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
}
