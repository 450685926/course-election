package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 查询学生有成绩的课程
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月25日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class CourseGradeLoad extends DataProLoad
{
    @Override
    public int getOrder()
    {
        return 1;
    }

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    @Autowired
    private ElcCourseTakeDao elcCourseTakeDao;

    @Override
    public void load(ElecContext context)
    {
     // select course_id, passed from course_grade where student_id_ = ? and status = 'PUBLISHED'
        // 1. 查询学生课程成绩
        StudentInfoCache studentInfo = context.getStudentInfo();

        Student stu = studentDao.findStudentByCode(studentInfo.getStudentId());
        if (null == stu)
        {
            String msg = String.format("student not find studentId=%s",
                    studentInfo.getStudentId());
            throw new RuntimeException(msg);
        }
        BeanUtils.copyProperties(stu, studentInfo);
        List<Map<String, Long>> results = new ArrayList<>();//TODO
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        for (Map<String, Long> map : results)
        {
            Long courseId = map.get("courseCode");
            Long passed = map.get("passed");
            if (passed == 1)
            {
                CompletedCourse lesson = new CompletedCourse();
                lesson.setCourseCode("");
                lesson.setCourseName("");
                completedCourses.add(lesson);
            }
        }
        //2.学生已选择课程
        List<SelectedCourse> selectedCourses = new ArrayList<>();
        //得到校历id
        ElectionRounds electionRounds = elecRoundsDao.selectByPrimaryKey(context.getRoundId());
        if(electionRounds==null) {
            String msg = String.format("electionRounds not find roundId=%s",
            		context.getRoundId());
            throw new RuntimeException(msg);
        }
        Long calendarId  = electionRounds.getCalendarId();
        Example example = new Example(ElcCourseTake.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId",studentInfo.getStudentId());
        criteria.andEqualTo("calendarId",calendarId);
        List<ElcCourseTake> elcCourseTake = elcCourseTakeDao.selectByExample(example);
        if(CollectionUtil.isEmpty(elcCourseTake)) {
            String msg = String.format("elcCourseTake is null=%s",
            		calendarId);
            throw new RuntimeException(msg);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("studentId", studentInfo.getStudentId());
        map.put("calendarId", calendarId);
        List<SelectedCourse> list = elcCourseTakeDao.findSelectedCourses(map);
        
        elcCourseTake.forEach(c->{
        	SelectedCourse selectedCourse = new SelectedCourse();
        	selectedCourse.setSelectedRound(c.getTurn());
        	selectedCourse.setChooseObj(c.getChooseObj());
        });
        // 3. 非本学期的选课并且没有成功的
    }
    
}
