package com.server.edu.election.studentelec.event;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.preload.BKCourseGradeLoad;
import com.server.edu.election.studentelec.preload.YJSCourseGradeLoad;

@Component
public class ElectEventListener implements ApplicationListener<ElectLoadEvent>
{
    @Autowired
    private BKCourseGradeLoad bKCourseGradeLoad;
    
    @Autowired
    private YJSCourseGradeLoad yjsCourseGradeLoad;
    
    @Autowired
    private StudentDao studentDao;
    
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
                // 重新加载学生缓存数据
                bKCourseGradeLoad.loadSelectedCourses(studentId,
                    context.getSelectedCourses(),
                    calendarId);
                context.saveToCache();
            }
            else
            {
                ElecContext context = new ElecContext(studentId, calendarId);
                // 重新加载学生缓存数据
                yjsCourseGradeLoad.loadSelectedCourses(studentId,
                    context.getSelectedCourses(),
                    calendarId);
                context.saveToCache();
            }
            
        }
        
    }
    
}
