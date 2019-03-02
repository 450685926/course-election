package com.server.edu.election.studentelec.rules.bk;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourseClass;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.util.CollectionUtil;

/**
 * 只能退本轮选的课
 * 
 * 
 */
@Component("WithdrawTimeCheckerRule")
public class WithdrawTimeCheckerRule extends AbstractRuleExceutor
{
    @Override
    public boolean checkRule(ElecContext context, ElecCourseClass courseClass)
    {
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        // TODO Auto-generated method stub
    	if(courseClass.getTeacherClassId()!=null&&CollectionUtil.isNotEmpty(selectedCourses)&&StringUtils.isNotBlank(courseClass.getCourseCode())) {
    		List<SelectedCourse> list = selectedCourses.stream().filter(c->courseClass.getCourseCode().equals(c.getCourseCode())).collect(Collectors.toList());
    		if(CollectionUtil.isNotEmpty(list)) {
    			return true;
    		}else {
    			ElecRespose respose = context.getRespose();
				respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
						I18nUtil.getMsg("ruleCheck.withdrawTimeCheckerRule"));
			}
    	}
        return false;
    }
    
}
