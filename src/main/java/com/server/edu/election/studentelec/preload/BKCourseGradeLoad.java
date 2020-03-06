package com.server.edu.election.studentelec.preload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.election.controller.ElecAgentYjsController;
import com.server.edu.election.dto.TimeTableMessage;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.common.entity.SchoolCalendar;
import com.server.edu.common.entity.Teacher;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ElcCouSubsDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElectionApplyDao;
import com.server.edu.election.dao.ExemptionApplyDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dao.TeachingClassTeacherDao;
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.dto.TeacherClassTimeRoom;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.BkStudentScoreService;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.bk.CompletedCourse;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.util.WeekModeUtil;
import com.server.edu.election.vo.ElcCouSubsVo;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 查询本科学生有成绩的课程
 * 
 * 
 * @author  OuYangGuoDong
 * @version  [版本号, 2019年2月25日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Component
public class BKCourseGradeLoad extends DataProLoad<ElecContextBk>
{
    //log日志记录
    Logger logger = LoggerFactory.getLogger(BKCourseGradeLoad.class);

    @Autowired
    private StudentDao studentDao;
    
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
    private ElcCouSubsDao elcCouSubsDao;
    
    @Autowired
    private TeachingClassTeacherDao teacherDao;

    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;
    
    @Autowired
    private BkStudentScoreService bkStudentScoreService;
    
    @Override
    public int getOrder()
    {
        return 1;
    }
    
    @Override
    public String getProjectIds()
    {
        return "1";
    }
    
    @Override
    public void load(ElecContextBk context)
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
        // 学生替代课程
        ElcCouSubsDto dto = new ElcCouSubsDto();
        dto.setStudentId(studentId);
        List<ElcCouSubsVo> list = elcCouSubsDao.selectElcNoGradCouSubs(dto);

        // 加载成绩已完成和未通过的课程
        //loadScore(context, studentInfo, studentId, stu);
        loadScoreTemp(context, studentInfo, studentId, stu,list);
        
        //得到校历id
        Long calendarId = request.getCalendarId();
        //2.学生已选择课程
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        
        //选课集合
        this.loadSelectedCourses(studentId, selectedCourses, calendarId);
        
        //3.学生免修课程
        List<ElecCourse> applyRecord =
            applyDao.findApplyRecord(calendarId, studentId);
        
        Set<ElecCourse> applyForDropCourses = context.getApplyForDropCourses();
        applyForDropCourses.addAll(applyRecord);
        // 4. 非本学期的选课并且没有成功的
        
        //5. 学生选课申请课程
        Set<ElectionApply> elecApplyCourses = context.getElecApplyCourses();
        Example aExample = new Example(ElectionApply.class);
        Example.Criteria aCriteria = aExample.createCriteria();
        aCriteria.andEqualTo("studentId", studentId);
        aCriteria.andEqualTo("calendarId", calendarId);
        List<ElectionApply> electionApplys =
            electionApplyDao.selectByExample(aExample);
        elecApplyCourses.addAll(electionApplys);
        //6. 保存学生替代课程
        context.getReplaceCourses().addAll(list);
    }

    /**
     * 查询学生成绩（使用的是分表）
     * 
     * @param context
     * @param studentInfo
     * @param studentId
     * @param stu
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unused")
    private void loadScore(ElecContextBk context, StudentInfoCache studentInfo,
        String studentId, Student stu)
    {
        List<ElcCourseTakeVo> elcCourseTakeVos = elcCourseTakeDao.findCompulsory(studentId);
        Map<String, String> compulsoryMap = new HashMap<>();
        Map<String, String> labelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(elcCourseTakeVos)) {
            for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                String compulsory = elcCourseTakeVo.getCompulsory();
                String label = elcCourseTakeVo.getLabel();
                if (StringUtils.isNotBlank(compulsory)) {
                    compulsoryMap.put(elcCourseTakeVo.getCourseCode(), compulsory);
                    labelMap.put(elcCourseTakeVo.getCourseCode(), label);
                }
            }
        }
        ElecRequest request = context.getRequest();
        Long requesCalendarId = request.getCalendarId();
        List<StudentScoreVo> stuScoreBest =
            ScoreServiceInvoker.findStuScoreBest(studentId);
        BeanUtils.copyProperties(stu, studentInfo);
        String campus = studentDao.getStudentCampus(request.getCalendarId(),stu.getGrade(),stu.getProfession());
        if(StringUtils.isEmpty(campus)){
            campus = stu.getCampus();
        }
        studentInfo.setCampus(campus);

        //如果是选下学期的课程，获得当前学期正在修读的课程
        Long currentCalendarId = BaseresServiceInvoker.getCurrentCalendar();/* 当前学期学年 */
        if(currentCalendarId.longValue() < requesCalendarId.longValue() ){

        }

        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();//未完成
        if (CollectionUtil.isNotEmpty(stuScoreBest))
        {
            List<Long> teachClassIds = stuScoreBest.stream()
                .map(temp -> temp.getTeachingClassId())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            // 获取学院，教师名称
            List<TeachingClassCache> classInfo =
                courseOpenDao.findClassInfo(teachClassIds);
            //
            Map<Long, TeachingClassCache> classMap = classInfo.stream()
                .collect(Collectors.toMap(TeachingClassCache::getTeachClassId,
                    teachClass -> teachClass));
            
            Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
            for (StudentScoreVo studentScore : stuScoreBest)
            {
                Long calendarId = studentScore.getCalendarId();
                Long teachingClassId = studentScore.getTeachingClassId();
                
                CompletedCourse c = new CompletedCourse();
                TeachingClassCache lesson = new TeachingClassCache();
                lesson.setTeachClassId(teachingClassId);
                lesson.setCourseCode(studentScore.getCourseCode());
                lesson.setCourseName(studentScore.getCourseName());
                lesson.setCredits(studentScore.getCredit());
                lesson.setCalendarId(calendarId);
                lesson.setNature(studentScore.getCourseNature());
                
                TeachingClassCache tClass = classMap.get(teachingClassId);
                if (null != tClass)
                {
                    lesson.setFaculty(tClass.getFaculty());
                }

                c.setCourse(lesson);
                String label = labelMap.get(studentScore.getCourseCode());
                String compulsory = compulsoryMap.get(studentScore.getCourseCode());
                c.setCompulsory(compulsory);
                if(StringUtils.isNotEmpty(label)){
                    c.setLabel(label);
                    String labelName = elcCourseTakeDao.getLabelName(label);
                    c.setLabelName(labelName);
                }
                c.setScore(studentScore.getTotalMarkScore());
                c.setExcellent(studentScore.isBestScore());
                c.setIsPass(studentScore.getIsPass());
                c.setCourseLabelId(studentScore.getCourseLabelId());
                c.setCheat(
                    StringUtils.isBlank(studentScore.getTotalMarkScore()));
                //课程安排查询
                List<ClassTimeUnit> times = this.concatTime(collect, lesson);
                lesson.setTimes(times);
                
                if (Objects.equals(studentScore.getIsPass(), Constants.ONE))
                { // 已通过的课程
                    completedCourses.add(c);
                }
                else
                { // 未通过的课程
                    failedCourse.add(c);
                }
            }
        }
    }
    /**
     * 查询学生成绩（使用的是一张表），由于成绩没有开发完所以使用的这个，如果成绩开发完了考虑使用loadScore方法
     * 
     * @param context
     * @param studentInfo
     * @param studentId
     * @param stu
     * @see [类、类#方法、类#成员]
     */
    private void loadScoreTemp(ElecContextBk context, StudentInfoCache studentInfo,
        String studentId, Student stu,List<ElcCouSubsVo> list)
    {
        List<ElcCourseTakeVo> elcCourseTakeVos = elcCourseTakeDao.findCompulsory(studentId);
        Map<String, String> compulsoryMap = new HashMap<>();
        Map<String, String> labelMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(elcCourseTakeVos)) {
            for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                String compulsory = elcCourseTakeVo.getCompulsory();
                String label = elcCourseTakeVo.getLabel();
                if (StringUtils.isNotBlank(compulsory)) {
                    compulsoryMap.put(elcCourseTakeVo.getCourseCode(), compulsory);
                    labelMap.put(elcCourseTakeVo.getCourseCode(), label);
                }
            }
        }
