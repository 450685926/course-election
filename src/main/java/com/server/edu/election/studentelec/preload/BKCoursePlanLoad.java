package com.server.edu.election.studentelec.preload;

import java.util.List;
import java.util.Set;

import com.server.edu.common.dto.CultureRuleDto;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.election.studentelec.context.PlanCourse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 本科生培养计划课程查询
 * 
 */
@Component
public class BKCoursePlanLoad extends DataProLoad
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
    public void load(ElecContext context)
    {
        StudentInfoCache stu = context.getStudentInfo();
        /*List<String> courseCodes =
            CultureSerivceInvoker.getCourseCodes(stu.getStudentId());
        
        if (CollectionUtil.isNotEmpty(courseCodes))
        {
            log.info("plan course size:{}", courseCodes.size());
            
            Set<ElecCourse> planCourses = context.getPlanCourses();
            
            Example example = new Example(Course.class);
            example.createCriteria().andIn("code", courseCodes);
            List<Course> list = courseDao.selectByExample(example);
            
            for (Course course : list)
            {
                ElecCourse c = new ElecCourse();
                c.setCourseCode(course.getCode());
                c.setCourseName(course.getName());
                c.setCredits(course.getCredits());
                c.setNameEn(course.getNameEn());
                planCourses.add(c);
            }
        }*/

        List<PlanCourseDto> courseType = CultureSerivceInvoker.findCourseType(stu.getStudentId());
        if(CollectionUtil.isNotEmpty(courseType)){
            log.info("plan course size:{}", courseType.size());

            Set<PlanCourse> planCourses = context.getPlanCourses();
            Set<ElecCourse> publicCourses = context.getPublicCourses();
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
                        pl.setCredits(planCourseTypeDto.getCredits());
                        pl.setLabel(label);
                        if(rule!=null){
                            if("1".equals(rule.getLabelType())){//通识选修课
                                ElecCourse c = new ElecCourse();
                                c.setCourseCode(planCourseTypeDto.getCourseCode());
                                c.setCourseName(planCourseTypeDto.getName());
                                c.setCredits(planCourseTypeDto.getCredits());
                                c.setNameEn(planCourseTypeDto.getNameEn());
                                publicCourses.add(c);
                            }
                            if("1".equals(rule.getLimitType())&&"2".equals(rule.getExpression())){//课程组学分限制
                                Double minCredits = rule.getMinCredits();
                                pl.setCredits(minCredits);
                            }
                        }
                        planCourses.add(pl);
                    }
                }

            }
        }
    }


}
