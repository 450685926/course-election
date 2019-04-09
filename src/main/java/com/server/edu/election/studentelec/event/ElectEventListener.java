package com.server.edu.election.studentelec.event;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.preload.CourseGradeLoad;

@Component
public class ElectEventListener implements ApplicationListener<ElectLoadEvent>
{
    @Autowired
    private CourseGradeLoad courseGradeLoad;
    
    @Override
    public void onApplicationEvent(ElectLoadEvent event)
    {
        Long calendarId = event.getCalendarId();
        String studentId = event.getStudentId();
        if (null != calendarId && StringUtils.isNoneBlank(studentId))
        {
            ElecContext context = new ElecContext(studentId, calendarId);
            StudentInfoCache studentInfo = context.getStudentInfo();
            if (StringUtils.isBlank(studentInfo.getStudentName()))
            {
                return;
            }
            // 重新加载学生缓存数据
            courseGradeLoad.loadSelectedCourses(studentId,
                context.getSelectedCourses(),
                calendarId);
            
            context.saveToCache();
        }
        
    }
    
}
