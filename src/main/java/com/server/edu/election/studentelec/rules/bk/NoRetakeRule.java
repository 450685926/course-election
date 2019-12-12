package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutorBk;
import com.server.edu.election.studentelec.utils.RetakeCourseUtil;

/**
 * 限制不能选择重修课
 * ElectableLessonNoRetakeFilter
 */
@Component("NoRetakeRule")
public class NoRetakeRule extends AbstractElecRuleExceutorBk
{

    @Autowired
    private TeachingClassDao teachingClassDao;

    @Override
    public boolean checkRule(ElecContextBk context,
        TeachingClassCache courseClass)
    {

        ElecRequest request = context.getRequest();
        Long calendarId = request.getCalendarId();
        StudentInfoCache studentInfo = context.getStudentInfo();

        //查找本学期和上学期的开始结束时间
        SchoolCalendarVo calendarVo1 = SchoolCalendarCacheUtil.getCalendar(calendarId);
        SchoolCalendarVo calendarVo2 = SchoolCalendarCacheUtil.getCalendar(calendarId - 1);
//        Long beginDay1 = calendarVo1.getBeginDay();
//        Long endDay1 = calendarVo1.getEndDay();
//        Long beginDay2 = calendarVo2.getBeginDay();
//        Long endDay2 = calendarVo2.getEndDay();
        Integer year1 = calendarVo1.getYear();
        Integer term1 = calendarVo1.getTerm();
        Integer year2 = calendarVo1.getYear();
        Integer term2 = calendarVo1.getTerm();

        boolean count = RetakeCourseUtil.isRetakeCourseBk(context,
            courseClass.getCourseCode());
        //查看学生是否有学籍异动信息
//        Integer count1 = teachingClassDao.getStudentAbnormalCount(studentInfo.getStudentId(), beginDay1, endDay1);
//
//        Integer count2 = teachingClassDao.getStudentAbnormalCount(studentInfo.getStudentId(),beginDay2,endDay2);

        Integer count1 = teachingClassDao.getStudentAbnormalCountNew(studentInfo.getStudentId(), year1, term1);

        Integer count2 = teachingClassDao.getStudentAbnormalCountNew(studentInfo.getStudentId(),year2,term2);
        if (count1.intValue() > 0 || count2.intValue() > 0){
            return true;
        }

        if (count)//重修
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.noRetake"));
            return false;
        }
        return true;
    }
    
}