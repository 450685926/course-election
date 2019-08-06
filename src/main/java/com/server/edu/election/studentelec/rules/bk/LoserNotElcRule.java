package com.server.edu.election.studentelec.rules.bk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;

/**
 * 预警学生不能选课
 * LoserGoodbyeRule
 */
@Component("LoserNotElcRule")
public class LoserNotElcRule extends AbstractElecRuleExceutorBk
{
    @Autowired
    private StudentDao studentDao;
    
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        String studentId = context.getStudentInfo().getStudentId();
        ElecRequest request = context.getRequest();
        Student stu = studentDao.isLoserStu(request.getRoundId(), studentId);
        if (stu == null)
        {
            return true;
        }
        
        ElecRespose respose = context.getRespose();
        respose.getFailedReasons()
            .put(courseClass.getCourseCodeAndClassCode(),
                I18nUtil.getMsg("ruleCheck.isLoserStu"));
        
        return false;
        
    }
}
