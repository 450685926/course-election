package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.election.studentelec.context.CompletedCourse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 不能重修，但是留降级学生除外
 *
 * 只有留降级可以重修
 *
 * CantRetakeExceptLoserDownFilter
 */
@Component("CantRetakeExceptLoserDownRule")
public class CantRetakeExceptLoserDownRule extends AbstractElecRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        boolean repeater = studentInfo.isRepeater();//是否留降级
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();
        List<CompletedCourse> hisCourse=new ArrayList<>();
        hisCourse.addAll(completedCourses);
        hisCourse.addAll(failedCourse);
        //历史课程中要包含替代的课程 todo
        boolean isReTakeCourse =hisCourse.contains(courseClass.getCourseCode());
         boolean flag= repeater || !isReTakeCourse;
        if(!flag){
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.cantRetakeExceptLoserDown"));
            return false;
        }

        return true;

    }
    
}