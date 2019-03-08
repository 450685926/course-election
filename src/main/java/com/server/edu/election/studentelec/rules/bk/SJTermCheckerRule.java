package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

import java.util.List;

/**
 * 控制实践课学期
 * 
 */
@Component("SJTermCheckerRule")
public class SJTermCheckerRule extends AbstractRuleExceutor {

	@Autowired
	private ElecRoundsDao roundsDao;

	// protected PlanCreditLimitPrepare planCreditLimitPrepare;

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		StudentInfoCache studentInfo = context.getStudentInfo();
		//得到校历id
		ElectionRounds electionRounds =
				roundsDao.selectByPrimaryKey(context.getRoundId());
		if (electionRounds == null)
		{
			String msg = String.format("electionRounds not find roundId=%s",
					context.getRoundId());
			throw new RuntimeException(msg);
		}
		Long calendarId = electionRounds.getCalendarId();
		Integer grade = studentInfo.getGrade();
		SchoolCalendarVo schoolCalendarVo = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
		//计算学期
		Integer year = schoolCalendarVo.getYear();
		Integer term = schoolCalendarVo.getTerm();
		String semester="";
		if(year!=null  && grade!=null && term!=null){
			int i = (year - grade) * 2 + term;
			if(i<1){
				return false;
			}
			semester=String.valueOf(i);
		}
		List<PlanCourseTypeDto> courseType = CultureSerivceInvoker.findCourseType(studentInfo.getStudentId());


		return true;
	}



}