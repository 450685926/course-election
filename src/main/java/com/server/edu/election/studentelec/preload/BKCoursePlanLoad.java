package com.server.edu.election.studentelec.preload;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourse;

import tk.mybatis.mapper.entity.Example;

/**
 * 本科生培养计划课程查询
 * 
 */
@Component
public class BKCoursePlanLoad implements DataProLoad
{
    @Autowired
    private CourseDao courseDao;
    
    @Override
    public int order()
    {
        return 2;
    }
    
    @Override
    public void load(ElecContext context)
    {
        StudentInfoCache stu = context.getStudentInfo();
        List<String> courseCodes =
            CultureSerivceInvoker.getCourseCodes(stu.getStudentId());
        
        List<ElecCourse> planCourses = context.getPlanCourses();
        
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
        
    }
    
}
