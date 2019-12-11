package com.server.edu.election.studentelec.rules.bk;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.StudentPayment;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;

/**
 * 检查学生是否缴费，未缴费的不能进入选课
 * StdPayCostChecker
 */
@Component("StdPayCostCheckerRule")
public class StdPayCostCheckerRule extends AbstractElecRuleExceutorBk
{
    @Autowired
    private StudentDao studentDao;
    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        Long roundId = context.getRequest().getRoundId();
        if (studentInfo != null)
        {
        	ElectionRounds round = dataProvider.getRound(roundId);
        	if(!Integer.valueOf(Constants.FIRST_TURN).equals(round.getTurn()) && !Integer.valueOf(Constants.SECOND_TURN).equals(round.getTurn())) {
        		return true;
        	}
        	Long calendarId = context.getCalendarId();
        	SchoolCalendarVo schoolCalendarVo =SchoolCalendarCacheUtil.getCalendar(calendarId);
        	String year ="";
        	String term = "";
    		if(schoolCalendarVo!=null) {
    			year =schoolCalendarVo.getYear().toString();
    			term = schoolCalendarVo.getTerm().toString();
    		}
        	StudentPayment studentPayment = studentDao.getStudentPayment(studentInfo.getStudentId(),year,term);
            if (studentPayment !=null)
            {
            	if(Constants.IS_PAYMENT.equals(studentPayment.getPaymentStatus())) {
            		return true;
            	}
            }
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                    .put(courseClass.getCourseCodeAndClassCode(),
                            I18nUtil.getMsg("ruleCheck.stdPayCostChecker"));
            return false;

        }
        return true;
    }
    
}
