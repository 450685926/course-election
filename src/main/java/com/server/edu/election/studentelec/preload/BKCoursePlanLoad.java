package com.server.edu.election.studentelec.preload;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.server.edu.common.dto.CultureRuleDto;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.PlanCourse;
import com.server.edu.election.util.CourseCalendarNameUtil;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 本科生培养计划课程查询
 * 
 */
@Component
public class BKCoursePlanLoad extends DataProLoad<ElecContextBk>
{
    Logger log = LoggerFactory.getLogger(getClass());
    

    @Autowired
    private CourseDao courseDao;
    
    @Override
    public int getOrder()
    {
        return 2;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1";
    }

    @Override
    public void load(ElecContextBk context)
    {
        StudentInfoCache stu = context.getStudentInfo();
        List<PlanCourseDto> courseType = CultureSerivceInvoker.findUnGraduateCourse(stu.getStudentId());
        if(CollectionUtil.isNotEmpty(courseType)){
            log.info("plan course size:{}", courseType.size());
            Set<PlanCourse> planCourses = context.getPlanCourses();//培养课程
            Set<ElecCourse> publicCourses = context.getPublicCourses();//通识选修课
            Set<CourseGroup> courseGroups = context.getCourseGroups();//课程组学分限制
            for (PlanCourseDto planCourse : courseType) {
                List<PlanCourseTypeDto> list = planCourse.getList();
                CultureRuleDto rule = planCourse.getRule();
                Long labelId = planCourse.getLabel();
                String labelName = planCourse.getLabelName();
                if(CollectionUtil.isNotEmpty(list)){
                    for (PlanCourseTypeDto pct : list) {//培养课程
                        String courseCode = pct.getCourseCode();
                        if(StringUtils.isBlank(courseCode)) {
                            log.warn("courseCode is Blank skip this record: {}", JSON.toJSONString(pct));
                            continue;
                        }
                        PlanCourse pl=new PlanCourse();
                        Example example = new Example(Course.class);
                        example.createCriteria().andEqualTo("code", courseCode);
                        Course course = courseDao.selectOneByExample(example);
                        ElecCourse course2 = new ElecCourse();
                        if (course != null) {
                            course2.setCourseCode(courseCode);
                            course2.setCourseName(pct.getName());
                            course2.setNameEn(pct.getNameEn());
                            course2.setNature(course.getNature());
                            course2.setCredits(pct.getCredits());
                            String calendarName = CourseCalendarNameUtil.getCalendarName(stu.getGrade(), pct.getSemester());
                            course2.setCalendarName(calendarName);
                            course2.setCompulsory(pct.getCompulsory());
                            course2.setLabelId(labelId);
                            course2.setLabelName(labelName);
						}
                        pl.setCourse(course2);
                        pl.setSemester(pct.getSemester());
                        pl.setWeekType(pct.getWeekType());
                        pl.setSubCourseCode(pct.getSubCourseCode());
                        pl.setLabel(labelId);
                        pl.setLabelName(labelName);
                        planCourses.add(pl);
                        if("1".equals(rule.getLabelType())){//通识选修课
                            ElecCourse c=new ElecCourse();
                            c.setCourseCode(courseCode);
                            c.setCourseName(pct.getName());
                            c.setNameEn(pct.getNameEn());
                            c.setCredits(pct.getCredits());
                            c.setCompulsory(pct.getCompulsory());
                            String calendar = CourseCalendarNameUtil.getCalendarName(stu.getGrade(), pct.getSemester());
                            c.setCalendarName(calendar);
                            publicCourses.add(c);
                        }
                    }
                }
                if("1".equals(rule.getLimitType())&&rule.getExpression().intValue()==2){
                    CourseGroup courseGroup=new CourseGroup();
                    courseGroup.setLabel(labelId);
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
