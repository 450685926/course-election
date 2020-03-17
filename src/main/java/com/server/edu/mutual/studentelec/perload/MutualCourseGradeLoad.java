package com.server.edu.mutual.studentelec.perload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.Teacher;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.TeacherClassTimeRoom;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.util.WeekModeUtil;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.mutual.studentelec.context.ElecContextMutual;
import com.server.edu.mutual.vo.SelectedCourse;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;

/**
 * 查询学生选课课程数据
 * 
 * @author  LuoXiaoLi
 * @version  [版本号, 2020年3月17日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class MutualCourseGradeLoad extends MutualDataProLoad<ElecContextMutual>
{
    //log日志记录
    Logger logger = LoggerFactory.getLogger(MutualCourseGradeLoad.class);

    @Autowired
    private ElcCourseTakeDao elcCourseTakeDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private TeachingClassTeacherDao teacherDao;

    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;
    
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

    @Override
    public void load(ElecContextMutual context)
    {
        StudentInfoCache studentInfo = context.getStudentInfo();
        String studentId = studentInfo.getStudentId();
        
        ElecRequest request = context.getRequest();
        Long calendarId = request.getCalendarId();
        
        // 学生已选择课程
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        this.loadSelectedCourses(studentId, selectedCourses, calendarId);
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
    	Integer index =TableIndexUtil.getIndex(calendarId);
    	//记录日志
        logger.info("----alex---it is start to get selectedCourses.........................");
        List<ElcCourseTakeVo> courseTakes =
            elcCourseTakeDao.findSelectedCourses(studentId, calendarId,index);
        String name = SchoolCalendarCacheUtil.getName(calendarId);
        // 获取学历年
        if (CollectionUtil.isNotEmpty(courseTakes))
        {
            List<ElcCourseTakeVo> elcCourseTakeVos = elcCourseTakeDao.findCompulsory(studentId);
            Map<String, String> map = new HashMap<>();
            if (CollectionUtil.isNotEmpty(elcCourseTakeVos)) {
                for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                    String compulsory = elcCourseTakeVo.getCompulsory();
                    if (StringUtils.isNotBlank(compulsory)) {
                        map.put(elcCourseTakeVo.getCourseCode(), compulsory);
                    }
                }
            }
            List<Long> teachClassIds = courseTakes.stream()
                .map(temp -> temp.getTeachingClassId())
                .collect(Collectors.toList());
            Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
            for (ElcCourseTakeVo c : courseTakes)
            {
                SelectedCourse course = new SelectedCourse();
                TeachingClassCache lesson = new TeachingClassCache();
                lesson.setCalendarName(name);
                lesson.setNature(c.getNature());
                lesson.setApply(c.getApply());
                lesson.setCampus(c.getCampus());
                lesson.setCourseCode(c.getCourseCode());
                lesson.setCourseName(c.getCourseName());
                lesson.setCredits(c.getCredits());
                lesson.setCalendarId(c.getCalendarId());
                lesson.setPublicElec(
                    c.getIsPublicCourse() == Constants.ZERO ? false : true);
                lesson.setTeachClassId(c.getTeachingClassId());
                lesson.setTeachClassCode(c.getTeachingClassCode());
                lesson.setFaculty(c.getFaculty());
                lesson.setTerm(c.getTerm());
                lesson.setCompulsory(map.get(c.getCourseCode()));
                List<ClassTimeUnit> times = this.concatTime(collect, lesson);
                lesson.setTimes(times);
                course.setCourse(lesson);
                course.setLabel(c.getLabel());
                course.setChooseObj(c.getChooseObj());
                course.setCourseTakeType(c.getCourseTakeType());
                course.setAssessmentMode(c.getAssessmentMode());
                course.setTurn(c.getTurn());
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
        List<TeacherClassTimeRoom> list = new ArrayList<>();
        //按周数拆分的选课数据集合
        if(CollectionUtil.isNotEmpty(teachClassIds)) {
        	list = classDao.getClassTimes(teachClassIds);
        }
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
                Map<String, List<TeacherClassTimeRoom>> timeRoomMap1 = rooms.stream().
                        filter(s->s.getTeacherCode() != null)
                        .collect(Collectors
                                .groupingBy(TeacherClassTimeRoom::getTeacherCode));
                for (Entry<String, List<TeacherClassTimeRoom>> entry3 : timeRoomMap1
                        .entrySet()){
                    List<TeacherClassTimeRoom> rooms3 = entry3.getValue();
                    if (CollectionUtil.isEmpty(rooms3))
                    {
                        continue;
                    }
                    ClassTimeUnit un = new ClassTimeUnit();
                    TeacherClassTimeRoom room = rooms3.get(0);
                    un.setArrangeTimeId(room.getArrangeTimeId());
                    un.setDayOfWeek(room.getDayOfWeek());
                    un.setTeachClassId(room.getTeachClassId());
                    un.setTimeEnd(room.getTimeEnd());
                    un.setTimeStart(room.getTimeStart());
                    un.setTeacherCode(room.getTeacherCode());
                    un.setRoomId(room.getRoomId());
                    // 所有周
                    List<Integer> weeks = rooms3.stream()
                            .map(TeacherClassTimeRoom::getWeekNumber)
                            .collect(Collectors.toList());
                    // 相同教室相同老师的周次
                    Map<String, List<TeacherClassTimeRoom>> roomTeacherMap =
                            rooms3.stream().collect(Collectors.groupingBy(r -> {
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

                        //String weekStr = CalUtil.getWeeks(roomWeeks);
                        //单双周
                        String weekStr = WeekModeUtil.parse(weeks, SessionUtils.getLocale());

                        String teacherNames = getTeacherInfo(r.getTeacherCode());

                        String roomName =
                                ClassroomCacheUtil.getRoomName(r.getRoomId());
                        // 老师名称(老师编号)[周] 教室
                        sb.append(String
                                .format("%s %s %s", teacherNames, weekStr, roomName))
                                .append(" ");
                    }
                    Collections.sort(weeks);
                    un.setValue(sb.toString());
                    un.setWeeks(weeks);
                    times.add(un);
                }

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
    	Long teachClassId = c.getTeachClassId();
        List<ClassTimeUnit> times = collect.get(teachClassId);
        String teacherName = null;
        if (CollectionUtil.isNotEmpty(times))
        {
            for (ClassTimeUnit ctu : times)
            {
                ctu.setValue(String.format("%s(%s) %s",
                    c.getCourseName(),
                    c.getTeachClassCode(),
                    ctu.getValue()));
            }

            teacherName = this.getTeacherName(times);
            
            c.setTeacherName(teacherName);
            
            return times;
        }else{
        	List<String> findNamesByTeachingClassId = teacherDao.findNamesByTeachingClassId(teachClassId);
        	Set<String> names = new HashSet<>(findNamesByTeachingClassId);
        	teacherName = StringUtils.join(names, ",");
        	c.setTeacherName(teacherName);
        }
        
        return null;
    }

    public List<ClassTimeUnit> concatYjsTime(
            List<TimeTableMessage> timeTableMessages, TeachingClassCache c)
    {
        //一个教学班的排课时间信息
        Long teachClassId = c.getTeachClassId();
        List<String> names = teachingClassTeacherDao.findNamesByTeachingClassId(teachClassId);
        String teacherName = null;
        if (CollectionUtil.isNotEmpty(names)) {
            teacherName = String.join(",", names);
        }
        c.setTeacherName(teacherName);
        List<ClassTimeUnit> list = new ArrayList<>(10);
        if (CollectionUtil.isNotEmpty(timeTableMessages))
        {
            String str = new StringBuilder().append(teacherName).append(" ").append(c.getCourseName()).toString();
            for (TimeTableMessage timeTableMessage : timeTableMessages)
            {
                ClassTimeUnit ct = new ClassTimeUnit();
                ct.setTeachClassId(timeTableMessage.getTeachingClassId());
                ct.setArrangeTimeId(timeTableMessage.getTimeId());
                ct.setTimeStart(timeTableMessage.getTimeStart());
                ct.setTimeEnd(timeTableMessage.getTimeEnd());
                ct.setDayOfWeek(timeTableMessage.getDayOfWeek());
                List<Integer> weeks = timeTableMessage.getWeeks();
                ct.setWeeks(weeks);
                String weekStr = WeekModeUtil.parse(weeks, SessionUtils.getLocale());
                String roomName =
                        ClassroomCacheUtil.getRoomName(timeTableMessage.getRoomId());
                ct.setValue(str + weekStr + roomName);
                list.add(ct);
            }
        }
        return list;
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
    
    private String getTeacherInfo(String teacherCode)
    {
        if (StringUtils.isEmpty(teacherCode))
        {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String[] codes = teacherCode.split(",");
        List<Teacher> teachers = TeacherCacheUtil.getTeachers(codes);

        if (CollectionUtil.isNotEmpty(teachers))
        {
            for (Teacher teacher : teachers) {
                if(teacher != null){
                    String tName = teacher.getName();
                    // 老师名称(老师编号)
                    sb.append(String.format("%s(%s) ", tName, teacher.getCode()));
                }

            }
        }
        return sb.toString();
    }
}
