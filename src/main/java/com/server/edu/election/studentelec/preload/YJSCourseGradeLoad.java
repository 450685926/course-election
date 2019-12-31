package com.server.edu.election.studentelec.preload;

import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSONObject;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.Course;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.studentelec.context.*;
import com.server.edu.election.util.WeekModeUtil;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.session.util.SessionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.DictTypeEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.election.constants.Constants;
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
    private TeachingClassTeacherDao teachingClassTeacherDao;
    
    @Autowired
    private DictionaryService dictionaryService;
    
    @Autowired
    private CourseDao courseDao;

    @Override
    public void load(ElecContext context)
    {
    	logger.info("----------YJSCourseGradeLoad start------------");
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
        
        List<ScoreStudentResultVo> stuScore = ScoreServiceInvoker.findStuScore(studentId);
        List<CompletedCourse> takenCourses = new ArrayList<CompletedCourse>(); // 已修读课程
        
        List<Long> teachClassIds = new ArrayList<>();
        List<CompletedCourse> course = new ArrayList<CompletedCourse>();
        
        /** 获取已完成通过的课程和未通过的课程 */
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
            
            logger.info("=========stuScore is size==========:" + size); 
                    
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
                    // 上课时间“春秋季”特殊处理
                    lesson.setTerm(schoolCalendar.getTerm().toString());
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
        }
        
        /** 获取历史选课记录(包括正在修读没有成绩的课程) */
        List<TeachingClassCache> historyElectedCourses = elcCourseTakeDao.findHistoryElectedCourses(studentId, context.getCalendarId());
        logger.info("--------historyElectedCourses size---------: " + historyElectedCourses.size());
        for (TeachingClassCache teachingClassCache : historyElectedCourses) {
        	CompletedCourse lesson = new CompletedCourse();
        	lesson.setNature(teachingClassCache.getNature());
        	lesson.setCourseCode(teachingClassCache.getCourseCode());
        	lesson.setCourseName(teachingClassCache.getCourseName());
        	lesson.setTeachClassName(teachingClassCache.getTeachClassName());
        	lesson.setCredits(teachingClassCache.getCredits());
        	lesson.setFaculty(teachingClassCache.getFaculty());

            SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(teachingClassCache.getCalendarId());
            // 上课时间“春秋季”特殊处理
            lesson.setTerm(schoolCalendar.getTerm().toString());

        	lesson.setRemark(teachingClassCache.getRemark());
        	lesson.setTeacherName(teachingClassCache.getTeacherName());
        	lesson.setTeachClassId(teachingClassCache.getTeachClassId());
        	lesson.setTeachClassCode(teachingClassCache.getTeachClassCode());
        	lesson.setTeachClassName(teachingClassCache.getTeachClassName());
        	lesson.setCalendarId(teachingClassCache.getCalendarId());
        	lesson.setStudentId(studentId);
            // 根据校历id设置学年
            if (schoolCalendar != null) {
                lesson.setCalendarName(schoolCalendar.getYear()+"");
            }
        	course.add(lesson);
        	teachClassIds.add(teachingClassCache.getTeachClassId());
		}
        logger.info("-----------course2------------:" + course.size());
        
        // 已修读课程去重(studentId+courseCode+calendarId)
        if (CollectionUtil.isNotEmpty(course)) {
        	StringBuffer codeAndStudentIdbuffer = new StringBuffer("");
        	for (CompletedCourse courseVo : course) {
        		String codeAndStudentId = courseVo.getStudentId()+courseVo.getCalendarId()+courseVo.getCourseCode();
        		if (!codeAndStudentIdbuffer.toString().contains(codeAndStudentId)) {
        			codeAndStudentIdbuffer.append(codeAndStudentId).append(",");
        			takenCourses.add(courseVo);
				}
			}
		}
        
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
        
        // 处理课程类别-->如果课程是培养计划中的课程，则取培养计划的课程lableId
    	for (PlanCourseDto planCourseDto : plan) {
    		List<PlanCourseTypeDto> list = planCourseDto.getList();
			Set<String> courseCodeSet = list.stream().map(PlanCourseTypeDto::getCourseCode).collect(Collectors.toSet());
			
			for (CompletedCourse completedCourse : takenCourses) {
				if (courseCodeSet.contains(completedCourse.getCourseCode())) {
					List<PlanCourseTypeDto> collect = list.stream().filter(vo->StringUtils.equals(vo.getCourseCode(), completedCourse.getCourseCode())).collect(Collectors.toList());
					PlanCourseTypeDto planCourseTypeDto = collect.get(0);
					completedCourse.setCourseLabelId(planCourseTypeDto.getLabelId());
					completedCourse.setLabelName(planCourseTypeDto.getLabelName());
					logger.info("----@@@@@@@@@@@----:" + completedCourse.getCourseCode() + ":" + planCourseTypeDto.getLabelId()+ ":" + planCourseTypeDto.getLabelName());
				}else {
					String dict = dictionaryService.query(DictTypeEnum.X_KCXZ.getType(),completedCourse.getNature());
					completedCourse.setCourseLabelId(Long.parseLong(completedCourse.getNature()));
					completedCourse.setLabelName(dict);
					logger.info("----%%%%%%%%%%%----:" + completedCourse.getCourseCode() + ":" + completedCourse.getNature()+ ":" + dict);
				}
			}
		}
        
        packageArrangementTime(takenCourses, teachClassIds);
        
        logger.info("----------takenCourses size--------: "+ takenCourses.size());
        context.setTakenCourses(takenCourses);
        
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

	/**
	 * 拼接课程的上课时间信息
	 * @param coursess
	 * @param teachClassIds
	 */
	private void packageArrangementTime(List<CompletedCourse> takenCourses, List<Long> teachClassIds) {
		if (CollectionUtil.isNotEmpty(teachClassIds)) {
            List<TimeTableMessage> list = classDao.getYjsClassTimes(teachClassIds);
            Map<Long, List<TimeTableMessage>> collect = list.stream()
                    .collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            for (CompletedCourse cours : takenCourses) {
            	logger.info("----------111111111---------:" + cours.toString());
                Long teachClassId = cours.getTeachClassId();
                if (teachClassId == null) {
                    continue;
                }
                List<TimeTableMessage> timeTableMessages = collect.get(teachClassId);
                if (CollectionUtil.isNotEmpty(timeTableMessages)) {
                    List<TimeAndRoom> timeAndRooms = new ArrayList<>();
                    for (TimeTableMessage tableMessage : timeTableMessages) {
                        TimeAndRoom time=new TimeAndRoom();
                        Integer dayOfWeek = tableMessage.getDayOfWeek();
                        Integer timeStart = tableMessage.getTimeStart();
                        Integer timeEnd = tableMessage.getTimeEnd();
                        String roomID = tableMessage.getRoomId();
                        String weekNumber = tableMessage.getWeekNum();
                        String[] str = weekNumber.split(",");
                        List<Integer> weeks = Arrays.asList(str)
                                .stream()
                                .map(Integer::parseInt)
                                .collect(Collectors.toList());
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
                        String timeStr = weekstr + " " + timeStart + "-" + timeEnd + " "
                                + weekNumStr + " ";
                        time.setTimeAndRoom(timeStr);
                        time.setRoomId(roomID);
                        timeAndRooms.add(time);
                    }
                    cours.setTimeTableList(timeAndRooms);
                }
            }
        }
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
        logger.info("it is start to loadSelectedCourses.................");
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
            List<TimeTableMessage> list = classDao.getYjsClassTimes(teachClassIds);
            Map<Long, List<TimeTableMessage>> collect = list.stream()
                    .collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            List<TeachingClassCache> teacherClass = teachingClassTeacherDao.findTeacherClass(teachClassIds);
            Map<Long, List<TeachingClassCache>> teachingMap = teacherClass.stream().collect(Collectors.groupingBy(TeachingClassCache::getTeachClassId));

            for (ElcCourseTakeVo c : courseTakes)
            {
                logger.info("==========the elcCourseTakeVo is:{}", JSONObject.toJSONString(c));
                SelectedCourse course = new SelectedCourse();
                course.setTerm(c.getTerm());
                course.setCalendarName(year);
                Long teachingClassId = c.getTeachingClassId();
                String teacherName = "";
                List<TeachingClassCache> teachingClassCaches = teachingMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                    Set<String> set = teachingClassCaches.stream().map(TeachingClassCache::getTeacherName).collect(Collectors.toSet());
                    teacherName = String.join(",", set);
                }
                course.setTeacherName(teacherName);
                course.setTeachClassMsg(teachingClassId);
                course.setNature(c.getNature());
                course.setApply(c.getApply());
                course.setCampus(c.getCampus());
                course.setChooseObj(c.getChooseObj());
                course.setCourseCode(c.getCourseCode());
                course.setCourseName(c.getCourseName());
                course.setCourseTakeType(c.getCourseTakeType());
                course.setCredits(c.getCredits());
                course.setCalendarId(c.getCalendarId());
                logger.info("------------the assessmentMode is:{}",c.getAssessmentMode());
                course.setAssessmentMode(c.getAssessmentMode());//考试/查
                course.setPublicElec(
                    c.getIsPublicCourse() == Constants.ZERO ? false : true);
                course.setTeachClassId(teachingClassId);
                course.setTeachClassCode(c.getTeachingClassCode());
                course.setTurn(c.getTurn());
                course.setFaculty(c.getFaculty());
                List<ClassTimeUnit> times = this.concatYjsTime(collect.get(teachingClassId), course);
                course.setTimes(times);
                selectedCourses.add(course);
            }
        }
    }
    
    public List<ClassTimeUnit> concatYjsTime(
            List<TimeTableMessage> timeTableMessages, SelectedCourse c)
    {
        List<ClassTimeUnit> list = new ArrayList<>(10);
        if (CollectionUtil.isNotEmpty(timeTableMessages))
        {
            String str = new StringBuilder().append(c.getTeacherName()).append(" ").append(c.getCourseName()).toString();
            for (TimeTableMessage timeTableMessage : timeTableMessages)
            {
                ClassTimeUnit ct = new ClassTimeUnit();
                ct.setTeachClassId(timeTableMessage.getTeachingClassId());
                ct.setArrangeTimeId(timeTableMessage.getTimeId());
                ct.setTimeStart(timeTableMessage.getTimeStart());
                ct.setTimeEnd(timeTableMessage.getTimeEnd());
                ct.setDayOfWeek(timeTableMessage.getDayOfWeek());
                String weekNumber = timeTableMessage.getWeekNum();
                String[] split = weekNumber.split(",");
                List<Integer> weeks = Arrays.asList(split)
                        .stream()
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
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
}
