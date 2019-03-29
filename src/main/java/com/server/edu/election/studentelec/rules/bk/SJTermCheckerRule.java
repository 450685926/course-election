package com.server.edu.election.studentelec.rules.bk;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.util.CollectionUtil;

/**
 * 控制实践课学期,当前学期所选教学班，必须匹配当前学生在这个学期是否有实践课
 * 
 */
@Component("SJTermCheckerRule")
public class SJTermCheckerRule extends AbstractElecRuleExceutor
{
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Override
    public boolean checkRule(ElecContext context,
        TeachingClassCache courseClass)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        String courseCode = courseClass.getCourseCode();
        ElecRequest request = context.getRequest();
        //得到校历id
        ElectionRounds electionRounds =
            dataProvider.getRound(request.getRoundId());
        
        if (electionRounds == null)
        {
            String msg = String.format("electionRounds not find roundId=%s",
                request.getRoundId());
            throw new RuntimeException(msg);
        }
        //获取当前学期
        Long calendarId = electionRounds.getCalendarId();
        //获取学生年级如2018
        Integer grade = studentInfo.getGrade();
        SchoolCalendarVo schoolCalendarVo =
            BaseresServiceInvoker.getSchoolCalendarById(calendarId);
        //计算学期如2018-2019 第二学期 则 year 2018 term 2
        Integer year = schoolCalendarVo.getYear();
        Integer term = schoolCalendarVo.getTerm();
        String semester = "";//根据学期计算该课程的开课学期
        if (year != null && grade != null && term != null)
        {
            int i = (year - grade) * 2 + term;
            if (i < 1)//说明课程开课学期不存在
            {
                return false;
            }
            semester += String.valueOf(i);
        }
        
        boolean flag = false;
        Set<PlanCourse> planCourses = context.getPlanCourses();//计划课程
        if (CollectionUtil.isNotEmpty(planCourses))
        {//根据培养计划内课程是否实践周获取实践课
            Set<PlanCourse> collect = planCourses.stream()
                .filter(temp -> temp.getWeekType().intValue() == 1)
                .collect(Collectors.toSet());
            if (CollectionUtil.isNotEmpty(collect))
            {
                flag = isPracticalCourse(collect, semester, courseCode);//判断学生中是否有该实践课
            }
        }
        if (flag)
        {
            return flag;
        }
        else
        {
            ElecRespose respose = context.getRespose();
            respose.getFailedReasons()
                .put(courseClass.getCourseCodeAndClassCode(),
                    I18nUtil.getMsg("ruleCheck.practicalCourseLimit"));
            return false;
        }
        
    }

    private boolean isPracticalCourse(Set<PlanCourse> collect, String semester,
        String courseCode)
    {
        boolean flag = false;
        for (PlanCourse planCourseTypeDto : collect)
        {
            String code = planCourseTypeDto.getCourseCode();//培养课程
            String semes = planCourseTypeDto.getSemester();//培养课程的学期
            int i = (","+semes+",").indexOf(","+semester+",");//判断是否有该学期
            if (courseCode.equals(code) && i !=-1)
            {
                flag = true;
            }
        }
        return flag;
    }
    
}