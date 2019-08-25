package com.server.edu.election.studentelec.preload;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.election.dao.*;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.Teacher;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.TeacherClassTimeRoom;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
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

/**
 * 查询研究生有成绩的课程
 * 
 */
@Component
public class YJSCourseGradeLoad extends DataProLoad<ElecContext>
{
    @Override
    public int getOrder()
    {
        return 1;
    }
    
    @Override
    public String getProjectIds()
    {
    	return "2,4";
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

    @Override
    public void load(ElecContext context)
    {
        // select course_id, passed from course_grade where student_id_ = ? and status = 'PUBLISHED'
        // 1. 查询学生课程成绩(包括已完成)
        StudentInfoCache studentInfo = context.getStudentInfo();
        ElecRequest request = context.getRequest();
        String studentId = studentInfo.getStudentId();
        Student stu = studentDao.findStudentByCode(studentId);
        if (null == stu)
        {
            String msg =
                String.format("student not find studentId=%s", studentId);
            throw new RuntimeException(msg);
        }
        List<ScoreStudentResultVo> stuScore = ScoreServiceInvoker.findStuScore(studentId);

        BeanUtils.copyProperties(stu, studentInfo);
        
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();//未完成
        if (CollectionUtil.isNotEmpty(stuScore))
        {
//            List<Long> teachClassIds = stuScore.stream()
//                    .map(temp -> Long.parseLong(temp.getTeachingClassId()))
//                    .collect(Collectors.toList());
//            // 获取学院，教师名称
//            List<TeachingClassCache> classInfo = courseOpenDao.findClassInfo(teachClassIds);
//            Map<Long, TeachingClassCache> classMap = classInfo.stream().collect(Collectors.toMap(s->s.getTeachClassId(),s->s));
//            Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
            for (ScoreStudentResultVo studentScore : stuScore)
            {
                CompletedCourse lesson = new CompletedCourse();
//                Long teachingClassId = Long.parseLong(studentScore.getTeachingClassId());
//                lesson.setTeachClassId(teachingClassId);
                lesson.setCourseCode(studentScore.getCourseCode());
                lesson.setCourseName(studentScore.getCourseName());
                lesson.setScore(studentScore.getTotalMarkScore());
                lesson.setCredits(studentScore.getCredit());
                lesson.setExcellent(studentScore.isBestScore());
                Long calendarId = studentScore.getCalendarId();
                lesson.setCalendarId(calendarId);
                lesson.setIsPass(studentScore.getIsPass());
                lesson.setCourseLabelId(studentScore.getCourseLabelId());
                lesson.setTeachClassName(studentScore.getTeachingClassName());
                lesson.setTeacherName(studentScore.getTeacherName());
                lesson.setCheat(
                    StringUtils.isBlank(studentScore.getTotalMarkScore()));
                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                // 根据校历id设置学年
                lesson.setCalendarName(schoolCalendar.getYear()+"");
//                List<ClassTimeUnit> classTimeUnits = collect.get(teachingClassId);
//                if (CollectionUtil.isNotEmpty(classTimeUnits)) {
//                    List<TimeAndRoom> list = new ArrayList<>();
//                    for (ClassTimeUnit classTimeUnit : classTimeUnits) {
//                        TimeAndRoom time=new TimeAndRoom();
//                        Integer dayOfWeek = classTimeUnit.getDayOfWeek();
//                        Integer timeStart = classTimeUnit.getTimeStart();
//                        Integer timeEnd = classTimeUnit.getTimeEnd();
//                        String roomID = classTimeUnit.getRoomId();
//                        List<Integer> weeks = classTimeUnit.getWeeks();
//                        if (CollectionUtil.isEmpty(weeks)) {
//                            continue;
//                        }
//                        List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
//                        String weekNumStr = weekNums.toString();//周次
//                        if ("[1, 3, 5, 7, 9, 11, 13, 15, 17]".equals(weekNumStr)) {
//                            weekNumStr = "单周";
//                        } else if ("[2, 4, 6, 8, 10, 12, 14, 16".equals(weekNumStr)) {
//                            weekNumStr = "双周";
//                        }
//                        String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
//                        String timeStr=weekstr+timeStart+"-"+timeEnd+weekNumStr+" ";
//                        time.setTimeAndRoom(timeStr);
//                        time.setRoomId(roomID);
//                        list.add(time);
//                    }
//                    lesson.setTimeTableList(list);
//                }
                // 设置学院，教师名称
//                TeachingClassCache classCache = classMap.get(teachingClassId);
//                if (classCache == null) {
//                    continue;
//                }
//                lesson.setNature(classCache.getNature());
//                lesson.setTeachClassCode(classCache.getTeachClassCode());
//                lesson.setRemark(classCache.getRemark());
//                lesson.setFaculty(classCache.getFaculty());
//                lesson.setTerm(classCache.getTerm());
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
        //得到校历id
        Long calendarId = 0L;
        if (request.getRoundId() != null) {
        	ElectionRounds electionRounds =
        			elecRoundsDao.selectByPrimaryKey(request.getRoundId());
        	if (electionRounds == null)
        	{
        		String msg = String.format("electionRounds not find roundId=%s",
        				request.getRoundId());
        		throw new RuntimeException(msg);
        	}
        	calendarId = electionRounds.getCalendarId();
		}else {
			calendarId = request.getCalendarId();
		}
        
        //选课集合
        this.loadSelectedCourses(studentId, selectedCourses, calendarId);
        //3.学生免修课程
        Set<ElecCourse> applyForDropCourses = context.getApplyForDropCourses();
        this.loadApplyRecord(calendarId, studentId,applyForDropCourses);
        
        // 4. 非本学期的选课并且没有成功的
    }
    
    public void loadApplyRecord(Long calendarId, String studentId, Set<ElecCourse> applyForDropCourses) {
    	List<ElecCourse> applyRecord = applyDao.findApplyRecord(calendarId, studentId);
    	if (CollectionUtil.isNotEmpty(applyRecord)) {
    		applyForDropCourses.addAll(applyRecord);
		}
		
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
            SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
            // 获取学历年
            String year = schoolCalendar.getYear() + "";
            Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
            for (ElcCourseTakeVo c : courseTakes)
            {
                SelectedCourse course = new SelectedCourse();
                course.setTerm(c.getTerm());
                course.setCalendarName(year);
                course.setTeachClassMsg(c.getTeachingClassId());
                course.setNature(c.getNature());
                course.setApply(c.getApply());
                course.setCampus(c.getCampus());
                course.setChooseObj(c.getChooseObj());
                course.setCourseCode(c.getCourseCode());
                course.setCourseName(c.getCourseName());
                course.setCourseTakeType(c.getCourseTakeType());
                course.setCredits(c.getCredits());
                course.setCalendarId(c.getCalendarId());
                course.setAssessmentMode(c.getAssessmentMode());
                course.setPublicElec(
                    c.getIsPublicCourse() == Constants.ZERO ? false : true);
                course.setTeachClassId(c.getTeachingClassId());
                course.setTeachClassCode(c.getTeachingClassCode());
                course.setTurn(c.getTurn());
                course.setFaculty(c.getFaculty());
                List<ClassTimeUnit> times = this.concatTime(collect, course);
                course.setTimes(times);
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
                un.setRoomId(room.getRoomId());
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
                            .format("%s", t.getName());
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
                sb.append(String.format("%s ", tName));
            }        
        }
        return sb.toString();
    }
}
