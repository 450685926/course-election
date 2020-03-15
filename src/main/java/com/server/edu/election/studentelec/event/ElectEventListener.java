package com.server.edu.election.studentelec.event;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCouSubsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.preload.BKCourseGradeLoad;
import com.server.edu.election.studentelec.preload.YJSCourseGradeLoad;
import com.server.edu.election.vo.ElcCouSubsVo;

@Component
public class ElectEventListener implements ApplicationListener<ElectLoadEvent>
{
    @Autowired
    private BKCourseGradeLoad bKCourseGradeLoad;
    
    @Autowired
    private YJSCourseGradeLoad yjsCourseGradeLoad;
    
    @Autowired
    private StudentDao studentDao;
    
    @Autowired
    private ElcCouSubsDao elcCouSubsDao;
    
    @Override
    public void onApplicationEvent(ElectLoadEvent event)
    {
        Long calendarId = event.getCalendarId();
        String studentId = event.getStudentId();
        if (null != calendarId && StringUtils.isNoneBlank(studentId))
        {
            Student stu = studentDao.findStudentByCode(studentId);
            
            if (null == stu)
            {
                return;
            }
            if (Constants.PROJ_UNGRADUATE.equals(stu.getManagerDeptId()))
            {
                ElecContextBk context =
                    new ElecContextBk(studentId, calendarId);
                context.courseClear();
                context.elecApplyCoursesClear();
                context.elecReplaceCoursesClear();
                context.elecCompletedCoursesClear();
                context.elecFailedCoursesClear();
                StudentInfoCache studentInfo = context.getStudentInfo();
                // 重新加载学生缓存数据
                bKCourseGradeLoad.loadSelectedCourses(studentId,
                    context.getSelectedCourses(),
                    calendarId);
            	ElcCouSubsDto dto = new ElcCouSubsDto();
            	dto.setStudentId(studentId);
                List<ElcCouSubsVo> elcCouSubsList =
                		elcCouSubsDao.selectElcNoGradCouSubs(dto);
                bKCourseGradeLoad.loadElecApplyCourse(studentId, context.getElecApplyCourses(), calendarId);
                bKCourseGradeLoad.loadElcCouSubsCourse(studentId, context.getReplaceCourses(),elcCouSubsList);
                bKCourseGradeLoad.loadScoreTemp(context, studentInfo, studentId, stu,elcCouSubsList);
                context.saveToCache();
            }
            else
            {
                ElecContext context = new ElecContext(studentId, calendarId);
                context.courseClear();
                // 重新加载学生缓存数据
                yjsCourseGradeLoad.loadSelectedCourses(studentId,
                    context.getSelectedCourses(),
                    calendarId);
                yjsCourseGradeLoad.loadApplyRecord(calendarId, studentId, context.getApplyForDropCourses());
                context.saveToCache();
            }
            
        }
        
    }
    
}
