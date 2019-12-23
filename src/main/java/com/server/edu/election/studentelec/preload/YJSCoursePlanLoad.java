package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.dto.CultureRuleDto;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.entity.Course;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.PlanCourse;
import com.server.edu.election.util.CourseCalendarNameUtil;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 研究生培养计划课程查询
 * 
 */
@Component
public class YJSCoursePlanLoad extends DataProLoad<ElecContext>
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
        return "2,4";
    }

    @Override
    public void load(ElecContext context)
    {
        StudentInfoCache stu = context.getStudentInfo();
        List<PlanCourseDto> courseType = new ArrayList<PlanCourseDto>();
        if (StringUtils.equalsIgnoreCase(context.getStudentInfo().getManagerDeptId(), "4")) {
        	courseType = CultureSerivceInvoker.findCourseTypeForGraduteExemption(stu.getStudentId());
		}else{
			courseType = CultureSerivceInvoker.findCourseTypeForGradute(stu.getStudentId());
		}
        
        if(CollectionUtil.isNotEmpty(courseType)){
            log.info("plan course size:{}", courseType.size());
            Set<PlanCourse> planCourses = context.getPlanCourses();//培养课程
            for (PlanCourseDto planCourse : courseType) {
                List<PlanCourseTypeDto> list = planCourse.getList();
                if(CollectionUtil.isNotEmpty(list)){
                    for (PlanCourseTypeDto planCourseTypeDto : list) {//培养课程
                        PlanCourse pl=new PlanCourse();
                        Example example = new Example(Course.class);
                        example.createCriteria().andEqualTo("code",planCourseTypeDto.getCourseCode());
                        Course course = courseDao.selectOneByExample(example);
                        if (course != null) {
                        	pl.setNature(course.getNature());
						}
                        Long label = planCourseTypeDto.getLabelId();
                        String labelName = null;
                        if (label != null) {
                        	if (label == 999999) {
                        		labelName  = "跨院系或跨门类";
							}else{
								labelName  = courseDao.getCourseLabelName(label);
							}
                        }
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
                        pl.setLabelName(labelName);
                        pl.setCompulsory(planCourseTypeDto.getCompulsory());
                        planCourses.add(pl);
                    }
                }
            }
        }
    }


}
