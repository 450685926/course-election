package com.server.edu.election.studentelec.rules.yjs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.service.cache.RoundCacheService;

/**
 * 在可选课名单内才能选课
 *
 */
@Component("yjsMustInElectableListRule")
public class MustInElectableListRule extends AbstractElecRuleExceutor{
    @Autowired
    private RoundCacheService roundCacheService;
	
	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		String studentId = context.getStudentInfo().getStudentId();
		Long roundId = context.getRequest().getRoundId();
		boolean containsStu = roundCacheService.containsStu(roundId, studentId);
		
		if (containsStu) {
			return true;
		}
		
		ElecRespose respose = context.getRespose();
		respose.getFailedReasons()
		.put(courseClass.getCourseCodeAndClassCode(),
				I18nUtil.getMsg("ruleCheck.isLoserStu"));
		return false;
	}
}