//        List<ScoreStudentResultVo> stuScore = ScoreServiceInvoker.findStuScore(studentId);
        ElecRequest request = context.getRequest();
        Long requesCalendarId = request.getCalendarId();

    	StudentScoreDto dto = new StudentScoreDto();
    	dto.setStudentId(studentId);
//    	dto.setCalendarId(context.getCalendarId());
    	List<ScoreStudentResultVo> stuScore = bkStudentScoreService.getAllStudentScoreList(dto);

        //能够展示的该轮次开课课程
        List<String> openCourse = listOpenCourse(request);
        List<String> openAndSubCourse = new ArrayList<>();
        openAndSubCourse.addAll(openCourse);

        if(CollectionUtil.isNotEmpty(list) && CollectionUtil.isNotEmpty(openAndSubCourse)){
            for (ElcCouSubsVo elcCouSubsVo : list) {
                //如果包含替代课程，那么原始课程是不需要过滤掉的
                if(openAndSubCourse.contains(elcCouSubsVo.getSubCourseCode())){
                    openAndSubCourse.add(elcCouSubsVo.getOrigsCourseCode());
                }
            }
        }

        if(CollectionUtil.isNotEmpty(stuScore) && CollectionUtil.isNotEmpty(openCourse)){
            stuScore = stuScore.stream().filter(vo -> openAndSubCourse.contains(vo.getCourseCode())).collect(Collectors.toList());
        }

        BeanUtils.copyProperties(stu, studentInfo);
        String campus = studentDao.getStudentCampus(request.getCalendarId(),stu.getGrade(),stu.getProfession());
        if(StringUtils.isEmpty(campus)){
            campus = stu.getCampus();
        }
        studentInfo.setCampus(campus);

        Set<CompletedCourse> unFinishedCourses = context.getUnFinishedCourses();

        //如果是选下学期的课程，获得当前学期正在修读的课程
        Long currentCalendarId = BaseresServiceInvoker.getCurrentCalendar();/* 当前学期学年 */
        if(currentCalendarId.longValue() < requesCalendarId.longValue() ){
            List<ElcCourseTakeVo> courseTakes = elcCourseTakeDao.findBkSelectedCourses(request.getStudentId(), currentCalendarId, TableIndexUtil.getIndex(currentCalendarId));
            for (ElcCourseTakeVo courseTake : courseTakes) {
                CompletedCourse c = new CompletedCourse();
                TeachingClassCache lesson = new TeachingClassCache();
                c.setCourseCode(courseTake.getCourseCode());
                lesson.setCourseCode(courseTake.getCourseCode());
                lesson.setCourseName(courseTake.getCourseName());
                lesson.setFaculty(courseTake.getFaculty());
                lesson.setTerm(courseTake.getTerm());
                lesson.setTeachClassId(courseTake.getTeachingClassId());
                lesson.setCredits(courseTake.getCredits());
                c.setCourse(lesson);
                unFinishedCourses.add(c);
            }
        }
        Set<CompletedCourse> completedCourses = context.getCompletedCourses();
        Set<CompletedCourse> failedCourse = context.getFailedCourse();//未完成
        if (CollectionUtil.isNotEmpty(stuScore))
        {
            List<String> courseCodes = stuScore.stream().map(ScoreStudentResultVo::getCourseCode).collect(Collectors.toList());
            List<Course> courses = elcCourseTakeDao.findCourses(courseCodes);
            Map<String, Course> map = courses.stream().collect(Collectors.toMap(Course::getCode, s -> s));
            List<Long> teachClassIds = new ArrayList<>();
            List<SchoolCalendarVo> schoolCalendarVos = SchoolCalendarCacheUtil.getAll();
            for (ScoreStudentResultVo studentScore : stuScore)
            {
            	SchoolCalendar sc = new SchoolCalendar();
            	sc.setYear(studentScore.getAcademicYear().intValue());
            	sc.setTerm(studentScore.getSemester().intValue());
            	SchoolCalendarVo schoolCalendarVo = new SchoolCalendarVo();
            	if(CollectionUtil.isNotEmpty(schoolCalendarVos)) {
            		schoolCalendarVo = schoolCalendarVos.stream().filter(c->c.getYear().equals(studentScore.getAcademicYear().intValue()))
            				.filter(c->c.getTerm().equals(studentScore.getSemester().intValue())).findFirst().orElse(null);
            	}
                Long calendarId = schoolCalendarVo.getId();
                String courseCode = studentScore.getCourseCode();
                CompletedCourse c = new CompletedCourse();
                TeachingClassCache lesson = new TeachingClassCache();
                lesson.setCourseCode(courseCode);
                Course co = map.get(courseCode);
                ElcCouSubsVo elcCouSubsVo = list.stream().filter(elc->courseCode.equals(elc.getOrigsCourseCode())).findFirst().orElse(null);
                if (co != null) {
                    lesson.setCourseName(co.getName());
                    lesson.setNature(co.getNature());
                    lesson.setFaculty(co.getCollege());
                    lesson.setTerm(co.getTerm());
                }
                if(elcCouSubsVo!=null) {
                	lesson.setReplaceCourse(elcCouSubsVo.getSubCourseCode());
                    lesson.setReplaceCourseName(elcCouSubsVo.getSubCourseName());
                    lesson.setReplaceCredits(elcCouSubsVo.getSubCredits());
                }
                if(openCourse.contains(courseCode)){
                    lesson.setIsOldCourse("1");
                }else{
                    lesson.setIsOldCourse("0");
                }
                lesson.setCredits(studentScore.getCredit());
                lesson.setCalendarId(calendarId);
                if (calendarId != null) {
                	Integer index =TableIndexUtil.getIndex(calendarId);
                    List<TeachingClassCache> tcList = elcCourseTakeDao.findBkTeachClass(studentId, calendarId, courseCode,index);
                    if (CollectionUtil.isNotEmpty(tcList)) {
                        Set<String> names = tcList.stream().map(TeachingClassCache::getTeacherName).collect(Collectors.toSet());
                        lesson.setTeacherName(String.join(",", names));
                        TeachingClassCache teachingClass = tcList.get(0);
                        Long teachClassId = teachingClass.getTeachClassId();
                        lesson.setTeachClassId(teachClassId);
                        lesson.setTeachClassName(teachingClass.getTeachClassName());
                        lesson.setTeachClassCode(teachingClass.getTeachClassCode());
                        lesson.setRemark(teachingClass.getRemark());
                        teachClassIds.add(teachClassId);
                    }
                }
                String label = labelMap.get(studentScore.getCourseCode());
                String compulsory = compulsoryMap.get(studentScore.getCourseCode());
                c.setCompulsory(compulsory);
                if(StringUtils.isNotEmpty(label)){
                    c.setLabel(label);
                    String labelName = elcCourseTakeDao.getLabelName(label);
                    c.setLabelName(labelName);
                }

                c.setCourse(lesson);
                c.setScore(studentScore.getFinalScore());
                boolean excellent = false;
                if(Constants.EXCELLENT.equals(studentScore.getFinalScore())) {
                	excellent = true;
                }
                c.setExcellent(excellent);
                c.setIsPass(studentScore.getIsPass());
                c.setCourseLabelId(studentScore.getCourseLabelId());
                c.setCheat(
                    StringUtils.isNotBlank(studentScore.getCheat()));
                
                if (Objects.equals(studentScore.getIsPass(), Constants.ONE))
                { // 已通过的课程
                    completedCourses.add(c);
                }
                else
                { // 未通过的课程
                    failedCourse.add(c);
                }
            }
            
            Map<Long, List<ClassTimeUnit>> collect = groupByTime(teachClassIds);
            Consumer<CompletedCourse> consumer = new Consumer<CompletedCourse>()
            {
                @Override
                public void accept(CompletedCourse t)
                {
                  //课程安排查询
                    List<ClassTimeUnit> times = BKCourseGradeLoad.this.concatTime(collect, t.getCourse());
                    t.getCourse().setTimes(times);
                    
                }
            };
            completedCourses.forEach(consumer);
            failedCourse.forEach(consumer);
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
    	Integer index =TableIndexUtil.getIndex(calendarId);
    	//记录日志
        logger.info("----alex---it is start to findBkSelectedCourses.........................");
        List<ElcCourseTakeVo> courseTakes =
            elcCourseTakeDao.findBkSelectedCourses(studentId, calendarId,index);
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
     * 加载本学期选课申请课程数据
     * 
     * @param studentId
     * @param elecApplyCourses
     * @param calendarId
     * @see [类、类#方法、类#成员]
     */
    public void loadElecApplyCourse(String studentId,
    		Set<ElectionApply> elecApplyCourses, Long calendarId)
    {
    	Example aExample = new Example(ElectionApply.class);
        Example.Criteria aCriteria = aExample.createCriteria();
        aCriteria.andEqualTo("studentId", studentId);
        aCriteria.andEqualTo("calendarId", calendarId);
        List<ElectionApply> electionApplys =
            electionApplyDao.selectByExample(aExample);
        if(CollectionUtil.isNotEmpty(electionApplys)) {
        	elecApplyCourses = new HashSet<>(elecApplyCourses);
        }
    }
    
    /**
     * 加载本学期替代课课程数据
     * 
     * @param studentId
     * @param set
     * @see [类、类#方法、类#成员]
     */
    public void loadElcCouSubsCourse(String studentId,
    		Set<ElcCouSubsVo> set)
    {
    	ElcCouSubsDto dto = new ElcCouSubsDto();
    	dto.setStudentId(studentId);
        List<ElcCouSubsVo> elcCouSubsList =
        		elcCouSubsDao.selectElcNoGradCouSubs(dto);
        if(CollectionUtil.isNotEmpty(elcCouSubsList)) {
        	set = new HashSet<>(elcCouSubsList);
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
                    StringBuilder tip = new StringBuilder();
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
                        tip.append(String.format("[%s-%s节] %s;%s %s",
                            un.getTimeStart(),un.getTimeEnd(),weekStr,teacherNames,roomName)).append(" ");
                    }
                    Collections.sort(weeks);
                    un.setValue(sb.toString());
                    un.setWeeks(weeks);
                    un.setPopover(tip.toString());
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
                if(StringUtils.isNoneEmpty(ctu.getPopover())) {
                    String[] strings = ctu.getPopover().split(";");
                    if(strings.length == 2) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%s %s(%s) %s",
                            strings[0],
                            c.getCourseName(),
                            c.getCourseCode(),
                            strings[1]));
                        ctu.setPopover(sb.toString());
                    }
                }

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

    private List<String> listOpenCourse(ElecRequest request){
        //加载当前轮次有教学班的课程，已修课程不展示没有教学班的数据
        Long roundId = request.getRoundId();
        List<String> openCourse = elcCourseTakeDao.listTeachingCourse(roundId);
       return openCourse;
    }
}
