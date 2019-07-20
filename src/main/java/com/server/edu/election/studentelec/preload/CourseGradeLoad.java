package com.server.edu.election.studentelec.preload;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.Teacher;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionApplyDao;
import com.server.edu.election.dao.ExemptionApplyDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.TeacherClassTimeRoom;
import com.server.edu.election.entity.ElectionApply;
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
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.util.CalUtil;
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
    
    @Override
    public String getProjectIds()
    {
    	return "1,2,4";
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
    private ElectionApplyDao electionApplyDao;

    @Autowired
    private CourseOpenDao courseOpenDao;

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

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
        if (CollectionUtil.isNotEmpty(stuScoreBest))
        {
            List<Long> teachingClassIds = stuScoreBest.stream().map(StudentScoreVo::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> timeTableMessages = setTeachingArrange(teachingClassIds);
            Map<Long, List<TimeTableMessage>> collect = timeTableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            for (StudentScoreVo studentScore : stuScoreBest)
            {
                CompletedCourse lesson = new CompletedCourse();
                String courseCode = studentScore.getCourseCode();
                lesson.setCourseCode(courseCode);
                lesson.setCourseName(studentScore.getCourseName());
                lesson.setScore(studentScore.getTotalMarkScore());
                lesson.setCredits(studentScore.getCredit());
                lesson.setExcellent(studentScore.isBestScore());
                Long calendarId = studentScore.getCalendarId();
                lesson.setCalendarId(calendarId);
//                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
//                lesson.setCalendarName(schoolCalendar.getFullName());
                lesson.setIsPass(studentScore.getIsPass());
                lesson.setNature(studentScore.getCourseNature());
                lesson.setCourseLabelId(studentScore.getCourseLabelId());
                lesson.setTeacherName(studentScore.getTeacherName());
                lesson.setCheat(
                    StringUtils.isBlank(studentScore.getTotalMarkScore()));
                lesson.setRemark(studentScore.getRemark());
                List<TimeTableMessage> tables = collect.get(studentScore.getTeachingClassId());
                if (CollectionUtil.isNotEmpty(tables)) {
                    List<String> teachingArrange = tables.stream().map(TimeTableMessage::getTimeAndRoom).collect(Collectors.toList());
                    lesson.setTeachingArrange(teachingArrange);
                }
                String faculty = courseOpenDao.selectFaculty(courseCode, studentScore.getCalendarId());
                if (faculty != null) {
                    lesson.setFaculty(faculty);
                }
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
        //3.学生免修课程
        List<ElecCourse> applyRecord =
            applyDao.findApplyRecord(calendarId, studentId);
        Set<ElecCourse> applyForDropCourses = context.getApplyForDropCourses();
        applyForDropCourses.addAll(applyRecord);
        // 4. 非本学期的选课并且没有成功的
        
        //5.保存选课申请
        Set<ElectionApply> elecApplyCourses = context.getElecApplyCourses();
        Example aExample =new Example(ElectionApply.class);
        Example.Criteria aCriteria = aExample.createCriteria();
        aCriteria.andEqualTo("studentId", studentId);
        aCriteria.andEqualTo("calendarId", calendarId);
        List<ElectionApply> electionApplys = electionApplyDao.selectByExample(aExample);
        elecApplyCourses.addAll(electionApplys);
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
            List<TimeTableMessage> timeTableMessages = setTeachingArrange(teachClassIds);
            Map<Long, List<TimeTableMessage>> timeTables = timeTableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            for (ElcCourseTakeVo c : courseTakes)
            {
                SelectedCourse course = new SelectedCourse();
                course.setTeachClassMsg(c.getTeachingClassId());
                course.setNature(c.getNature());
                course.setApply(c.getApply());
                course.setLabel(c.getLabel());
                course.setCampus(c.getCampus());
                course.setChooseObj(c.getChooseObj());
                course.setCourseCode(c.getCourseCode());
                course.setCourseName(c.getCourseName());
                course.setCourseTakeType(c.getCourseTakeType());
                course.setCredits(c.getCredits());
                course.setAssessmentMode(c.getAssessmentMode());
                course.setPublicElec(
                    c.getIsPublicCourse() == Constants.ZERO ? false : true);
                Long teachingClassId = c.getTeachingClassId();
                course.setTeachClassId(teachingClassId);
                course.setTeachClassCode(c.getTeachingClassCode());
                course.setTurn(c.getTurn());
                course.setFaculty(c.getFaculty());
                List<ClassTimeUnit> times = this.concatTime(collect, course);
                course.setTimes(times);
                List<TimeTableMessage> tables = timeTables.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(tables)) {
                    List<String> teachingArrange = tables.stream().map(TimeTableMessage::getTimeAndRoom).collect(Collectors.toList());
                    course.setTeachingArrange(teachingArrange);
                }

                selectedCourses.add(course);
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
        Map<Long, List<ClassTimeUnit>> map = new HashMap<>();
        //按周数拆分的选课数据集合
        List<TeacherClassTimeRoom> list = classDao.getClassTimes(teachClassIds);
        if (CollectionUtil.isEmpty(list))
        {
            return map;
        }
        //一个教学班分组
        Map<Long, List<TeacherClassTimeRoom>> classTimeMap = list.stream()
            .collect(
                Collectors.groupingBy(TeacherClassTimeRoom::getTeachClassId));
        
        for (Entry<Long, List<TeacherClassTimeRoom>> entry : classTimeMap
            .entrySet())
        {
            List<TeacherClassTimeRoom> ls = entry.getValue();
            if (CollectionUtil.isEmpty(ls))
            {
                continue;
            }
            List<ClassTimeUnit> times = new ArrayList<>();
            // time->room
            Map<Long, List<TeacherClassTimeRoom>> timeRoomMap = ls.stream()
                .collect(Collectors
                    .groupingBy(TeacherClassTimeRoom::getArrangeTimeId));
            for (Entry<Long, List<TeacherClassTimeRoom>> entry2 : timeRoomMap
                .entrySet())
            {
                List<TeacherClassTimeRoom> rooms = entry2.getValue();
                if (CollectionUtil.isEmpty(rooms))
                {
                    continue;
                }
                ClassTimeUnit un = new ClassTimeUnit();
                TeacherClassTimeRoom room = rooms.get(0);
                un.setArrangeTimeId(room.getArrangeTimeId());
                un.setDayOfWeek(room.getDayOfWeek());
                un.setTeachClassId(room.getTeachClassId());
                un.setTimeEnd(room.getTimeEnd());
                un.setTimeStart(room.getTimeStart());
                un.setTeacherCode(room.getTeacherCode());
                // 所有周
                List<Integer> weeks = rooms.stream()
                    .map(TeacherClassTimeRoom::getWeekNumber)
                    .collect(Collectors.toList());
                // 相同教室相同老师的周次
                Map<String, List<TeacherClassTimeRoom>> roomTeacherMap =
                    rooms.stream().collect(Collectors.groupingBy(r -> {
                        return r.getRoomId() + "_" + r.getTeacherCode();
                    }));
                
                StringBuilder sb = new StringBuilder();
                for (Entry<String, List<TeacherClassTimeRoom>> e : roomTeacherMap
                    .entrySet())
                {
                    List<TeacherClassTimeRoom> roomTeachers = e.getValue();
                    TeacherClassTimeRoom r = roomTeachers.get(0);
                    List<Integer> roomWeeks = roomTeachers.stream()
                        .map(TeacherClassTimeRoom::getWeekNumber)
                        .collect(Collectors.toList());
                    sb.append(this.fillValue(r, roomWeeks)).append(";");
                }
                Collections.sort(weeks);
                un.setValue(sb.toString());
                un.setWeeks(weeks);
                times.add(un);
            }
            
            map.put(entry.getKey(), times);
        }
        
        return map;
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
        Map<Long, List<ClassTimeUnit>> collect, TeachingClassCache c)
    {
        //一个教学班的排课时间信息
        List<ClassTimeUnit> times = collect.get(c.getTeachClassId());
        if (CollectionUtil.isNotEmpty(times))
        {
            for (ClassTimeUnit ctu : times)
            {
                ctu.setValue(String.format("%s(%s) %s",
                    c.getCourseName(),
                    c.getCourseCode(),
                    ctu.getValue()));
            }
            
            String teacherName = this.getTeacherName(times);
            c.setTeacherName(teacherName);

            return times;
        }
        
        return null;
    }
    
    private String getTeacherName(List<ClassTimeUnit> times)
    {
        String tName = null;
        if (CollectionUtil.isNotEmpty(times))
        {
            List<String> teacherSet = new ArrayList<>(times.stream()
                .map(ClassTimeUnit::getTeacherCode)
                .collect(Collectors.toSet()));
            if (CollectionUtil.isNotEmpty(teacherSet))
            {
                String str = StringUtils.join(teacherSet, ",");
                List<String> nameList = new ArrayList<>();
                Collections.addAll(nameList, str.split(","));
                Set<String> tnames = new HashSet<>(nameList);
                List<Teacher> teachers = TeacherCacheUtil
                    .getTeachers(tnames.toArray(new String[] {}));
                if (CollectionUtil.isNotEmpty(teachers))
                {
                    List<String> names = teachers.stream().map(t -> {
                        if (t == null)
                            return "";
                        return String
                            .format("%s(%s)", t.getName(), t.getCode());
                    }).collect(Collectors.toList());
                    
                    tName = StringUtils.join(names, ",");
                }
            }
        }
        return tName;
    }
    
    private String fillValue(TeacherClassTimeRoom r, List<Integer> weeks)
    {
        String weekStr = CalUtil.getWeeks(weeks);
        
        String teacherNames = getTeacherInfo(r.getTeacherCode());
        
        String roomName = ClassroomCacheUtil.getRoomName(r.getRoomId());
        // 老师名称(老师编号)[周] 教室
        return String.format("%s[%s] %s", teacherNames, weekStr, roomName);
    }
    
    private String getTeacherInfo(String teacherCode)
    {
        if (StringUtils.isEmpty(teacherCode))
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String[] codes = teacherCode.split(",");
        List<String> names = TeacherCacheUtil.getNames(codes);
        if(CollectionUtil.isNotEmpty(names)) {
            for (int i = 0; i < codes.length; i++)
            {
                String tCode = codes[i];
                String tName = names.get(i);
                // 老师名称(老师编号)
                sb.append(String.format("%s(%s) ", tName, tCode));
            }        
        }
        return sb.toString();
    }

    /**
     * 研究生教学安排信息获取
     * @param teachingClassIds
     * @return
     */
    private List<TimeTableMessage> setTeachingArrange(List<Long> teachingClassIds) {
        List<TimeTableMessage> timeTableMessages = courseTakeDao.findCourseArrange(teachingClassIds);
        for (TimeTableMessage timeTableMessage : timeTableMessages) {
            Integer dayOfWeek = timeTableMessage.getDayOfWeek();
            Integer timeStart = timeTableMessage.getTimeStart();
            Integer timeEnd = timeTableMessage.getTimeEnd();
            String weekNumber = timeTableMessage.getWeekNum();
            String[] str = weekNumber.split(",");
            List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
            List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[]{}));
            String weekstr = findWeek(dayOfWeek);//星期
            String timeStr=weekstr+" "+timeStart+"-"+timeEnd+ weekNums.toString()+ClassroomCacheUtil.getRoomName(timeTableMessage.getRoomId());
            timeTableMessage.setTimeAndRoom(timeStr);
        }

        return timeTableMessages;
    }

    public String findWeek(Integer number){
        String week="";
        switch(number){
            case 1:
                week="星期一";
                break;
            case 2:
                week="星期二";
                break;
            case 3:
                week="星期三";
                break;
            case 4:
                week="星期四";
                break;
            case 5:
                week="星期五";
                break;
            case 6:
                week="星期六";
                break;
            case 7:
                week="星期日";
                break;
        }
        return week;
    }
}
