package com.server.edu.election.studentelec.preload;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourse;

/**
 * 建议课表
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月26日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class SuggestCourseLoad extends DataProLoad
{
    @Autowired
    private TeachingClassDao classDao;
    
    @Override
    public int getOrder()
    {
        return 3;
    }

    @Override
    public void load(ElecContext context)
    {
        // TODO 
        StudentInfoCache studentInfo = context.getStudentInfo();
        // 1. 查询teaching_class_suggest_student_t表学生所对应的所有课程
        // 2. 根据学生年级专业查询所有teaching_class_suggest_profession_t对应课程
        List<ElecCourse> selectSuggestCourse = classDao.selectSuggestCourse(studentInfo);
        
    }
    
}
