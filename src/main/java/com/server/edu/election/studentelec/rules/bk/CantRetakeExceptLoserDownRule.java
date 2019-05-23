package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;

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
        boolean isReTakeCourse = RetakeCourseUtil.isRetakeCourse(context,
            courseClass.getCourseCode());
        //历史课程中要包含替代的课程 todo
        boolean flag = repeater || !isReTakeCourse;
        if (!flag)
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.cantRetakeExceptLoserDown"));
            return false;
        }
        
        return true;
        
    }
    
}