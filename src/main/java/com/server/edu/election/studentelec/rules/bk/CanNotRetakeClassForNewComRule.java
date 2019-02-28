package com.server.edu.election.studentelec.rules.bk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

/**
 * 新选课程不出现重修班
 *
 */
@Component("CanNotRetakeClassForNewComRule")
public class CanNotRetakeClassForNewComRule
    extends AbstractRuleExceutor
{
    private static final String RULE_PARAM_FOR_NEW_ELECT =
        "CAN_NOT_RETAKE_CLASS_FOR_NEW";
    @Autowired
    private TeachingClassDao teachingClassDao;
    
    @Override
    public int getOrder()
    {
        return RulePriority.FOURTH.ordinal();
    }
    
    /**
     * 在学生选课时，被onExecuteRuleReturn调用<br>
     * 在学生进入选课界面时被调用
     * FIXME 这个数据有多少???
     */
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
    	Long id =courseClass.getTeacherClassId();
    	if(id!=null) {
    		if(courseClass.getTeacherClassType()!=null) {
    			if(Constants.REBUILD_CALSS.equals(courseClass.getTeacherClassType())) {
    	    		 ElecRespose respose = context.getRespose();
    	    		 respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(), I18nUtil.getMsg("ruleCheck.canNotRetakeClassForNewCom"));
    	    		 return false;
    			}
    		}
    	}
    	
        return true;
    }

}