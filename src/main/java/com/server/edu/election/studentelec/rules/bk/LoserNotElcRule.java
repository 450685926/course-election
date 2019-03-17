package com.server.edu.election.studentelec.rules.bk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;

/**
 * 预警学生不能选课
 */
@Component("LoserNotElcRule")
public class LoserNotElcRule extends AbstractElecRuleExceutor
{
    
    @Autowired
    private StudentDao studentDao;
    
    @Override
    public boolean checkRule(ElecContext context,
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
