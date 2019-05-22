package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.Teacher;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionApplyCoursesDao;
import com.server.edu.election.dao.ExemptionApplyDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.CompletedCourse;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.util.CalUtil;
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
    
    @Autowired
    private ElectionApplyCoursesDao electionApplyCoursesDao;
    
    @Autowired
    private TeachingClassTeacherDao teacherDao;
    
    @Autowired
    private TeachClassCacheService teachClassCacheService;
    
    @Override
    public void load(ElecContext context)
    {
        // select course_id, passed from course_grade where student_id_ = ? and status = 'PUBLISHED'
        // 1. 查询学生课程成绩(包括已完成)
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
        Set<CompletedCourse> failedCourse = context.getFailedCourse();//未完成
        Set<ElecCourse> elecApplyCourses = context.getElecApplyCourses();
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
                lesson.setCheat(
                    StringUtils.isBlank(studentScore.getTotalMarkScore()));
                if (studentScore.getIsPass() != null
                    && studentScore.getIsPass().intValue() == Constants.ONE)
                {//已經完成課程
                    completedCourses.add(lesson);
                }
                else
                {
                    
                    failedCourse.add(lesson);
                }
                
            }
        }
        
        //2.学生已选择课程
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        ElecRequest request = context.getRequest();
        //得到校历id
        ElectionRounds electionRounds =
            elecRoundsDao.selectByPrimaryKey(request.getRoundId());
        if (electionRounds == null)
        {
            String msg = String.format("electionRounds not find roundId=%s",
                request.getRoundId());
            throw new RuntimeException(msg);
        }
        Long calendarId = electionRounds.getCalendarId();
        //选课集合
        this.loadSelectedCourses(studentId, selectedCourses, calendarId);
        List<ElecCourse> electionApplies = elcCourseTakeDao
            .selectApplyCourses(studentId, calendarId, Constants.ZERO);
        elecApplyCourses = new HashSet<>(electionApplies);
        //3.学生免修课程
        List<ElecCourse> applyRecord =
            applyDao.findApplyRecord(calendarId, studentId);
        Set<ElecCourse> applyForDropCourses = context.getApplyForDropCourses();
        applyForDropCourses.addAll(applyRecord);
        // 4. 非本学期的选课并且没有成功的
    }
    
    static String groupByRoom(ClassTimeUnit time)
    {
        return String.format("%s-%s", time.getRoomId(), time.getTeacherCode());
    }
    
    /**
     * 加载本学期已选课课程数据
     * 
     * @param studentId
     * @param selectedCourses
     * @param calendarId
     * @see [类、类#方法、类#成员]
     */
    public void loadSelectedCourses(String studentId,
        Set<SelectedCourse> selectedCourses, Long calendarId)
    {
        List<ElcCourseTakeVo> courseTakes =
            elcCourseTakeDao.findSelectedCourses(studentId, calendarId);
        if (CollectionUtil.isNotEmpty(courseTakes))
        {
            List<Long> teachClassIds = courseTakes.stream()
                .map(temp -> temp.getTeachingClassId())
                .collect(Collectors.toList());
            Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
            Map<String, Teacher> teacherMap = new HashMap<>();
            for (ElcCourseTakeVo c : courseTakes)
            {
                SelectedCourse selectedCourse = new SelectedCourse();
                selectedCourse.setApply(c.getApply());
                selectedCourse.setCampus(c.getCampus());
                selectedCourse.setChooseObj(c.getChooseObj());
                selectedCourse.setCourseCode(c.getCourseCode());
                selectedCourse.setCourseName(c.getCourseName());
                selectedCourse.setCourseTakeType(c.getCourseTakeType());
                selectedCourse.setCredits(c.getCredits());
                selectedCourse.setPublicElec(
                    c.getIsPublicCourse() == Constants.ZERO ? false : true);
                selectedCourse.setTeachClassId(c.getTeachingClassId());
                selectedCourse.setTeachClassCode(c.getTeachingClassCode());
                selectedCourse.setTurn(c.getTurn());
                List<ClassTimeUnit> times =
                    concatTime(collect, teacherMap, selectedCourse);
                selectedCourse.setTimes(times);
                
                String teacherName =
                    teachClassCacheService.getTeacherName(times);
                if (StringUtils.isNotBlank(teacherName))
                {
                    selectedCourse.setTeacherName(teacherName);
                }
                selectedCourses.add(selectedCourse);
                
            }
        }
    }
    
    /**
     * 查询教学班排课时间，并按教学班分组
     * 
     * @param teachClassIds
     * @return
     * @see [类、类#方法、类#成员]
     */
    public Map<Long, List<ClassTimeUnit>> groupByTime(List<Long> teachClassIds)
    {
        //按周数拆分的选课数据集合
        List<ClassTimeUnit> list = classDao.getClassTimes(teachClassIds);
        //一个教学班分组
        Map<Long, List<ClassTimeUnit>> collect = list.stream()
            .collect(Collectors.groupingBy(ClassTimeUnit::getTeachClassId));
        return collect;
    }
    
    /**
     * 拼接上课时间
     * 
     * @param collect
     * @param teacherMap
     * @param c
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<ClassTimeUnit> concatTime(
        Map<Long, List<ClassTimeUnit>> collect, Map<String, Teacher> teacherMap,
        TeachingClassCache c)
    {
        //一个教学班的排课时间信息
        List<ClassTimeUnit> classTimeUnits = collect.get(c.getTeachClassId());
        if (CollectionUtil.isNotEmpty(classTimeUnits))
        {
            List<ClassTimeUnit> classTimeList = new ArrayList<>();
            // 按上课节次分组取每个节次下对应的教室与老师
            Map<Long, List<ClassTimeUnit>> collect2 = classTimeUnits.stream()
                .collect(
                    Collectors.groupingBy(ClassTimeUnit::getArrangeTimeId));
            for (Entry<Long, List<ClassTimeUnit>> entry : collect2.entrySet())
            {
                List<ClassTimeUnit> times = entry.getValue();
                if (CollectionUtil.isEmpty(times))
                {
                    continue;
                }
                
                // 按教室、老师分组
                Map<String, List<ClassTimeUnit>> collect3 = times.stream()
                    .collect(
                        Collectors.groupingBy(CourseGradeLoad::groupByRoom));
                StringBuilder sb = new StringBuilder();
                sb.append(String
                    .format("%s(%s) ", c.getCourseName(), c.getCourseCode()));
                int step = 0;
                for (Entry<String, List<ClassTimeUnit>> entry1 : collect3
                    .entrySet())
                {
                    List<ClassTimeUnit> rooms = entry1.getValue();
                    if (CollectionUtil.isEmpty(rooms))
                    {
                        continue;
                    }
                    ClassTimeUnit room = rooms.get(0);
                    List<Integer> weekNumbers = rooms.stream()
                        .map(ClassTimeUnit::getWeekNumber)
                        .sorted()
                        .collect(Collectors.toList());
                    
                    List<String> weekStr = CalUtil
                        .getWeekNums(weekNumbers.toArray(new Integer[] {}));
                    
                    String teacherCode = room.getTeacherCode();
                    Teacher teacherInfo =
                        getTeacherInfo(teacherMap, teacherCode);
                    String teacherName =
                        teacherInfo != null ? teacherInfo.getName() : "";
                    if (step > 0)
                    {
                        sb.append(",");
                    }
                    // 老师名称(老师编号)周次
                    sb.append(String.format("%s(%s)%s",
                        teacherName,
                        teacherCode,
                        StringUtils.join(weekStr, ",")));
                    step++;
                }
                ClassTimeUnit time = times.get(0);
                time.setValue(sb.toString());
                classTimeList.add(time);
            }
            return classTimeList;
        }
        
        return null;
    }
    
    private Teacher getTeacherInfo(Map<String, Teacher> teacherMap,
        String teacherCode)
    {
        Teacher teacherInfo = null;
        if (teacherMap.containsKey(teacherCode))
        {
            teacherInfo = teacherMap.get(teacherCode);
        }
        else
        {
            teacherInfo = TeacherCacheUtil.getTeacher(teacherCode);
            teacherMap.put(teacherCode, teacherInfo);
        }
        return teacherInfo;
    }
    
}
