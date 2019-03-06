package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.StudentScore;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.context.TimeUnit;
import com.server.edu.election.vo.SelectedCourseVo;
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
        List<StudentScore> stuScoreBest = ScoreServiceInvoker.findStuScoreBest(studentInfo.getStudentId());

        BeanUtils.copyProperties(stu, studentInfo);

        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        if(CollectionUtil.isNotEmpty(stuScoreBest)){

            for (StudentScore studentScore : stuScoreBest) {
                CompletedCourse lesson = new CompletedCourse();
                lesson.setCourseCode(studentScore.getCourseCode());
                lesson.setCourseName(studentScore.getCourseName());
                lesson.setScore(studentScore.getTotalMarkScore());
                lesson.setCredits(studentScore.getCredit());
                lesson.setExcellent(true);
                completedCourses.add(lesson);
            }
        }


        //2.学生已选择课程
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        //得到校历id
        ElectionRounds electionRounds =
            elecRoundsDao.selectByPrimaryKey(context.getRoundId());
        if (electionRounds == null)
        {
            String msg = String.format("electionRounds not find roundId=%s",
                context.getRoundId());
            throw new RuntimeException(msg);
        }
        Long calendarId = electionRounds.getCalendarId();
        Example example = new Example(ElcCourseTake.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", studentInfo.getStudentId());
        criteria.andEqualTo("calendarId", calendarId);
        //选课集合
        List<ElcCourseTake> elcCourseTake =
            elcCourseTakeDao.selectByExample(example);
        if (CollectionUtil.isNotEmpty(elcCourseTake))
        {
        	List<Long> elcCourseTakeIds = elcCourseTake.stream().map(temp->temp.getId()).collect(Collectors.toList());
        	//按周数拆分的选课数据集合
            List<SelectedCourseVo> list =
                elcCourseTakeDao.findSelectedCourses(elcCourseTakeIds);
            elcCourseTake.forEach(c -> {
                SelectedCourse selectedCourse = new SelectedCourse();
                //一个教学班的课程信息
                List<SelectedCourseVo> voList = list.stream()
                    .filter(temp -> temp.getTeachingclassId()
                        .equals(c.getTeachingClassId()))
                    .collect(Collectors.toList());
                //一个教学班的排课时间信息
                List<TimeUnit> timeUnits = new ArrayList<>();
                voList.forEach(temp -> {
                    TimeUnit timeUnit = new TimeUnit();
                    timeUnit.setArrangeTimeId(temp.getArrangeTimeId());
                    timeUnit.setTimeStart(temp.getTimeStart());
                    timeUnit.setTimeEnd(temp.getTimeEnd());
                    timeUnits.add(timeUnit);
                });
                timeUnits.stream().distinct().collect(Collectors.toList());
                timeUnits.forEach(temp -> {
                    List<Integer> weeks = voList.stream()
                        .filter(vo -> temp.getArrangeTimeId()
                            .equals(vo.getArrangeTimeId()))
                        .map(vo -> vo.getWeek())
                        .collect(Collectors.toList());
                    temp.setWeeks(weeks);
                });
                BeanUtils.copyProperties(voList.get(0), selectedCourse);
                selectedCourse.setTimes(timeUnits);
                selectedCourses.add(selectedCourse);
            });
        }
        // 3. 非本学期的选课并且没有成功的
    }
    
}
