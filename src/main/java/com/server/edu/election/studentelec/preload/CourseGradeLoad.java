package com.server.edu.election.studentelec.preload;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.election.dao.*;
import com.server.edu.election.entity.ExemptionApplyManage;
import com.server.edu.election.studentelec.context.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.StudentScore;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.util.CollectionUtil;

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
    
    @Autowired
    private TeachingClassDao classDao;

    @Autowired
    private ExemptionApplyDao applyDao;
    
    @Override
    public void load(ElecContext context)
    {
        // select course_id, passed from course_grade where student_id_ = ? and status = 'PUBLISHED'
        // 1. 查询学生课程成绩(已完成)
        StudentInfoCache studentInfo = context.getStudentInfo();
        
        String studentId = studentInfo.getStudentId();
        Student stu = studentDao.findStudentByCode(studentId);
        if (null == stu)
        {
            String msg =
                String.format("student not find studentId=%s", studentId);
            throw new RuntimeException(msg);
        }
        List<StudentScoreVo> stuScoreBest =
            ScoreServiceInvoker.findStuScoreBest(studentId);
        
        BeanUtils.copyProperties(stu, studentInfo);
        
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        if (CollectionUtil.isNotEmpty(stuScoreBest))
        {
            for (StudentScoreVo studentScore : stuScoreBest)
            {
                CompletedCourse lesson = new CompletedCourse();
                lesson.setCourseCode(studentScore.getCourseCode());
                lesson.setCourseName(studentScore.getCourseName());
                lesson.setScore(studentScore.getTotalMarkScore());
                lesson.setCredits(studentScore.getCredit());
                lesson.setExcellent(studentScore.isBestScore());
                lesson.setCalendarId(studentScore.getCalendarId());
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
        //选课集合
        List<ElcCourseTakeVo> courseTakes =
            elcCourseTakeDao.findSelectedCourses(studentId, calendarId);
        if (CollectionUtil.isNotEmpty(courseTakes))
        {
            List<Long> teachClassIds = courseTakes.stream()
                .map(temp -> temp.getTeachingClassId())
                .collect(Collectors.toList());
            //按周数拆分的选课数据集合
            List<ClassTimeUnit> list = classDao.getClassTimes(teachClassIds);
            //一个教学班分组
            Map<Long, List<ClassTimeUnit>> collect = list.stream()
                .collect(Collectors.groupingBy(ClassTimeUnit::getTeachClassId));
            
            for (ElcCourseTakeVo c : courseTakes)
            {
                SelectedCourse selectedCourse = new SelectedCourse();
                
                //一个教学班的排课时间信息
                List<ClassTimeUnit> classTimeUnits =
                    collect.get(c.getTeachingClassId());
                
                /*classTimeUnits.forEach(temp -> {
                    List<Integer> weeks = voList.stream()
                        .filter(vo -> temp.getArrangeTimeId()
                            .equals(vo.getArrangeTimeId()))
                        .map(vo -> vo.getWeekNumber())
                        .collect(Collectors.toList());
                    temp.setWeeks(weeks);
                });*/
                
                selectedCourse.setCampus(c.getCampus());
                selectedCourse.setChooseObj(selectedCourse.getChooseObj());
                selectedCourse.setCourseCode(c.getCourseCode());
                selectedCourse.setCourseName(c.getCourseName());
                selectedCourse.setCourseTakeType(c.getCourseTakeType());
                selectedCourse.setCredits(c.getCredits());
                //selectedCourse.setNameEn(c.geten);
                //selectedCourse.setPublicElec(publicElec);
                //selectedCourse.setRebuildElec(rebuildElec);
                selectedCourse.setTeachClassId(c.getTeachingClassId());
                selectedCourse.setTurn(c.getTurn());
                selectedCourse.setTimes(classTimeUnits);
                selectedCourses.add(selectedCourse);
                
            }
        }

        //3.学生免修课程
        List<ElecCourse> applyRecord = applyDao.findApplyRecord(calendarId, studentId);
        Set<ElecCourse> applyForDropCourses = context.getApplyForDropCourses();
        applyForDropCourses.addAll(applyRecord);


        // 4. 非本学期的选课并且没有成功的
    }
    
}
