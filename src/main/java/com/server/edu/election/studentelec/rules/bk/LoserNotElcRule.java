package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.context.ElecRespose;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 预警学生不能选课
 * 
 */
@Component("LoserNotElcRule")
public class LoserNotElcRule extends AbstractRuleExceutor {
	private static final String PARAM = "IS_LOSER";

	@Autowired
	private StudentDao studentDao;

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		if(context.getRoundId()!=null){
			String studentId = context.getStudentInfo().getStudentId();
			Student stu = studentDao.isLoserStu(context.getRoundId(), studentId);
			if(stu==null){
				return true;
			}else{
				ElecRespose respose = context.getRespose();
				respose.getFailedReasons().put(courseClass.getTeachClassId().toString(),
						I18nUtil.getMsg("ruleCheck.isLoserStu"));
			}
		}
		return false;
	}
}
