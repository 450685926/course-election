package com.server.edu.election.studentelec.preload;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.server.edu.common.dto.CultureRuleDto;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.util.CourseCalendarNameUtil;
import com.server.edu.util.CollectionUtil;

/**
 * 本科生培养计划课程查询
 * 
 */
@Component
public class BKCoursePlanLoad extends DataProLoad
{
    Logger log = LoggerFactory.getLogger(getClass());
    
    @Override
    public int getOrder()
    {
        return 2;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1,2,4";
    }

    @Override
    public void load(ElecContext context)
    {
        StudentInfoCache stu = context.getStudentInfo();
        List<PlanCourseDto> courseType = CultureSerivceInvoker.findCourseType(stu.getStudentId());
        if(CollectionUtil.isNotEmpty(courseType)){
            log.info("plan course size:{}", courseType.size());
            Set<PlanCourse> planCourses = context.getPlanCourses();//培养课程
            Set<ElecCourse> publicCourses = context.getPublicCourses();//通识选修课
            Set<CourseGroup> courseGroups = context.getCourseGroups();//课程组学分限制
            for (PlanCourseDto planCourse : courseType) {
                List<PlanCourseTypeDto> list = planCourse.getList();
                CultureRuleDto rule = planCourse.getRule();
                Long label = planCourse.getLabel();
                if(CollectionUtil.isNotEmpty(list)){
                    for (PlanCourseTypeDto planCourseTypeDto : list) {//培养课程
                        PlanCourse pl=new PlanCourse();
                        pl.setSemester(planCourseTypeDto.getSemester());
                        pl.setWeekType(planCourseTypeDto.getWeekType());
                        pl.setCourseCode(planCourseTypeDto.getCourseCode());
                        pl.setCourseName(planCourseTypeDto.getName());
                        pl.setNameEn(planCourseTypeDto.getNameEn());
                        pl.setSubCourseCode(planCourseTypeDto.getSubCourseCode());
                        pl.setCredits(planCourseTypeDto.getCredits());
                        String calendarName = CourseCalendarNameUtil.getCalendarName(stu.getGrade(), planCourseTypeDto.getSemester());
                        pl.setCalendarName(calendarName);
                        pl.setLabel(label);
                        planCourses.add(pl);
                        if("1".equals(rule.getLabelType())){//通识选修课
                            ElecCourse c=new ElecCourse();
                            c.setCourseCode(planCourseTypeDto.getCourseCode());
                            c.setCourseName(planCourseTypeDto.getName());
                            c.setNameEn(planCourseTypeDto.getNameEn());
                            c.setCredits(planCourseTypeDto.getCredits());
                            String calendar = CourseCalendarNameUtil.getCalendarName(stu.getGrade(), planCourseTypeDto.getSemester());
                            c.setCalendarName(calendar);
                            publicCourses.add(c);
                        }
                    }
                }
                if("1".equals(rule.getLimitType())&&rule.getExpression().intValue()==2){
                    CourseGroup courseGroup=new CourseGroup();
                    courseGroup.setLabel(label);
                    courseGroup.setCrrdits(rule.getMinCredits());
                    if("1".equals(rule.getLabelType())){
                        courseGroup.setLimitType("1");
                    }else{
                        courseGroup.setLimitType("0");
                    }
                    courseGroups.add(courseGroup);
                }
            }
        }
    }


}
