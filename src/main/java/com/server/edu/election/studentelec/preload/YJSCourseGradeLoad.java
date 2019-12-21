package com.server.edu.election.studentelec.preload;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.election.dao.*;
import com.server.edu.election.entity.Course;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.studentelec.context.*;
import com.server.edu.election.util.WeekUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.TeacherClassTimeRoom;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
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
    Logger logger = LoggerFactory.getLogger(YJSCourseGradeLoad.class);
	
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
    
    @Autowired
    private DictionaryService dictionaryService;
    
    @Autowired
    private CourseDao courseDao;

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
        BeanUtils.copyProperties(stu, studentInfo);
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();// 已完成通過课程
        Set<CompletedCourse> failedCourse = context.getFailedCourse();// 未通过课程
        List<CompletedCourse> takenCourses = new ArrayList<CompletedCourse>(); // 已修读课程
        
        List<ScoreStudentResultVo> stuScore = ScoreServiceInvoker.findStuScore(studentId);
        if (CollectionUtil.isNotEmpty(stuScore))
        {
//            List<Long> teachClassIds = stuScore.stream()
//                    .map(temp -> Long.parseLong(temp.getTeachingClassId()))
//                    .collect(Collectors.toList());
//            // 获取学院，教师名称
//            List<TeachingClassCache> classInfo = courseOpenDao.findClassInfo(teachClassIds);
//            Map<Long, TeachingClassCache> classMap = classInfo.stream().collect(Collectors.toMap(s->s.getTeachClassId(),s->s));
            List<String> courseCodes = stuScore.stream().map(ScoreStudentResultVo::getCourseCode).collect(Collectors.toList());
            List<Course> courses = elcCourseTakeDao.findCourses(courseCodes);
            Map<String, Course> map = courses.stream().collect(Collectors.toMap(Course::getCode, s -> s));
            int size = stuScore.size();
            List<Long> teachClassIds = new ArrayList<>();
            List<CompletedCourse> course = new ArrayList<CompletedCourse>();
            for (ScoreStudentResultVo studentScore : stuScore)
            {
                CompletedCourse lesson = new CompletedCourse();
//                Long teachingClassId = Long.parseLong(studentScore.getTeachingClassId());
//                lesson.setTeachClassId(teachingClassId);
                String courseCode = studentScore.getCourseCode();
                lesson.setCourseCode(courseCode);
                Course co = map.get(courseCode);
                if (co != null) {
                    lesson.setCourseName(co.getName());
                    lesson.setNature(co.getNature());
                    lesson.setFaculty(co.getCollege());
                    lesson.setTerm(co.getTerm());
                }
                lesson.setStudentId(studentScore.getStudentId());
                lesson.setCredits(studentScore.getCredit());
                Long calendarId = studentScore.getCalendarId();
                lesson.setCalendarId(calendarId);
                lesson.setIsPass(studentScore.getIsPass());
                lesson.setCourseLabelId(studentScore.getCourseLabelId());
                if (calendarId != null) {
                    List<TeachingClassCache> teachClass = elcCourseTakeDao.findTeachClass(studentId, calendarId, courseCode);
                    if (CollectionUtil.isNotEmpty(teachClass)) {
                        Set<String> names = teachClass.stream().map(TeachingClassCache::getTeacherName).collect(Collectors.toSet());
                        String name = String.join(",", names);
                        if (!"null".equals(name)) {
                            lesson.setTeacherName(name);
                        }
                        TeachingClassCache teachingClass = teachClass.get(0);
                        Long teachClassId = teachingClass.getTeachClassId();
                        lesson.setTeachClassId(teachClassId);
                        teachClassIds.add(teachClassId);
                        lesson.setTeachClassName(teachingClass.getTeachClassName());
                        lesson.setTeachClassCode(teachingClass.getTeachClassCode());
                        lesson.setRemark(teachingClass.getRemark());
                    }
                    SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                    // 根据校历id设置学年
                    if (schoolCalendar != null) {
                        lesson.setCalendarName(schoolCalendar.getYear()+"");
                    }
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
                course.add(lesson);
            }
            logger.info("-----------completedCourses------------:" + completedCourses.size());
            logger.info("-------------failedCourse--------------:" + failedCourse.size());
            logger.info("----------------course1-----------------:" + course.size());
            
            // 获取当前学年学期的课程(正在修读没有成绩的课程)
    		List<PlanCourseDto> plan = CultureSerivceInvoker.findCourseTypeForGradute(studentId);
            for (PlanCourseDto planCourseDto : plan) {
    			List<PlanCourseTypeDto> list = planCourseDto.getList();
    			for (PlanCourseTypeDto planCourseTypeDto : list) {
                    Long label = planCourseTypeDto.getLabelId();
                    String labelName = "";
                    if (label != null) {
                    	if (label == 999999) {
                    		labelName  = "跨院系或跨门类";
    					}else{
    						labelName  = courseDao.getCourseLabelName(label);
    					}
                    }
                    planCourseTypeDto.setLabelName(labelName);
    			}
    		}
            
            //Integer index =TableIndexUtil.getIndex(context.getCalendarId()-1);
            List<TeachingClassCache> currentCalendarCourses = elcCourseTakeDao.findCurrentCalendarCourses(studentId, context.getCalendarId());
            logger.info("--------studentId---------: " + studentId);
            logger.info("--------calendarId---------: " + context.getCalendarId());
            for (TeachingClassCache teachingClassCache : currentCalendarCourses) {
            	logger.info("--------teachingClassCache---------: " + teachingClassCache);
            	CompletedCourse lesson = new CompletedCourse();
            	lesson.setNature(teachingClassCache.getNature());
            	lesson.setCourseCode(teachingClassCache.getCourseCode());
            	lesson.setCourseName(teachingClassCache.getCourseName());
            	lesson.setTeachClassName(teachingClassCache.getTeachClassName());
            	lesson.setCredits(teachingClassCache.getCredits());
            	lesson.setFaculty(teachingClassCache.getFaculty());
            	lesson.setTerm(teachingClassCache.getTerm());
            	lesson.setRemark(teachingClassCache.getRemark());
            	lesson.setTeacherName(teachingClassCache.getTeacherName());
            	lesson.setTeachClassId(teachingClassCache.getTeachClassId());
            	lesson.setTeachClassCode(teachingClassCache.getTeachClassCode());
            	lesson.setTeachClassName(teachingClassCache.getTeachClassName());
            	lesson.setCalendarId(teachingClassCache.getCalendarId());
            	lesson.setStudentId(studentId);
            	SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(teachingClassCache.getCalendarId());
                // 根据校历id设置学年
                if (schoolCalendar != null) {
                    lesson.setCalendarName(schoolCalendar.getYear()+"");
                }
            	// 如果课程是培养计划中的课程，则取培养计划的课程lableId
            	for (PlanCourseDto planCourseDto : plan) {
            		List<PlanCourseTypeDto> list = planCourseDto.getList();
    				Set<String> courseCodeSet = list.stream().map(PlanCourseTypeDto::getCourseCode).collect(Collectors.toSet());
    				
    				if (courseCodeSet.contains(teachingClassCache.getCourseCode())) {
    					List<PlanCourseTypeDto> collect = list.stream().filter(vo->StringUtils.equals(vo.getCourseCode(), teachingClassCache.getCourseCode())).collect(Collectors.toList());
    					PlanCourseTypeDto planCourseTypeDto = collect.get(0);
    					lesson.setCourseLabelId(planCourseTypeDto.getLabelId());
    					lesson.setLabelName(planCourseTypeDto.getLabelName());
    					break;
    				}else {
    					String dict = dictionaryService.query(DictTypeEnum.X_KCXZ.getType(),teachingClassCache.getNature());
    					lesson.setCourseLabelId(Long.parseLong(teachingClassCache.getNature()));
    					lesson.setLabelName(dict);
    				}
    			}
            	course.add(lesson);
            	teachClassIds.add(teachingClassCache.getTeachClassId());
			}
            logger.info("-----------course2------------:" + course.size());
            
            // 已修读课程去重(studentId+courseCode+calendarId)
            List<CompletedCourse> coursess = new ArrayList<CompletedCourse>();
            if (CollectionUtil.isNotEmpty(course)) {
            	StringBuffer codeAndStudentIdbuffer = new StringBuffer("");
            	for (CompletedCourse courseVo : course) {
            		String codeAndStudentId = courseVo.getStudentId()+courseVo.getCalendarId()+courseVo.getCourseCode();
            		if (!codeAndStudentIdbuffer.toString().contains(codeAndStudentId)) {
            			codeAndStudentIdbuffer.append(codeAndStudentId).append(",");
						coursess.add(courseVo);
					}
				}
			}
            
            if (CollectionUtil.isNotEmpty(teachClassIds)) {
                Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
                for (CompletedCourse cours : coursess) {
                	logger.info("----------111111111---------:" + cours.toString());
                    Long teachClassId = cours.getTeachClassId();
                    if (teachClassId == null) {
                        continue;
                    }
                    List<ClassTimeUnit> classTimeUnits = collect.get(teachClassId);
                    if (CollectionUtil.isNotEmpty(classTimeUnits)) {
                        List<TimeAndRoom> list = new ArrayList<>();
                        for (ClassTimeUnit classTimeUnit : classTimeUnits) {
                            TimeAndRoom time=new TimeAndRoom();
                            Integer dayOfWeek = classTimeUnit.getDayOfWeek();
                            Integer timeStart = classTimeUnit.getTimeStart();
                            Integer timeEnd = classTimeUnit.getTimeEnd();
                            String roomID = classTimeUnit.getRoomId();
                            List<Integer> weeks = classTimeUnit.getWeeks();
                            if (CollectionUtil.isEmpty(weeks)) {
                                continue;
                            }
                            List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                            String weekNumStr = weekNums.toString();//周次
                            if ("[1, 3, 5, 7, 9, 11, 13, 15, 17]".equals(weekNumStr)) {
                                weekNumStr = "单周";
                            } else if ("[2, 4, 6, 8, 10, 12, 14, 16".equals(weekNumStr)) {
                                weekNumStr = "双周";
                            }
                            String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                            String timeStr=weekstr+timeStart+"-"+timeEnd+weekNumStr+" ";
                            time.setTimeAndRoom(timeStr);
                            time.setRoomId(roomID);
                            list.add(time);
                        }
                        cours.setTimeTableList(list);
                    }
                }
            }
            logger.info("----------TakenCourses--------: "+ coursess.size());
            context.setTakenCourses(coursess);
//            takenCourses.addAll(course);
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
    	List<ElecCourse> applyRecord = applyDao.findApplyRecordForElection(studentId);
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
