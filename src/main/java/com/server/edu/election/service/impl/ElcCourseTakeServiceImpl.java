package com.server.edu.election.service.impl;

import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.server.edu.election.dao.*;
import com.server.edu.election.dto.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.common.entity.StudentPlanCoure;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElcResultSwitch;
import com.server.edu.election.entity.RebuildCourseRecycle;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClass;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElecResultSwitchService;
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.studentelec.service.ElecBkService;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.service.impl.ElecYjsServiceImpl;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.CourseConflictVo;
import com.server.edu.election.vo.ElcCourseTakeNameListVo;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.election.vo.ElcStudentVo;
import com.server.edu.election.vo.StudentVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.export.ExcelExecuter;
import com.server.edu.util.excel.export.ExcelResult;
import com.server.edu.util.excel.export.ExportExcelUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ElcCourseTakeServiceImpl implements ElcCourseTakeService
{
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private ElecBkService elecBkService;
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private TeachingClassDao classDao;
    
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private ElectionConstantsDao constantsDao;
    
    @Autowired
    private StudentDao studentDao;

    @Autowired
    private RetakeCourseCountDao retakeCourseCountDao;

    @Autowired
    private TeachingClassDao teachingClassDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;

    @Autowired
    private ElecResultSwitchService elecResultSwitchService;
    
    @Autowired
    private ElecYjsServiceImpl elecYjsServiceImpl;

    @Autowired
    private DictionaryService dictionaryService;

	@Autowired
	private RebuildCourseRecycleDao rebuildCourseRecycleDao;

    @Value("${cache.directory}")
    private String cacheDirectory;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private TeachClassCacheService teachClassCacheService;

    @Autowired
    private ElectionApplyService electionApplyService;
    
    @Autowired
    private SqlSessionFactory factory;
    
    @Autowired
    private StringRedisTemplate strTemplate;

    @Autowired
    private ElecRoundsDao roundsDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    @Override
    public PageResult<ElcCourseTakeVo> listPage(
        PageCondition<ElcCourseTakeQuery> page)
    {
        ElcCourseTakeQuery cond = page.getCondition();
        // 1体育课
        if (Objects.equals(cond.getCourseType(), ElcCourseTakeQuery.PE_COURSE_TYPE))
        {
            cond.setCourseFaculty("000293");
        }
        else if (Objects.equals(cond.getCourseType(), ElcCourseTakeQuery.EN_COURSE_TYPE))
        {// 2英语课
            cond.setCourseFaculty("000268");
        }
        Session session = SessionUtils.getCurrentSession();

        //针对选课结果 中通过教学班要查看所有学生，所以 teachingClassId == null ,是上课名单数据，取学院分权数据，否则取所有
        if(cond.getTeachingClassId() == null){
            //走的非体育或英语上课名单
            if (StringUtils.isBlank(cond.getCourseFaculty())) {
                List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
                if(CollectionUtil.isNotEmpty(deptIds)){
                    if(deptIds.contains("000293") || deptIds.contains("000268")){
                        cond.setCourseFacultys(deptIds);
                    }else{
                        cond.setFaculties(deptIds);
                    }
                }
            }
        }
        PageHelper.startPage(page.getPageNum_() ,page.getPageSize_());
        cond.setIndex(TableIndexUtil.getIndex(cond.getCalendarId()));
        Page<ElcCourseTakeVo> listPage = courseTakeDao.listPage(cond);
        List<String> stds = new ArrayList<>();
        if(cond.getIsLimit() != null && cond.getIsLimit().intValue() == 1 && cond.getTeachingClassId() != null){
            StudentVo stu = new StudentVo();
            stu.setManagerDeptId("1");
            List<Student> elcAffinityCoursesStds = studentDao.selectElcInvincibleStds(stu);
            if (CollectionUtil.isNotEmpty(elcAffinityCoursesStds)){
                stds =  elcAffinityCoursesStds.stream().map(Student::getStudentCode).collect(Collectors.toList()) ;
            }
        }
        for (ElcCourseTakeVo elcCourseTakeVo : listPage) {
			if (elcCourseTakeVo.getChooseObj().intValue() == 1) {
				elcCourseTakeVo.setElectionMode(1);
			}else{
				elcCourseTakeVo.setElectionMode(2);
			}
			if(CollectionUtil.isNotEmpty(stds) && stds.contains(elcCourseTakeVo.getStudentId())){
                elcCourseTakeVo.setAffinityStds(Constants.ONE);
            }

		}
        if(CollectionUtil.isNotEmpty(listPage)) {
            for(ElcCourseTakeVo vo: listPage) {
                // 处理教学安排（上课时间地点）信息
                List<TimeAndRoom> tableMessages = getTimeById(vo.getTeachingClassId());
                vo.setTimeTableList(tableMessages);
            }
        }
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
    }
    
    
    @Override
    public PageResult<ElcCourseTakeVo> honorPage(
        PageCondition<ElcCourseTakeQuery> page)
    {
        ElcCourseTakeQuery cond = page.getCondition();
        Session session = SessionUtils.getCurrentSession();
        if (StringUtils.equals(session.getCurrentRole(), "1")) {
            if (StringUtils.isBlank(cond.getFaculty())) {
                List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
                cond.setFaculties(deptIds);
            }
        }
        PageHelper.startPage(page.getPageNum_() ,page.getPageSize_());
        cond.setIndex(TableIndexUtil.getIndex(cond.getCalendarId()));
        Page<ElcCourseTakeVo> listPage = courseTakeDao.honorPage(cond);
        List<String> stds = new ArrayList<>();
        for (ElcCourseTakeVo elcCourseTakeVo : listPage) {
			if (elcCourseTakeVo.getChooseObj().intValue() == 1) {
				elcCourseTakeVo.setElectionMode(1);
			}else{
				elcCourseTakeVo.setElectionMode(2);
			}
			if(CollectionUtil.isNotEmpty(stds) && stds.contains(elcCourseTakeVo.getStudentId())){
                elcCourseTakeVo.setAffinityStds(Constants.ONE);
            }

		}
        if(CollectionUtil.isNotEmpty(listPage)) {
            for(ElcCourseTakeVo vo: listPage) {
                // 处理教学安排（上课时间地点）信息
                List<TimeAndRoom> tableMessages = getTimeById(vo.getTeachingClassId());
                vo.setTimeTableList(tableMessages);
            }
        }
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
    }


    private List<TimeAndRoom>  getTimeById(Long teachingClassId){
        List<TimeAndRoom> list=new ArrayList<>();
        List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoomStr(teachingClassId);
        if(CollectionUtil.isNotEmpty(classTimeAndRoom)){
            for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
                TimeAndRoom time=new TimeAndRoom();
                Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                Integer timeStart = classTeacherDto.getTimeStart();
                Integer timeEnd = classTeacherDto.getTimeEnd();
                String roomID = classTeacherDto.getRoomID();
                String weekNumber = classTeacherDto.getWeekNumberStr();
                Long timeId = classTeacherDto.getTimeId();
                String[] str = weekNumber.split(",");

                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+" "+weekNumStr+" ";
                time.setTimeId(timeId);
                time.setTimeAndRoom(timeStr);
                time.setRoomId(roomID);
                list.add(time);
            }
        }
        return list;
    }

    /**
     *  @Description: 星期
     */
    private String findWeek(Integer number){
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

    @Override
    public PageResult<ElcCourseTakeVo> graduatePage(
            PageCondition<ElcCourseTakeQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<ElcCourseTakeVo> elcCourseTakeVos = courseTakeDao.graduatePage(page.getCondition());
        setTeachingArrange(elcCourseTakeVos);
        PageResult<ElcCourseTakeVo> result = new PageResult<>(elcCourseTakeVos);
        return result;
    }

    @Override
    public List<ElcCourseTakeVo> getExportGraduatePage(
            List<Long> ids)
    {
        List<ElcCourseTakeVo> elcCourseTakeVos = courseTakeDao.getExportGraduatePage(ids);
        setTeachingArrange(elcCourseTakeVos);
        return elcCourseTakeVos;
    }

    @Override
    public PageResult<ElcCourseTakeVo> allSelectedCourse(PageCondition<Student> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Student student = condition.getCondition();
        Page<ElcCourseTakeVo> listPage = courseTakeDao.allSelectedCourse(student.getStudentCode());
        setTeachingArrange(listPage);
        return new PageResult<>(listPage);
    }

    @Transactional
    @Override
    public String add(ElcCourseTakeAddDto add)
    {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        Integer mode = add.getMode();
        List<TeachingClass> teachingClasses = teachingClassDao.findTeachingClasses(teachingClassIds);
        if (teachingClasses.size() != teachingClassIds.size()) {
            throw new ParameterValidateException("教学班不存在");
        }
        for (String studentId : studentIds)
        {
            for (int i = 0; i < teachingClassIds.size(); i++)
            {
                Long teachingClassId = teachingClassIds.get(i);
                ElcCourseTakeVo vo = courseTakeDao
                        .getTeachingClassInfo(calendarId, teachingClassId, null);
                if (null != vo && vo.getCourseCode() != null)
                {
                    //mode为空 需要判断学生是哪个类型（选课结果处理时学生可以是任意类型的学生 结业 也可以 mode 就是3）
                    if(mode == null){
                        StudentDto studentRoundType = roundsDao.findStudentRoundType(studentId);
                        if(studentRoundType != null){
                            //结业生
                            if(StringUtils.isNotBlank(studentRoundType.getGraduateStudent())){
                                mode = Constants.THREE_MODE;
                            }else if(StringUtils.isNotBlank(studentRoundType.getInternationalGraduates())){
                                mode = Constants.FOUR_MODE;
                            }else{
                                mode = Constants.FIRST;
                            }
                        }
                    }
                    addTake(date, calendarId, studentId, vo, mode);
                }
                else
                {
                    String code = teachingClassId.toString();
                    if (vo != null)
                    {
                        code = vo.getTeachingClassCode();
                    }
                    sb.append("教学班[" + code + "]对应的课程不存在,");
                }
            }
        }

        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }

    @Transactional
    @Override
    public String addCourseBk(ElcCourseTakeAddDto add)
    {
        Integer status = add.getStatus();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        List<TeachingClass> teachingClasses = teachingClassDao.findTeachingClasses(teachingClassIds);
        List<String> stus = new ArrayList<>();
        List<String> courses = new ArrayList();
        if (teachingClasses.size() != teachingClassIds.size()) {
            throw new ParameterValidateException("教学班不存在");
        }
        // 教学班容量与上课时间冲突校验
        if (status == null || status != 1) {
            int size = studentIds.size();
            // 教学班容量校验
            List<String> codes = new ArrayList<>(teachingClassIds.size());
            for (TeachingClass teachingClass : teachingClasses) {
                if (teachingClass.getElcNumber() + teachingClass.getReserveNumber() + size> teachingClass.getNumber()) {
                    codes.add(teachingClass.getCode());
                }
            }
            StringBuilder sb = new StringBuilder();
            if (CollectionUtil.isNotEmpty(codes)) {
                sb.append("教学班").append(String.join(",",codes)).append("容量超出上限,");
            }
            // 要添加课程的上课时间查询
            List<TimeTableMessage> courseArrangeBk = courseTakeDao.findCourseArrangeBk(teachingClassIds);
            // 上课时间是否冲突校验
            if (CollectionUtil.isNotEmpty(courseArrangeBk)) {
                List<String> list = new ArrayList<>(size);
                loop:for (String studentId : studentIds)
                {
                    ElecContextBk context = new ElecContextBk(studentId, calendarId);
                    Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
                    List<ClassTimeUnit> classTimeUnits = new ArrayList<>(20);
                    if (CollectionUtil.isNotEmpty(selectedCourses)) {
                        for (SelectedCourse selectedCours : selectedCourses) {
                            List<ClassTimeUnit> times = selectedCours.getCourse().getTimes();
                            classTimeUnits.addAll(times);
                        }
                    }
                    for (TimeTableMessage timeTableMessage : courseArrangeBk) {
                        String weekNum = timeTableMessage.getWeekNum();
                        String[] split = weekNum.split(",");
                        Set<String> set = new HashSet<>(Arrays.asList(split));
                        Integer dayOfWeek = timeTableMessage.getDayOfWeek();
                        Integer timeStart = timeTableMessage.getTimeStart();
                        Integer timeEnd = timeTableMessage.getTimeEnd();
                        int size1 = set.size();
                        for (ClassTimeUnit classTimeUnit : classTimeUnits) {
                            List<Integer> weeks = classTimeUnit.getWeeks();
                            Set<String> weekStu = weeks.stream().map(s -> String.valueOf(s)).collect(Collectors.toSet());
                            int size2 = weekStu.size();
                            int size3 = size1 + size2;
                            Set<String> all = new HashSet<>();
                            all.addAll(set);
                            all.addAll(weekStu);
                            // 上课周冲突
                            if (size3 > all.size() ) {
                                // 判断上课天是否一样
                                if (dayOfWeek.intValue() == classTimeUnit.getDayOfWeek()) {
                                    // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                                    int start = classTimeUnit.getTimeStart();
                                    int end = classTimeUnit.getTimeEnd();
                                    if ( (start <= timeStart && timeStart <= end)
                                            || (start <= timeEnd && timeEnd <= end)
                                            || (timeStart <= start && start <= timeEnd)
                                            || (timeStart <= end && end <= timeEnd)) {
                                        list.add(studentId);
                                        continue loop;
                                    }
                                }
                            }
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(list)) {
                    sb.append("要添加的课程上课时间与学生").append(String.join(",", list)).append("已选课程上课时间冲突，");
                }
            }
            if (sb.length() > 0)
            {
//            return sb.substring(0, sb.length() - 1);
                return sb.append("您确定要添加吗？").toString();
            }
        }
        //加课
        Date date = new Date();
        Integer mode = add.getMode();
        for (String studentId : studentIds) {
            for (int i = 0; i < teachingClassIds.size(); i++)
            {
                Long teachingClassId = teachingClassIds.get(i);
                ElcCourseTakeVo vo = courseTakeDao
                        .getTeachingClassInfo(calendarId, teachingClassId, null);
                if (null != vo && vo.getCourseCode() != null)
                {
                    addTakeNew(date, calendarId, studentId, vo, mode, stus, courses, studentIds, teachingClassIds);
                }
                else
                {
                    String code = teachingClassId.toString();
                    if (vo != null)
                    {
                        code = vo.getTeachingClassCode();
                    }
                    throw new ParameterValidateException("教学班[" + code + "]对应的课程不存在,");
//                    sb.append(");
                }
            }
        }
        if(CollectionUtil.isNotEmpty(stus)){
            return StringUtils.join(stus.toArray(),",")+"已经选取了这门课程";

        }else if(CollectionUtil.isNotEmpty(courses)){
            return StringUtils.join(courses.toArray(),",")+"这些课程为重复添加操作";
        }
        return StringUtils.EMPTY;
    }

    @Transactional
    private void addTakeNew(Date date, Long calendarId, String studentId,
                         ElcCourseTakeVo vo, Integer mode,List<String> stus,List<String> courses,List<String> students,List<Long> teachClassIds)
    {
        String courseCode = vo.getCourseCode();
        Long teachingClassId = vo.getTeachingClassId();
        String courseName = vo.getCourseName();
        String teachingClassCode = vo.getTeachingClassCode();

        // 查询当前学期是否选过这门课程
        int count = courseTakeDao.findIsEletionCourse(studentId, calendarId, courseCode);

        if (count == 0)
        {
            ElcCourseTake record = new ElcCourseTake();
            record.setStudentId(studentId);
            record.setCourseCode(courseCode);
            int selectCount = courseTakeDao.selectCount(record);
            ElcCourseTake take = new ElcCourseTake();
            // 判断是否是第一次上该课程
            if (selectCount == 0) {
                take.setCourseTakeType(CourseTakeType.NORMAL.type());
            } else {
                take.setCourseTakeType(CourseTakeType.RETAKE.type());
            }
            take.setCalendarId(calendarId);
            take.setChooseObj(ChooseObj.ADMIN.type());
            take.setCourseCode(courseCode);
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachingClassId);
            take.setMode(mode);
            take.setTurn(0);
            courseTakeDao.insertSelective(take);
            // 增加选课人数
            classDao.increElcNumber(teachingClassId);
            // 添加选课日志
            ElcLog log = new ElcLog();
            log.setCalendarId(calendarId);
            log.setCourseCode(courseCode);
            log.setCourseName(courseName);
            Session currentSession = SessionUtils.getCurrentSession();
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(date);
            log.setCreateIp(currentSession.getIp());
            log.setMode(ElcLogVo.MODE_2);
            log.setStudentId(studentId);
            log.setTeachingClassCode(teachingClassCode);
            log.setTurn(0);
            log.setType(ElcLogVo.TYPE_1);
            this.elcLogDao.insertSelective(log);
            // 更新缓存中教学班人数
            teachClassCacheService.updateTeachingClassNumber(teachingClassId);
            //ElecContextUtil.updateSelectedCourse(calendarId, studentId);
            applicationContext
                    .publishEvent(new ElectLoadEvent(calendarId, studentId));
            Student stu  = studentDao.findStudentByCode(studentId);
//            try {
//                elecBkService.syncRemindTime(calendarId,1,studentId,stu.getName(),courseName+"("+courseCode+")");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            String elecStatus = Constants.IS_ELEC;
            //更新培养的选课状态
            StudentPlanCoure studentPlanCoure = new StudentPlanCoure();
            studentPlanCoure.setStudentId(studentId);
            studentPlanCoure.setCourseCode(courseCode);
            studentPlanCoure.setElecStatus(elecStatus);
            try {
                CultureSerivceInvoker.updateElecStatus(studentPlanCoure);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else {
            if(students.size() > 1 && teachClassIds.size() == 1){
                stus.add(studentId);
            }else if (teachClassIds.size() > 1 && students.size() == 1){
                courses.add(courseCode);
            }else{
                courses.add(courseCode);
            }
        }
    }
    @Transactional
    private void addTake(Date date, Long calendarId, String studentId,
        ElcCourseTakeVo vo, Integer mode)
    {
        String courseCode = vo.getCourseCode();
        Long teachingClassId = vo.getTeachingClassId();
        String courseName = vo.getCourseName();
        String teachingClassCode = vo.getTeachingClassCode();
        // 查询当前学期是否选过这门课程
        int count = courseTakeDao.findIsEletionCourse(studentId, calendarId, courseCode);
        if (count == 0)
        {
            ElcCourseTake record = new ElcCourseTake();
            record.setStudentId(studentId);
            record.setCourseCode(courseCode);
            int selectCount = courseTakeDao.selectCount(record);
            ElcCourseTake take = new ElcCourseTake();
            // 判断是否是第一次上该课程
            if (selectCount == 0) {
                take.setCourseTakeType(CourseTakeType.NORMAL.type());
            } else {
                take.setCourseTakeType(CourseTakeType.RETAKE.type());
            }
            take.setCalendarId(calendarId);
            take.setChooseObj(ChooseObj.ADMIN.type());
            take.setCourseCode(courseCode);
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachingClassId);
            take.setMode(mode);
            take.setTurn(0);
            courseTakeDao.insertSelective(take);
            // 增加选课人数
            classDao.increElcNumber(teachingClassId);
            // 添加选课日志
            ElcLog log = new ElcLog();
            log.setCalendarId(calendarId);
            log.setCourseCode(courseCode);
            log.setCourseName(courseName);
            Session currentSession = SessionUtils.getCurrentSession();
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(date);
            log.setCreateIp(currentSession.getIp());
            log.setMode(ElcLogVo.MODE_2);
            log.setStudentId(studentId);
            log.setTeachingClassCode(teachingClassCode);
            log.setTurn(0);
            log.setType(ElcLogVo.TYPE_1);
            this.elcLogDao.insertSelective(log);
            // 更新缓存中教学班人数
            teachClassCacheService.updateTeachingClassNumber(teachingClassId);
            //ElecContextUtil.updateSelectedCourse(calendarId, studentId);
            applicationContext
                .publishEvent(new ElectLoadEvent(calendarId, studentId));
            Student stu  = studentDao.findStudentByCode(studentId);
//            try{
//                elecBkService.syncRemindTime(calendarId,1,studentId,stu.getName(),courseName+"("+courseCode+")");
//            }catch (Exception e){
//                e.printStackTrace();
//            }
            String elecStatus = Constants.IS_ELEC;
            //更新培养的选课状态
            StudentPlanCoure studentPlanCoure = new StudentPlanCoure();
            studentPlanCoure.setStudentId(studentId);
            studentPlanCoure.setCourseCode(courseCode);
            studentPlanCoure.setElecStatus(elecStatus);
            try {
                CultureSerivceInvoker.updateElecStatus(studentPlanCoure);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Transactional
    @Override
    public String addByExcel(Long calendarId, List<ElcCourseTakeAddDto> datas,
        Integer mode)
    {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        for (ElcCourseTakeAddDto data : datas)
        {
            String studentId = StringUtils.trim(data.getStudentId());
            String teachingClassCode =
                StringUtils.trim(data.getTeachingClassCode());
            
            if (StringUtils.isNotBlank(studentId)
                && StringUtils.isNotBlank(teachingClassCode))
            {
                ElcCourseTakeVo vo = this.courseTakeDao
                    .getTeachingClassInfo(calendarId, null, teachingClassCode);
                
                if (null != vo && vo.getCourseCode() != null)
                {
                    addTake(date, calendarId, studentId, vo, mode);
                }
                else
                {
                    sb.append("教学班[" + teachingClassCode + "]对应的课程不存在,");
                }
                
            }
        }
        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }
    

//	@Override
//	public String graduateAdd(ElcCourseTakeAddDto value, String currentRole, boolean adminFlag, String projId) {
//		Date date = new Date();
//		if (CollectionUtil.isEmpty(value.getStudentIds())) {
//			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.noStudent",I18nUtil.getMsg("elecResultSwitch.noStudent")));
//		}
//    	//如果当前操作人是老师
//        if ("1".equals(currentRole)&&!adminFlag)
//        {
//        	//判断选课结果开关状态
//    		ElcResultSwitch elcResultSwitch = elecResultSwitchService.getSwitch(value.getCalendarId(),projId);
//    		if (elcResultSwitch.getStatus() == Constants.ZERO) {
//    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled",I18nUtil.getMsg("elecResultSwitch.notEnabled")));
//    		} else if(date.getTime() > elcResultSwitch.getOpenTimeEnd().getTime() ||  date.getTime() < elcResultSwitch.getOpenTimeStart().getTime()){
//    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.operationalerror",I18nUtil.getMsg("elecResultSwitch.operationalerror")));
//    		}
//
//    		//判断课程性质
//
//    		Example example = new Example(Course.class);
//    		Criteria createCriteria = example.createCriteria();
//    		createCriteria.andEqualTo("code",value.getCourseCode());
//    		Course course = courseDao.selectOneByExample(example);
//    		if(course.getIsElective().intValue() == Constants.ONE){
//    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.noPower",I18nUtil.getMsg("elecResultSwitch.noPower")));
//    		}
//
//        }
//        return this.graduateAddCourse(value);
//	}

    @Override
    public String graduateAdd(ElcCourseTakeAddDto value) {
        Integer status = value.getStatus();

        // 获取教学班
        List<Long> teachingClassIds = value.getTeachingClassIds();
        Long teachingClassId = teachingClassIds.get(0);
        TeachingClass teachingClass =
                teachingClassDao.selectByPrimaryKey(teachingClassId);
        List<TimeTableMessage> courseArrange =
                courseTakeDao.findCourseArrange(teachingClassIds);
        // 获取要加课的学生
        List<String> studentIds = value.getStudentIds();
        Long calendarId = value.getCalendarId();

        // 0或者null第一次进来，需校验规则，status = 1，强制添加
        if (status == null || status == 0) {
            // 不管是管理员还是教务员，都不能跨校区加课
            // 判断学生校区和课程校区
            Example stuExample = new Example(Student.class);
            stuExample.createCriteria()
                    .andIn("studentCode", studentIds);
            List<Student> students = studentDao.selectByExample(stuExample);
            String campus = teachingClass.getCampus();

            List<String> list = new ArrayList<>(studentIds.size());
            for (Student student : students) {
                if (!campus.equals(student.getCampus())) {
                    list.add(student.getStudentCode());
                }
            }

            if (CollectionUtil.isNotEmpty(list)) {
                throw new ParameterValidateException("学生"
                        + String.join(",", list) +
                        "所在校区与课程校区不一致，请重新添加");
            }

            Session session = SessionUtils.getCurrentSession();
            boolean isAdmin = StringUtils.equals(session.getCurrentRole(), "1")
                    && session.isAdmin();

            if (isAdmin) {
                StringBuffer sb = new StringBuffer("当前课程");
                if (teachingClass.getElcNumber() + studentIds.size() > teachingClass.getNumber()) {
                    sb.append("已达教室容量上限,");
                }
                Set<String> set = conflictStu(courseArrange, studentIds, calendarId);
                if (CollectionUtil.isNotEmpty(set)) {
                    sb.append("与学生").append(String.join(",", set)).
                            append("已选课程上课时间冲突，");
                }
                String msg = sb.toString();
                if (!"".equals(msg)) {
                    msg = msg + "您确定要添加吗？";
                    return msg;

                }
            } else {
                //判断选课结果开关状态
                boolean switchStatus = elecResultSwitchService.getSwitchStatus(calendarId, session.getCurrentManageDptId());
                // 教务员需判断选课开关是否开启
                if (!switchStatus) {
                    throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled"));
                }
                // 判断教学班容量
                if (teachingClass.getElcNumber() + studentIds.size() > teachingClass.getNumber()) {
                    throw new ParameterValidateException("已达教室容量上限");
                }
                // 判断上课时间冲突
                Set<String> set = conflictStu(courseArrange, studentIds, calendarId);
                if (CollectionUtil.isNotEmpty(set)) {
                    throw new ParameterValidateException("当前课程与学生"
                            + String.join(",", set) +
                            "已选课程上课时间冲突，请重新添加");
                }
            }
            return graduateAddCourse(value);
        } else {
            return graduateAddCourse(value);
        }
    }

    private Set<String> conflictStu(List<TimeTableMessage> courseArrange,
                                    List<String> studentIds, Long calendarId)
    {
        Set<String> set = new HashSet<>(20);
        if (CollectionUtil.isNotEmpty(courseArrange)) {
            // 获取学生当前学期选课记录
            List<ElcCourseTakeVo> elcCourseTakes =
                    courseTakeDao.findElcCourseTakes(studentIds, calendarId);
            // 获取教学班id集合

            Set<Long> collect = elcCourseTakes.stream().
                    filter(s -> s.getTeachingClassId() != null).
                    map(ElcCourseTakeVo::getTeachingClassId).
                    collect(Collectors.toSet());

            if (CollectionUtil.isNotEmpty(collect)) {
                // 查询教学班课程安排
                List<TimeTableMessage> courseArranges =
                        courseTakeDao.findCourseArrange(new ArrayList<>(collect));

                // 获取与当前教学班上课时间冲突的教学班
                Set<Long> ids = new HashSet<>(10);
                for (TimeTableMessage timeTableMessage : courseArrange) {
                    String[] split = timeTableMessage.getWeekNum().split(",");
                    Set<String> addWeeks = new HashSet<>(Arrays.asList(split));
                    int dayOfWeek = timeTableMessage.getDayOfWeek().intValue();
                    int timeStart = timeTableMessage.getTimeStart().intValue();
                    int timeEnd = timeTableMessage.getTimeEnd().intValue();
                    int addSize = addWeeks.size();
                    for (TimeTableMessage arrange : courseArranges) {
                        String[] week = arrange.getWeekNum().split(",");
                        Set<String> selectWeeks = new HashSet<>(Arrays.asList(week));
                        int selectSize = selectWeeks.size();
                        Set<String> weeks = new HashSet<>(addSize + selectSize);
                        weeks.addAll(addWeeks);
                        weeks.addAll(selectWeeks);
                        // 判断上课周是否冲突
                        if (addSize + selectSize > weeks.size()) {
                            //上课周冲突，判断上课天
                            if (dayOfWeek == arrange.getDayOfWeek().intValue()) {
                                // 上课天相同，比价上课节次
                                int start = arrange.getTimeStart().intValue();
                                int end = arrange.getTimeEnd().intValue();
                                // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                                if ( (start <= timeStart && timeStart <= end)
                                        || (start <= timeEnd && timeEnd <= end)
                                        || (timeStart <= start && start <= timeEnd)
                                        || (timeStart <= end && end <= timeEnd)) {
                                    ids.add(arrange.getTeachingClassId());
                                }
                            }
                        }
                    }
                }
                if (CollectionUtil.isNotEmpty(ids)) {
                    for (ElcCourseTakeVo elcCourseTake : elcCourseTakes) {
                        Long teachingClassId = elcCourseTake.getTeachingClassId();
                        if (teachingClassId != null && ids.contains(teachingClassId)) {
                            set.add(elcCourseTake.getStudentId());
                        }
                    }
                }
            }
        }
        return set;
    }


    @Transactional
    private String graduateAddCourse(ElcCourseTakeAddDto add)
    {
        Date date = new Date();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        Integer mode = add.getMode();
        for (String studentId : studentIds)
        {
            List<ElcStudentVo> courseInfo = courseTakeDao.findCourseInfo(add.getTeachingClassIds());
            ElcStudentVo vo = courseInfo.get(0);
            String courseCode = vo.getCourseCode();
            Long teachingClassId = vo.getTeachingClassId();
            String courseName = vo.getCourseName();
            String teachingClassCode = vo.getClassCode();

            ElcCourseTake record = new ElcCourseTake();
            record.setStudentId(studentId);
            record.setCourseCode(courseCode);
            record.setCalendarId(calendarId);
            int selectCount = courseTakeDao.selectCount(record);
            if (selectCount == 0)
            {
                ElcCourseTake take = new ElcCourseTake();
                take.setCalendarId(calendarId);
                take.setChooseObj(ChooseObj.ADMIN.type());
                take.setCourseCode(courseCode);
                take.setCourseTakeType(CourseTakeType.NORMAL.type());
                take.setCreatedAt(date);
                take.setStudentId(studentId);
                take.setTeachingClassId(teachingClassId);
                take.setMode(mode);
                take.setTurn(0);
                courseTakeDao.insertSelective(take);
                try {
                    elecYjsServiceImpl.updateSelectCourse(studentId,courseCode,ElectRuleType.ELECTION);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 增加选课人数
                classDao.increElcNumber(teachingClassId);
                // 添加选课日志
                ElcLog log = new ElcLog();
                log.setCalendarId(calendarId);
                log.setCourseCode(courseCode);
                log.setCourseName(courseName);
                Session currentSession = SessionUtils.getCurrentSession();
                log.setCreateBy(currentSession.getUid());
                log.setCreatedAt(date);
                log.setCreateIp(currentSession.getIp());
                log.setMode(ElcLogVo.MODE_2);
                log.setStudentId(studentId);
                log.setTeachingClassCode(teachingClassCode);
                log.setTurn(0);
                log.setType(ElcLogVo.TYPE_1);
                this.elcLogDao.insertSelective(log);

                applicationContext
                        .publishEvent(new ElectLoadEvent(calendarId, studentId));
            }
        }
        return "";
    }
    
    @Transactional
    private void graduateAddCourseTake(Date date, Long calendarId, String studentId,
        ElcCourseTakeVo vo, Integer mode)
    {
        String courseCode = vo.getCourseCode();
        Long teachingClassId = vo.getTeachingClassId();
        String courseName = vo.getCourseName();
        String teachingClassCode = vo.getTeachingClassCode();
        
        ElcCourseTake record = new ElcCourseTake();
        record.setStudentId(studentId);
        record.setCourseCode(courseCode);
        record.setCalendarId(calendarId);
        int selectCount = courseTakeDao.selectCount(record);
        if (selectCount == 0)
        {
            ElcCourseTake take = new ElcCourseTake();
            take.setCalendarId(calendarId);
            take.setChooseObj(ChooseObj.ADMIN.type());
            take.setCourseCode(courseCode);
            int count = courseTakeDao.courseCount(courseCode, studentId);
            if (count > 0) {
            	take.setCourseTakeType(CourseTakeType.RETAKE.type());
			}else{
				take.setCourseTakeType(CourseTakeType.NORMAL.type());
			}
            
            take.setCreatedAt(date);
            take.setStudentId(studentId);
            take.setTeachingClassId(teachingClassId);
            take.setMode(mode);
            take.setTurn(0);
            courseTakeDao.insertSelective(take);
            
            
            try {
            	elecYjsServiceImpl.updateSelectCourse(studentId,courseCode,ElectRuleType.ELECTION);
			} catch (Exception e) {
				e.printStackTrace();
			}
            // 增加选课人数
            classDao.increElcNumber(teachingClassId);
            // 添加选课日志
            ElcLog log = new ElcLog();
            log.setCalendarId(calendarId);
            log.setCourseCode(courseCode);
            log.setCourseName(courseName);
            Session currentSession = SessionUtils.getCurrentSession();
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(date);
            log.setCreateIp(currentSession.getIp());
            log.setMode(ElcLogVo.MODE_2);
            log.setStudentId(studentId);
            log.setTeachingClassCode(teachingClassCode);
            log.setTurn(0);
            log.setType(ElcLogVo.TYPE_1);
            this.elcLogDao.insertSelective(log);
            
            applicationContext
                .publishEvent(new ElectLoadEvent(calendarId, studentId));
        }
    }

    
    @Transactional
    @Override
    public void withdraw(List<ElcCourseTake> value)
    {
    	Session currentSession = SessionUtils.getCurrentSession();
        boolean admin = currentSession.isAdmin();
        Map<String, ElcCourseTakeVo> classInfoMap = new HashMap<>();
        List<ElcLog> logList = new ArrayList<>();
        Map<String, ElcCourseTake> withdrawMap = new HashMap<>();
        for (ElcCourseTake take : value)
        {
            Long calendarId = take.getCalendarId();
            String studentId = take.getStudentId();
            Long teachingClassId = take.getTeachingClassId();
            Integer turn = take.getTurn()!=null?take.getTurn():0;
            //删除选课记录
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria()
                .andEqualTo("calendarId", calendarId)
                .andEqualTo("studentId", studentId)
                .andEqualTo("teachingClassId", teachingClassId);
            ElcCourseTake elcCourseTake = courseTakeDao.selectOneByExample(example);
            this.canWithdrawByFee(elcCourseTake,admin);
            //更新选课申请数据
            electionApplyService
                    .update(studentId, calendarId, elcCourseTake.getCourseCode(),ElectRuleType.WITHDRAW);
            Example example1 = new Example(Course.class);
            example1.createCriteria().andEqualTo("code",elcCourseTake.getCourseCode());
            Course course = courseDao.selectOneByExample(example1);
            courseTakeDao.deleteByExample(example);
            Integer paid = take.getPaid();
        	RebuildCourseRecycle rebuildCourseRecycle = new RebuildCourseRecycle();
        	rebuildCourseRecycle.setCalendarId(calendarId);
        	rebuildCourseRecycle.setStudentCode(studentId);
        	rebuildCourseRecycle.setCourseCode(elcCourseTake.getCourseCode());
        	rebuildCourseRecycle.setTeachingClassId(elcCourseTake.getTeachingClassId());
        	rebuildCourseRecycle.setCourseTakeType(elcCourseTake.getCourseTakeType());
        	//取选课对象（不是退课对象）
        	rebuildCourseRecycle.setChooseObj(elcCourseTake.getChooseObj());
        	rebuildCourseRecycle.setTurn(turn);
        	//取选课模式
        	rebuildCourseRecycle.setMode(elcCourseTake.getMode());
        	rebuildCourseRecycle.setPaid(elcCourseTake.getPaid());
        	rebuildCourseRecycle.setType(Constants.FIRST);
        	rebuildCourseRecycle.setScreenLabel(null);
            //结业生、留学结业生退课进入回收站，正常学生重修进入回收站
        	if(paid !=null && Constants.FIRST.equals(paid)) {
        		StudentDto studentDto = elecRoundsDao.findStudentRoundType(studentId);
                if(StringUtils.isNotEmpty(studentDto.getGraduateStudent()) || StringUtils.isNotEmpty(studentDto.getInternationalGraduates())) {
                	rebuildCourseRecycleDao.insertSelective(rebuildCourseRecycle);
                }else {
                    if(Constants.SECOND.equals(elcCourseTake.getCourseTakeType())) {
                    	rebuildCourseRecycleDao.insertSelective(rebuildCourseRecycle);
                    }
                }
        	}
            //减少选课人数
            int count =classDao.decrElcNumber(teachingClassId);
            //保存第三、四轮退课人数
            if (turn == Constants.THIRD_TURN
                    || turn == Constants.FOURTH_TURN)
            {
            	 count= classDao.increDrawNumber(teachingClassId);
            	 if (count > 0)
                 {
                     dataProvider.incrementDrawNumber(teachingClassId);
                 }
            }
            Student stu  = studentDao.findStudentByCode(studentId);
            // 更新缓存中教学班人数
            teachClassCacheService.updateTeachingClassNumber(teachingClassId);
            ElcCourseTakeVo vo = null;
            String key = calendarId + "-" + teachingClassId;
            if (!classInfoMap.containsKey(key))
            {
                vo = this.courseTakeDao
                    .getTeachingClassInfo(calendarId, teachingClassId, null);
                classInfoMap.put(key, vo);
            }
            else
            {
                vo = classInfoMap.get(key);
            }
            //ElecContextUtil.updateSelectedCourse(calendarId, studentId);
            // 记录退课日志
            if (null != vo)
            {
                String teachingClassCode = vo.getTeachingClassCode();
                ElcLog log = new ElcLog();
                log.setCalendarId(calendarId);
                log.setCourseCode(vo.getCourseCode());
                log.setCourseName(vo.getCourseName());
                log.setCreateBy(currentSession.getUid());
                log.setCreatedAt(new Date());
                log.setCreateIp(currentSession.getIp());
                log.setMode(ElcLogVo.MODE_2);
                log.setStudentId(studentId);
                log.setTeachingClassCode(teachingClassCode);
                log.setTurn(turn);
                log.setType(ElcLogVo.TYPE_2);
                logList.add(log);
                
                vo.setCalendarId(calendarId);
                vo.setStudentId(studentId);
                try{
                    elecBkService.syncRemindTime(calendarId,2,studentId,stu.getName(),course.getName()+"("+elcCourseTake.getCourseCode()+")");
                }catch (Exception e){
                    e.printStackTrace();
                }
                withdrawMap.put(
                    String
                        .format("%s-%s", vo.getCalendarId(), vo.getStudentId()),
                    vo);
            }
            else
            {
                logger.warn(
                    "not find teachingClassInfo calendarId={},teachingClassId={}",
                    calendarId,
                    teachingClassId);
            }
            
            // 修改本研互选的缓存课程信息
            String pattern = String.format(STD_STATUS, calendarId, studentId);
        	Set<String> keys = strTemplate.keys(pattern);
        	if (CollectionUtil.isNotEmpty(keys)) {
        		strTemplate.delete(keys);
    		}
        }
        if (CollectionUtil.isNotEmpty(logList))
        {
            this.elcLogDao.insertList(logList);
            for (Entry<String, ElcCourseTake> entry : withdrawMap.entrySet())
            {
                ElcCourseTake take = entry.getValue();
                applicationContext.publishEvent(new ElectLoadEvent(
                    take.getCalendarId(), take.getStudentId()));
                String elecStatus = Constants.UN_ELEC;
                //更新培养的选课状态
                StudentPlanCoure studentPlanCoure = new StudentPlanCoure();
                studentPlanCoure.setStudentId(take.getStudentId());
                studentPlanCoure.setCourseCode(take.getCourseCode());
                studentPlanCoure.setElecStatus(elecStatus);
                try {
                    CultureSerivceInvoker.updateElecStatus(studentPlanCoure);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    @Transactional
    @Override
    public void newWithdraw(List<ElcCourseTake> value)
    {
    	Session currentSession = SessionUtils.getCurrentSession();
        Map<String, ElcCourseTakeVo> classInfoMap = new HashMap<>();
        List<ElcLog> logList = new ArrayList<>();
        Map<String, ElcCourseTake> withdrawMap = new HashMap<>();
        for (ElcCourseTake take : value)
        {
            Long calendarId = take.getCalendarId();
            String studentId = take.getStudentId();
            Long teachingClassId = take.getTeachingClassId();
            Integer turn = take.getTurn()!=null?take.getTurn():0;
            //删除选课记录
            //更新选课申请数据
            electionApplyService
                    .update(studentId, calendarId, take.getCourseCode(),ElectRuleType.WITHDRAW);
            Example example1 = new Example(Course.class);
            example1.createCriteria().andEqualTo("code",take.getCourseCode());
//            Course course = courseDao.selectOneByExample(example1);
            //减少选课人数
            int count =classDao.decrElcNumber(teachingClassId);
            //保存第三、四轮退课人数
            if (turn == Constants.THIRD_TURN
                    || turn == Constants.FOURTH_TURN)
            {
            	 count= classDao.increDrawNumber(teachingClassId);
            	 if (count > 0)
                 {
                     dataProvider.incrementDrawNumber(teachingClassId);
                 }
            }
//            Student stu  = studentDao.findStudentByCode(studentId);
            // 更新缓存中教学班人数
            teachClassCacheService.updateTeachingClassNumber(teachingClassId);
            ElcCourseTakeVo vo = null;
            String key = calendarId + "-" + teachingClassId;
            if (!classInfoMap.containsKey(key))
            {
                vo = this.courseTakeDao
                    .getTeachingClassInfo(calendarId, teachingClassId, null);
                classInfoMap.put(key, vo);
            }
            else
            {
                vo = classInfoMap.get(key);
            }
            //ElecContextUtil.updateSelectedCourse(calendarId, studentId);
            // 记录退课日志
            if (null != vo)
            {
                String teachingClassCode = vo.getTeachingClassCode();
                ElcLog log = new ElcLog();
                log.setCalendarId(calendarId);
                log.setCourseCode(vo.getCourseCode());
                log.setCourseName(vo.getCourseName());
                log.setCreateBy(currentSession.getUid());
                log.setCreatedAt(new Date());
                log.setCreateIp(currentSession.getIp());
                log.setMode(ElcLogVo.MODE_2);
                log.setStudentId(studentId);
                log.setTeachingClassCode(teachingClassCode);
                log.setTurn(turn);
                log.setType(ElcLogVo.TYPE_2);
                logList.add(log);
                
                vo.setCalendarId(calendarId);
                vo.setStudentId(studentId);
//                try{
//                    elecBkService.syncRemindTime(calendarId,2,studentId,stu.getName(),course.getName()+"("+elcCourseTake.getCourseCode()+")");
//                }catch (Exception e){
//                    e.printStackTrace();
//                }

                withdrawMap.put(
                    String
                        .format("%s-%s", vo.getCalendarId(), vo.getStudentId()),
                    vo);
            }
            else
            {
                logger.warn(
                    "not find teachingClassInfo calendarId={},teachingClassId={}",
                    calendarId,
                    teachingClassId);
            }
        }
        List<Long> takeIds = value.stream().map(ElcCourseTake ::getId).collect(Collectors.toList());
        Example example = new Example(ElcCourseTake.class);
        example.createCriteria().andIn("id", takeIds).andEqualTo("calendarId", value.get(0).getCalendarId());
        courseTakeDao.deleteByExample(example);
        if (CollectionUtil.isNotEmpty(logList))
        {
        	
			 SqlSession session = factory.openSession(ExecutorType.BATCH, false);
			 ElcLogDao classmapper = session.getMapper(ElcLogDao.class);
		//             result.setTotal(insertNumber+size);
		     for (int i = 0; i < logList.size(); i++)
		     {
		     	classmapper.insertSelective(logList.get(i));
		         if (i % 100 == 0)
		         {//每1000条提交一次防止内存溢出
		         	session.commit();
		         	session.clearCache();
		         }
		     }
		     session.commit();
		     session.clearCache();
//            this.elcLogDao.insertList(logList);
            for (Entry<String, ElcCourseTake> entry : withdrawMap.entrySet())
            {
                ElcCourseTake take = entry.getValue();
                applicationContext.publishEvent(new ElectLoadEvent(
                    take.getCalendarId(), take.getStudentId()));
                String elecStatus = Constants.UN_ELEC;
                //更新培养的选课状态
                StudentPlanCoure studentPlanCoure = new StudentPlanCoure();
                studentPlanCoure.setStudentId(take.getStudentId());
                studentPlanCoure.setCourseCode(take.getCourseCode());
                studentPlanCoure.setElecStatus(elecStatus);
                try {
                    CultureSerivceInvoker.updateElecStatus(studentPlanCoure);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }
    
    @Override
	public void graduateWithdraw(ElcCourseTakeWithDrawDto value,String currentRole, boolean adminFlag, String projId) {
    	
    	Date date = new Date();
    	//如果当前操作人是教务员
        if ("1".equals(currentRole)&&!adminFlag)
        {
        	//判断选课结果开关状态
    		ElcResultSwitch elcResultSwitch = elecResultSwitchService.getSwitch(value.getCalendarId(),projId);
    		if (elcResultSwitch.getStatus() == Constants.ZERO) {
    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled",I18nUtil.getMsg("elecResultSwitch.notEnabled")));
    		} else if(date.getTime() > elcResultSwitch.getOpenTimeEnd().getTime() ||  date.getTime() < elcResultSwitch.getOpenTimeStart().getTime()){
    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.operationalerror",I18nUtil.getMsg("elecResultSwitch.operationalerror")));
    		}
    		
    		//判断课程性质
    		
    		Example example = new Example(Course.class);
    		Criteria createCriteria = example.createCriteria();
    		createCriteria.andEqualTo("code",value.getCourseCode());
    		Course course = courseDao.selectOneByExample(example);
    		if(course.getIsElective().intValue() == Constants.ONE){
    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.noPower",I18nUtil.getMsg("elecResultSwitch.noPower")));
    		}

        }
        List<ElcCourseTake> values = new ArrayList<ElcCourseTake>();
        List<String> students = ScoreServiceInvoker.findCourseHaveScore2(value.getCourseCode(), value.getCalendarId(), value.getStudents());
        if (CollectionUtil.isNotEmpty(students)) {
            throw new ParameterValidateException(I18nUtil.getMsg(I18nUtil.getMsg("elcCourseUphold.removeCourseError2",String.join(",",students))));
        }
        for (String studentId : value.getStudents()) {
        	ElcCourseTake elcCourseTake = new ElcCourseTake();
        	elcCourseTake.setStudentId(studentId);
        	elcCourseTake.setCalendarId(value.getCalendarId());
        	elcCourseTake.setCourseCode(value.getCourseCode());
        	elcCourseTake.setTeachingClassId(value.getTeachingClassId());
        	values.add(elcCourseTake);
		}
        //
        this.graduateWithdrawCourse(values);
	}
    @Transactional
    private void graduateWithdrawCourse(List<ElcCourseTake> value)
    {
        Map<String, ElcCourseTakeVo> classInfoMap = new HashMap<>();
        
        List<ElcLog> logList = new ArrayList<>();
        Map<String, ElcCourseTake> withdrawMap = new HashMap<>();
        for (ElcCourseTake take : value)
        {
            Long calendarId = take.getCalendarId();
            String studentId = take.getStudentId();
            Long teachingClassId = take.getTeachingClassId();
            //删除选课记录
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria()
                .andEqualTo("calendarId", calendarId)
                .andEqualTo("studentId", studentId)
                .andEqualTo("teachingClassId", teachingClassId);
            courseTakeDao.deleteByExample(example);
            //减少选课人数
            classDao.decrElcNumber(teachingClassId);
            
            ElcCourseTakeVo vo = null;
            String key = calendarId + "-" + teachingClassId;
            if (!classInfoMap.containsKey(key))
            {
                vo = this.courseTakeDao
                    .getTeachingClassInfo(calendarId, teachingClassId, null);
                classInfoMap.put(key, vo);
            }
            else
            {
                vo = classInfoMap.get(key);
            }
            try {
            	elecYjsServiceImpl.updateSelectCourse(studentId,vo.getCourseCode(),ElectRuleType.WITHDRAW);
			} catch (Exception e) {
				e.printStackTrace();
			}
            // 记录退课日志
            if (null != vo)
            {
                String teachingClassCode = vo.getTeachingClassCode();
                ElcLog log = new ElcLog();
                log.setCalendarId(calendarId);
                log.setCourseCode(vo.getCourseCode());
                log.setCourseName(vo.getCourseName());
                Session currentSession = SessionUtils.getCurrentSession();
                log.setCreateBy(currentSession.getUid());
                log.setCreatedAt(new Date());
                log.setCreateIp(currentSession.getIp());
                log.setMode(ElcLogVo.MODE_2);
                log.setStudentId(studentId);
                log.setTeachingClassCode(teachingClassCode);
                log.setTurn(0);
                log.setType(ElcLogVo.TYPE_2);
                logList.add(log);
                
                vo.setCalendarId(calendarId);
                vo.setStudentId(studentId);
                withdrawMap.put(
                    String
                        .format("%s-%s", vo.getCalendarId(), vo.getStudentId()),
                    vo);
            }
            else
            {
                logger.warn(
                    "not find teachingClassInfo calendarId={},teachingClassId={}",
                    calendarId,
                    teachingClassId);
            }
            
            // 修改本研互选的缓存课程信息
            String pattern = String.format(STD_STATUS, calendarId, studentId);
        	Set<String> keys = strTemplate.keys(pattern);
        	if (CollectionUtil.isNotEmpty(keys)) {
        		strTemplate.delete(keys);
    		}
        }
        if (CollectionUtil.isNotEmpty(logList))
        {
            this.elcLogDao.insertList(logList);
            for (Entry<String, ElcCourseTake> entry : withdrawMap.entrySet())
            {
                ElcCourseTake take = entry.getValue();
                applicationContext.publishEvent(new ElectLoadEvent(
                    take.getCalendarId(), take.getStudentId()));
            }
        }
    }

	@Override
	public PageResult<Student4Elc> getGraduateStudentForCulturePlan(PageCondition<ElcResultQuery> page) {
		Session currentSession = SessionUtils.getCurrentSession();
		ElcResultQuery cond = page.getCondition();
		cond.setProjectId(currentSession.getCurrentManageDptId());

		//查询本门课是否有选课
		logger.info("cond.getCourseCode()+++++++++++++++++++++++"+cond.getCourseCode());

		Example example = new Example(ElcCourseTake.class);
		example.createCriteria().andEqualTo("courseCode",cond.getCourseCode());
		List<ElcCourseTake> selectByExample = courseTakeDao.selectByExample(example);
//		List<String> collect = selectByExample.stream().map(ElcCourseTake::getStudentId).collect(Collectors.toList());
		List<String> collect = new ArrayList<>();
		for (ElcCourseTake string : selectByExample) {
			collect.add(string.getStudentId());
		}
		collect.add("0");

        List<String> stu = ScoreServiceInvoker.findStu(cond.getCourseCode(), cond.getStudentIds());
        collect.addAll(stu);
		cond.setStudentCodes(collect);
		PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<Student4Elc> listPage = studentDao.getStudent4CulturePlan(cond);
        PageResult<Student4Elc> result = new PageResult<>(listPage);
		return result;
	}
	@Override
	public PageResult<Student4Elc> getGraduateStudentForCulturePlan4Retake(PageCondition<ElcResultQuery> page) {
		Session currentSession = SessionUtils.getCurrentSession();
		ElcResultQuery cond = page.getCondition();
		cond.setProjectId(currentSession.getCurrentManageDptId());

		//查询本门课是否有选课
		logger.info("cond.getCourseCode()+++++++++++++++++++++++"+cond.getCourseCode());

		Example example = new Example(ElcCourseTake.class);
		example.createCriteria().andEqualTo("courseCode",cond.getCourseCode());
		List<ElcCourseTake> selectByExample = courseTakeDao.selectByExample(example);
//		List<String> collect = selectByExample.stream().map(ElcCourseTake::getStudentId).collect(Collectors.toList());
		List<String> collect = new ArrayList<>();
		for (ElcCourseTake string : selectByExample) {
			collect.add(string.getStudentId());
		}
		collect.add("0");
		cond.setStudentCodes(collect);
		Example example1 = new Example(ElcCourseTake.class);
		example1.createCriteria().andEqualTo("courseCode",cond.getCourseCode()).andEqualTo("calendarId",cond.getCalendarId());
		List<ElcCourseTake> selectByExample1 = courseTakeDao.selectByExample(example1);
		List<String> collect1 = new ArrayList<>();
		for (ElcCourseTake string : selectByExample1) {
			collect1.add(string.getStudentId());
		}
		collect1.add("0");
		cond.setStudentCodess(collect1);
		PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<Student4Elc> listPage = studentDao.getStudent4CulturePlanRetake(cond);
        PageResult<Student4Elc> result = new PageResult<>(listPage);
		return result;
	}
    /**
    *@Description: 查找加课学生
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/2/23 14:17
    */
    @Override
    public PageResult<Student> findStudentList(
        PageCondition<ElcCourseTakeQuery> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<Student> page =
            courseTakeDao.findStudentList(condition.getCondition());
        return new PageResult<>(page);
    }

    /**
    *@Description: 查询学籍异动学生选课信息
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/6/5 16:29
    */
    @Override
    public List<ElcCourseTakeVo> page2StuAbnormal(ElcCourseTakeQuery query) {
        String studentId = query.getStudentId();
        Long calendarId = query.getCalendarId();
        //查询学生已选课程
        List<ElcCourseTakeVo> elcList =new ArrayList<>();
        PageCondition<ElcCourseTakeQuery> page =new PageCondition<>();
        page.setPageNum_(1);
        page.setPageSize_(1000);
        page.setCondition(query);
        PageResult<ElcCourseTakeVo> listPage = listPage(page);
        if(listPage!=null){
            elcList = listPage.getList();
        }
        if(CollectionUtil.isEmpty(elcList)){
            return null;
        }
        List<StudentScoreVo> stuScore = ScoreServiceInvoker.findStuScoreByCalendarIdAndStudentCode(calendarId, studentId);
        if(CollectionUtil.isEmpty(stuScore)){
            return elcList;
        }
        List<String> codes = stuScore.stream().filter(vo ->StringUtils.isNotBlank(vo.getCourseCode())).map(StudentScoreVo::getCourseCode).collect(Collectors.toList());
        List<ElcCourseTakeVo> collect = elcList.stream().filter(vo -> !codes.contains(vo.getCourseCode())).collect(Collectors.toList());
        return collect;
    }

    /**
     *@Description: 学籍异动学生退课
     *@Param:
     *@return:
     *@Author: bear
     *@date: 2019/6/5 16:29
     */
    @Override
    public void withdraw2StuAbnormal(ElcCourseTakeQuery query) {
        List<ElcCourseTakeVo> elcCourseTakeVos = page2StuAbnormal(query);
        List<ElcCourseTake> list =new ArrayList<>();
        if(CollectionUtil.isNotEmpty(elcCourseTakeVos)){
            for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                ElcCourseTake courseTake=new ElcCourseTake();
                courseTake.setCalendarId(elcCourseTakeVo.getCalendarId());
                courseTake.setStudentId(elcCourseTakeVo.getStudentId());
                courseTake.setTeachingClassId(elcCourseTakeVo.getTeachingClassId());
                list.add(courseTake);
            }
            withdraw(list);
        }
    }

    @Override
    @Transactional
	public int editStudyType(ElcCourseTakeDto elcCourseTakeDto) {
    	if(elcCourseTakeDto.getCourseTakeType()==null||CollectionUtil.isEmpty(elcCourseTakeDto.getIds())) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
    	}
    	int result = courseTakeDao.editStudyType(elcCourseTakeDto.getCourseTakeType(), elcCourseTakeDto.getIds(),elcCourseTakeDto.getCalendarId());
    	return result;
    }

    @Override
    public List<String> findAllByStudentId(String studentId) {
        return courseTakeDao.findAllByStudentId(studentId) ;
    }

	@Override
	public PageResult<ElcCourseTakeNameListVo> courseTakeNameListPage(PageCondition<ElcCourseTakeQuery> condition) {
		Session currentSession = SessionUtils.getCurrentSession();
		if (StringUtils.isNotEmpty(condition.getCondition().getIncludeCourseCode())) {
			condition.getCondition().getIncludeCourseCodes().add(condition.getCondition().getIncludeCourseCode());
		}
//		condition.getCondition().setProjectId(currentSession.getCurrentManageDptId());

//		PageResult<ElcCourseTakeVo> list = listPage(condition);
//        PageResult<ElcCourseTakeVo> list = courseTakeNameList(condition);

        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcCourseTakeVo> listPage = courseTakeDao.courseTakeNameList(condition.getCondition());

		if (CollectionUtil.isNotEmpty(listPage)) {
            for (ElcCourseTakeVo elcCourseTakeVo:listPage) {
                if (elcCourseTakeVo.getChooseObj().intValue() == 1) {
                    elcCourseTakeVo.setElectionMode(1);
                }else{
                    elcCourseTakeVo.setElectionMode(2);
                }
            }

			/*Iterator<ElcCourseTakeVo> iterator = list2.iterator();
			while (iterator.hasNext()) {
				ElcCourseTakeVo takeVo = iterator.next();
				condition.getCondition().setStudentId(takeVo.getStudentId());
				condition.getCondition().setTeachingClassCode(takeVo.getTeachingClassCode());
				List<ElcLog> listLogs = elcLogDao.getElectionLog(condition.getCondition());
				if (CollectionUtil.isNotEmpty(listLogs)) {
					takeVo.setElectionMode(listLogs.get(0).getMode());
				}else {
					if(condition.getCondition().getMode() != null) {
						iterator.remove();
					}
				}
			}*/
		}
		
//		list2.sort(Comparator.comparing(ElcCourseTakeVo::getStudentCode));
		
		List<ElcCourseTakeNameListVo> nameList = new ArrayList<>();
    	for (ElcCourseTakeVo elcCourseTakeVo : listPage) {
    		ElcCourseTakeNameListVo elcCourseTakeNameListVo = new ElcCourseTakeNameListVo();
    		elcCourseTakeNameListVo.setElcCourseTakeVo(elcCourseTakeVo);
    		Student stu = studentDao.findStudentByCode(elcCourseTakeVo.getStudentId());
    		elcCourseTakeNameListVo.setStudentInfo(stu);
    		nameList.add(elcCourseTakeNameListVo);
		}
    	PageResult<ElcCourseTakeNameListVo> result = new PageResult<>();
    	result.setList(nameList);
    	result.setPageNum_(listPage.getPageNum());
    	result.setPageSize_(listPage.getPageSize());
    	result.setTotal_(listPage.getTotal());
		return result;
	}
	
	/** 研究生选课结果处理学生名单 */
	public PageResult<ElcCourseTakeVo> courseTakeNameList(PageCondition<ElcCourseTakeQuery> condition){
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcCourseTakeVo> listPage = courseTakeDao.courseTakeNameList(condition.getCondition());
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
	}

//    @Override
//    public PageResult<ElcStudentVo> addCourseList(PageCondition<ElcCourseTakeQuery> condition) {
//        ElcCourseTakeQuery courseTakeQuerytudentVo = condition.getCondition();
//        String studentId = courseTakeQuerytudentVo.getStudentId();
//        Long calendarId = courseTakeQuerytudentVo.getCalendarId();
//        String keyword = courseTakeQuerytudentVo.getKeyword();
//        Session session = SessionUtils.getCurrentSession();
//        String currentManageDptId = session.getCurrentManageDptId();
//        List<PlanCourseDto> courseType;
//        if ("2".equals(currentManageDptId)) {
//            //通过普研培养计划获取学生所有需要修读的课程
//            courseType = CultureSerivceInvoker.findCourseTypeForGradute(studentId);
//        } else {
//            //通过在职研究生培养计划获取学生所有需要修读的课程
//            courseType = CultureSerivceInvoker.findCourseTypeForGraduteExemption(studentId);
//        }
//        if (CollectionUtil.isEmpty(courseType)) {
//            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.planCultureError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
//        }
//        List<PlanCourseTypeDto> planCourseTypeDtos = new ArrayList<>(100);
//        for (PlanCourseDto planCourseDto : courseType) {
//            List<PlanCourseTypeDto> list = planCourseDto.getList();
//            planCourseTypeDtos.addAll(list);
//        }
//        Set<String> planCourseCode = planCourseTypeDtos.stream().
//                map(PlanCourseTypeDto::getCourseCode).collect(Collectors.toSet());
//        //获取学生本学期已选的课程
//        List<String> codes = courseTakeDao.findSelectedCourseCode(studentId, calendarId);
//        // 获取学生所有已修课程成绩
//        List<ScoreStudentResultVo> stuScore = ScoreServiceInvoker.findStuScore(studentId);
//        // 获取学生通过课程集合
//        Set<String> collect1 = stuScore.stream().
//                filter(item -> item.getIsPass() == 1).
//                map(ScoreStudentResultVo::getCourseCode).
//                collect(Collectors.toSet());
//
//        // 组合学生已选课程和考试通过课程
//        codes.addAll(collect1);
//
//        // 获取学生未通过课程集合
//        Set<String> collect2 = stuScore.stream().
//                filter(item -> item.getIsPass() != 1).
//                map(ScoreStudentResultVo::getCourseCode).
//                collect(Collectors.toSet());
//
//        // 将未通过课程放到培养计划，防止培养计划里面没有未通过课程
//       planCourseCode.addAll(collect2);
//        //剔除培养计划课程集合中学生已通过的课程，获取学生还需要修读的课程
//        List<String> elcCourses = planCourseCode.stream()
//                .filter(item -> !codes.contains(item))
//                .collect(Collectors.toList());
//
//        Page<ElcStudentVo> elcStudentVos = new Page<ElcStudentVo>();
//        if (CollectionUtil.isEmpty(elcCourses)) {
//            return new PageResult<>(elcStudentVos);
//        }
//
//        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
//        if (StringUtils.equals(session.getCurrentRole(), "1") && session.isAdmin()) {
//            elcStudentVos = courseTakeDao.findAddCourseList(elcCourses, calendarId, keyword, currentManageDptId);
//        } else if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
//            elcStudentVos = courseTakeDao.findAddCourseListByNature(elcCourses, calendarId, keyword, currentManageDptId);
//        } else {
//            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.loginError",I18nUtil.getMsg("elecResultSwitch.operationalerror")));
//        }
//        setCourseArrange(elcStudentVos);
//        return new PageResult<>(elcStudentVos);
//    }

    @Override
    public PageResult<ElcStudentVo> addCourseList(PageCondition<ElcCourseTakeQuery> condition) {
        ElcCourseTakeQuery courseTakeQuerytudentVo = condition.getCondition();
        String studentId = courseTakeQuerytudentVo.getStudentId();
        Long calendarId = courseTakeQuerytudentVo.getCalendarId();
        String keyword = courseTakeQuerytudentVo.getKeyword();
        Session session = SessionUtils.getCurrentSession();
        String currentManageDptId = session.getCurrentManageDptId();
        List<PlanCourseDto> courseType;
        if ("2".equals(currentManageDptId)) {
            //通过普研培养计划获取学生所有需要修读的课程
            courseType = CultureSerivceInvoker.findCourseTypeForGradute(studentId);
        } else {
            //通过在职研究生培养计划获取学生所有需要修读的课程
            courseType = CultureSerivceInvoker.findCourseTypeForGraduteExemption(studentId);
        }
        if (CollectionUtil.isEmpty(courseType)) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.planCultureError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        List<PlanCourseTypeDto> planCourseTypeDtos = new ArrayList<>(100);
        for (PlanCourseDto planCourseDto : courseType) {
            List<PlanCourseTypeDto> list = planCourseDto.getList();
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            planCourseTypeDtos.addAll(list);
        }
        Set<String> planCourseCode = planCourseTypeDtos.stream().
                map(PlanCourseTypeDto::getCourseCode).collect(Collectors.toSet());
        //获取学生本学期已选的课程
        List<String> codes = courseTakeDao.findSelectedCourseCode(studentId, calendarId);
        // 获取学生所有已修课程成绩
        List<ScoreStudentResultVo> stuScore = ScoreServiceInvoker.findStuScore(studentId);
        // 获取学生修过课程集合
        // 这里不包含重修课，所有只要有成绩就不用再修了，重修课在单独页面添加
        Set<String> collect = stuScore.stream().
                map(ScoreStudentResultVo::getCourseCode).
                collect(Collectors.toSet());

        // 组合学生本学期已选课程和之前修过的课程
        codes.addAll(collect);

        //剔除培养计划课程集合中学生已修过的课程，获取学生还需要修读的课程
        List<String> elcCourses = planCourseCode.stream()
                .filter(item -> !codes.contains(item))
                .collect(Collectors.toList());

        Page<ElcStudentVo> elcStudentVos = new Page<ElcStudentVo>();
        if (CollectionUtil.isEmpty(elcCourses)) {
            return new PageResult<>(elcStudentVos);
        }

        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        if (StringUtils.equals(session.getCurrentRole(), "1") && session.isAdmin()) {
            elcStudentVos = courseTakeDao.findAddCourseList(elcCourses, calendarId, keyword, currentManageDptId);
        } else if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            elcStudentVos = courseTakeDao.findAddCourseListByNature(elcCourses, calendarId, keyword, currentManageDptId);
        } else {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.loginError",I18nUtil.getMsg("elecResultSwitch.operationalerror")));
        }
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    @Override
    @Transactional
    public String addCourse(AddCourseDto courseDto) {
        Integer status = courseDto.getStatus();
        Session session = SessionUtils.getCurrentSession();
        boolean isAdmin = StringUtils.equals(session.getCurrentRole(), "1")
                && session.isAdmin();
        Integer chooseObj = isAdmin ? 3 : 2 ;
        // 查询要添加教学班的课程信息
        List<Long> teachingClassIds = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassIds);
        int size = teachingClassIds.size();

        if (status == null || status == 0) {
            String studentId = courseDto.getStudentId();
            Student student = studentDao.selectByPrimaryKey(studentId);
            String campus = student.getCampus();

            List<String> campusList = new ArrayList<>(size);
            List<String> numList = new ArrayList<>(size);
            Map<Long, String> addMap = new HashMap<>(size);
            for (ElcStudentVo elcStudentVo : elcStudentVos) {
                String classCode = elcStudentVo.getClassCode();
                addMap.put(elcStudentVo.getTeachingClassId(), elcStudentVo.getClassCode());
                if (!campus.equals(elcStudentVo.getCampus()))
                {
                    campusList.add(classCode);
                }
                if (elcStudentVo.getElcNumber() + 1 > elcStudentVo.getNumber())
                {
                    numList.add(classCode);
                }
            }
            if (CollectionUtil.isNotEmpty(campusList)) {
                throw new ParameterValidateException("课程"
                        + String.join(",", campusList) +
                        "所在校区与学生校区不一致，请重新添加");
            }
            Long calendarId = courseDto.getCalendarId();
            if (isAdmin) {
                StringBuffer sb = new StringBuffer();
                if (CollectionUtil.isNotEmpty(numList)) {
                    sb.append("教学班").append(String.join(",", numList)).
                            append("已达教室容量上限,");
                }
                String msg = conflictMsg(courseDto, addMap);
                sb.append(msg);
                String s = sb.toString();
                if (!"".equals(s)) {
                    return s + "您确定要添加吗？";
                }
            } else {
                //判断选课结果开关状态
                boolean switchStatus = elecResultSwitchService.getSwitchStatus(calendarId, session.getCurrentManageDptId());
                // 教务员需判断选课开关是否开启
                if (!switchStatus) {
                    throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled"));
                }
                if (CollectionUtil.isNotEmpty(numList)) {
                    throw new ParameterValidateException("教学班"
                            + String.join(",", numList) + "已达教室容量上限");
                }
                String msg = conflictMsg(courseDto, addMap);
                if (!"".equals(msg)) {
                    throw new ParameterValidateException(msg);
                }
            }
        }

        List<ElcCourseTake> elcCourseTakes = new ArrayList<>(size);
        List<ElcLog> elcLogs = new ArrayList<>(size);
        // 保存数据
        addToList(session, chooseObj, courseDto, elcStudentVos, elcCourseTakes, elcLogs);
        saveCourse(courseDto, elcCourseTakes, elcLogs);
        return "";
    }

    private String conflictMsg(AddCourseDto courseDto, Map<Long, String> addMap){
        String studentId = courseDto.getStudentId();
        Long calendarId = courseDto.getCalendarId();
        List<Long> teachingClassIds = courseDto.getTeachingClassId();
        String msg = "";
        // 查询已选课程上课时间
        List<ElcCourseTakeVo> elcCourseTakeVos = courseTakeDao.findElcCourseTakeByStudentId(studentId, calendarId);
        if (CollectionUtil.isNotEmpty(elcCourseTakeVos)) {
            Map<Long, String> selectedMap = elcCourseTakeVos.stream().collect(Collectors.toMap(ElcCourseTakeVo::getTeachingClassId, s -> s.getCourseCode()));
            Set<Long> set = selectedMap.keySet();
            List<Long> ids = new ArrayList<>(set);
            List<TimeTableMessage> selectCourseArrange = courseTakeDao.findCourseArrange(ids);
            // 查询要添加课程的上课时间
            List<TimeTableMessage> addCourseArrange = courseTakeDao.findCourseArrange(teachingClassIds);
            Map<Long, Long> map = getCourseConflict(addCourseArrange, selectCourseArrange);
            if (!map.isEmpty()) {
                int sz = map.size();
                List<String> addCourses = new ArrayList<>(sz);
                List<String> selectedCourses = new ArrayList<>(sz);
                for (Entry<Long, Long> entry : map.entrySet()) {
                    String addCourse = addMap.get(entry.getKey());
                    String selectedCourse = selectedMap.get(entry.getValue());
                    addCourses.add(addCourse);
                    selectedCourses.add(selectedCourse);
                }
                msg = "课程" + String.join(",", addCourses)
                        + "与学生已选课程" + String.join(",", selectedCourses)
                        + "上课时间冲突,";
            }
        }
        return msg;
    }

    private void saveCourse(AddCourseDto courseDto, List<ElcCourseTake> elcCourseTakes, List<ElcLog> elcLogs) {
        List<Long> teachingClassIds = courseDto.getTeachingClassId();
        // 保存数据
        Integer count = courseTakeDao.saveCourseTask(elcCourseTakes);
        if (count.intValue() != teachingClassIds.size()) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.addCourseError"));
        }
        for (ElcCourseTake elcCourseTake : elcCourseTakes) {
        	try {
				elecYjsServiceImpl.updateSelectCourse(elcCourseTake.getStudentId(),elcCourseTake.getCourseCode(),ElectRuleType.ELECTION);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
        teachingClassDao.increElcNumberList(teachingClassIds);
        Integer logCount = elcLogDao.saveCourseLog(elcLogs);
        if (logCount != count) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.addCourseLogError"));
        }
        logger.info("++++++++++++++++++++++publishEvent and event start");
        applicationContext.publishEvent(new ElectLoadEvent(
                courseDto.getCalendarId(), courseDto.getStudentId()));
    }

    @Override
    @Transactional
    public void forceAdd(AddCourseDto courseDto) {
        List<Long> teachingClassIds = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassIds);
        String studentId = courseDto.getStudentId();
        int size = elcStudentVos.size();
        List<ElcCourseTake> elcCourseTakes = new ArrayList<>(size);
        List<ElcLog> elcLogs = new ArrayList<>(size);
        Session session = SessionUtils.getCurrentSession();
        String uid = session.getUid();
        String name = session.getName();
        String ip = session.getIp();
        Long calendarId = courseDto.getCalendarId();
        Integer chooseObj = getChooseObj(session, calendarId);
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            ElcCourseTake elcCourseTake = new ElcCourseTake();
            elcCourseTake.setStudentId(studentId);
            elcCourseTake.setCalendarId(calendarId);
            String courseCode = elcStudentVo.getCourseCode();
            elcCourseTake.setCourseCode(courseCode);
            elcCourseTake.setTeachingClassId(elcStudentVo.getTeachingClassId());
            elcCourseTake.setMode(1);
            elcCourseTake.setChooseObj(chooseObj);
            elcCourseTake.setCreatedAt(new Date());
            elcCourseTake.setTurn(0);
            int count = courseTakeDao.courseCount(courseCode, studentId);
            if(count != 0) {
                elcCourseTake.setCourseTakeType(2);
            }else {
                elcCourseTake.setCourseTakeType(1);
            }
            elcCourseTakes.add(elcCourseTake);
            ElcLog elcLog = new ElcLog();
            elcLog.setStudentId(studentId);
            elcLog.setCourseCode(elcStudentVo.getCourseCode());
            elcLog.setCourseName(elcStudentVo.getCourseName());
            elcLog.setTeachingClassCode(elcStudentVo.getClassCode());
            elcLog.setCalendarId(calendarId);
            elcLog.setType(1);
            elcLog.setMode(2);
            elcLog.setCreateBy(uid);
            elcLog.setCreateName(name);
            elcLog.setCreateIp(ip);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
        saveCourse(courseDto, elcCourseTakes, elcLogs);
    }

    @Override
    @Transactional
    public Integer removedCourse(List<ElcCourseTake> value) {
        Session session = SessionUtils.getCurrentSession();
        // 查询退课
        ElcCourseTake courseTake = value.get(0);
        Long calendarId = courseTake.getCalendarId();
        String studentId = courseTake.getStudentId();
        List<Long> teachingClassIds = value.stream().map(ElcCourseTake::getTeachingClassId).collect(Collectors.toList());
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassIds);
        List<String> courseCodes = elcStudentVos.stream().map(ElcStudentVo::getCourseCode).collect(Collectors.toList());
        List<String> courses = ScoreServiceInvoker.findCourseHaveScore(studentId, calendarId, courseCodes);
        if (CollectionUtil.isNotEmpty(courses)) {
            throw new ParameterValidateException(I18nUtil.getMsg(I18nUtil.getMsg("elcCourseUphold.removeCourseError",String.join(",",courses))));
        }
        // 验证选课维护开关状态
        getChooseObj(session, calendarId);
        int count = courseTakeDao.deleteByCourseTask(value);
        int delSize = teachingClassIds.size();
        if (delSize != count ) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.removedCourseError"));
        }
        for (Long teachingClassId : teachingClassIds) {
            int decrElcNumber = teachingClassDao.decrElcNumber(teachingClassId);
            if (decrElcNumber != 1) {
                throw new ParameterValidateException(I18nUtil.getMsg(I18nUtil.getMsg("elcCourseUphold.decrElcNumberError",teachingClassId + "")));
            }
        }
        int size = elcStudentVos.size();
        if (elcStudentVos.size() != delSize) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.teachingTaskError"));
        }
        String id = session.getUid();
        String name = session.getName();
        String ip = session.getIp();
        List<ElcLog> elcLogs = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ElcStudentVo elcStudentVo = elcStudentVos.get(i);
            ElcCourseTake elcCourseTake = value.get(i);
            ElcLog elcLog = new ElcLog();
            elcLog.setStudentId(elcCourseTake.getStudentId());
            elcStudentVo.setStudentId(elcCourseTake.getStudentId());
            elcLog.setCourseCode(elcStudentVo.getCourseCode());
            elcLog.setCourseName(elcStudentVo.getCourseName());
            elcLog.setTeachingClassCode(elcStudentVo.getClassCode());
            elcLog.setCalendarId(elcCourseTake.getCalendarId());
            elcLog.setType(2);
            elcLog.setMode(2);
            elcLog.setTurn(0);
            elcLog.setCreateBy(id);
            elcLog.setCreateIp(ip);
            elcLog.setCreateName(name);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
    		try {
    			elecYjsServiceImpl.updateSelectCourse(elcStudentVo.getStudentId(),elcStudentVo.getCourseCode(),ElectRuleType.WITHDRAW);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
        }
        Integer logCount = elcLogDao.saveCourseLog(elcLogs);
        if (logCount != delSize) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.addCourseLogError"));
        }
        for (ElcCourseTake entry : value)
        {
            applicationContext.publishEvent(new ElectLoadEvent(
            		entry.getCalendarId(),entry.getStudentId()));
        }
        
        // 更新本研互选缓存中的课程信息
        String pattern = String.format(STD_STATUS, calendarId, studentId);
    	Set<String> keys = strTemplate.keys(pattern);
    	if (CollectionUtil.isNotEmpty(keys)) {
    		strTemplate.delete(keys);
		}
        
        return delSize;
    }

    @Override
    public PageResult<ElcStudentVo> removedCourseList(PageCondition<ElcCourseTakeQuery> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElcCourseTakeQuery elcCourseTakeQuery = condition.getCondition();
        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findRemovedCourseList(elcCourseTakeQuery.getCalendarId(), elcCourseTakeQuery.getStudentId());
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    private void setTeachingArrange(List<ElcCourseTakeVo> elcCourseTakeVos) {
        if (CollectionUtil.isNotEmpty(elcCourseTakeVos)) {
            List<Long> ids = elcCourseTakeVos.stream().map(ElcCourseTakeVo::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = courseTakeDao.findClassTime(ids);
            int size = tableMessages.size();
            MultiValueMap<Long, String> arrangeMap = new LinkedMultiValueMap<>(size);
            for (TimeTableMessage tableMessage : tableMessages) {
                Integer dayOfWeek = tableMessage.getDayOfWeek();
                Integer timeStart = tableMessage.getTimeStart();
                Integer timeEnd = tableMessage.getTimeEnd();
                String roomID = tableMessage.getRoomId();
                Long teachingClassId = tableMessage.getTeachingClassId();
                String[] str = tableMessage.getWeekNum().split(",");
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ ClassroomCacheUtil.getRoomName(roomID);
                arrangeMap.add(teachingClassId, timeStr);
            }
            List<TeachingClassCache> teacherClass = teachingClassTeacherDao.findTeacherClass(ids);
            Map<Long, List<TeachingClassCache>> map = teacherClass.stream().collect(Collectors.groupingBy(TeachingClassCache::getTeachClassId));
            for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(elcCourseTakeVo.getCalendarId());
                elcCourseTakeVo.setCalendarName(schoolCalendar.getFullName());
                Long teachingClassId = elcCourseTakeVo.getTeachingClassId();
                List<String> arr = arrangeMap.get(teachingClassId);
                List<TeachingClassCache> teachingClassCaches = map.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                    Set<String> set = teachingClassCaches.stream().map(TeachingClassCache::getTeacherName).collect(Collectors.toSet());
                    String teacherName = String.join(",",set);
                    elcCourseTakeVo.setTeachingName(teacherName);
                }
                if (CollectionUtil.isNotEmpty(arr)) {
                    elcCourseTakeVo.setCourseArrange(String.join(",", arr));
                }
            }
        }
    }

    private void setCourseArrange(Page<ElcStudentVo> elcStudentVos) {
        if (CollectionUtil.isNotEmpty(elcStudentVos)) {
            List<Long> ids = elcStudentVos.stream().map(ElcStudentVo::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = courseTakeDao.findClassTime(ids);
            int size = tableMessages.size();
            MultiValueMap<Long, String> arrangeMap = new LinkedMultiValueMap<>(size);
            for (TimeTableMessage tableMessage : tableMessages) {
                Integer dayOfWeek = tableMessage.getDayOfWeek();
                Integer timeStart = tableMessage.getTimeStart();
                Integer timeEnd = tableMessage.getTimeEnd();
                String roomID = tableMessage.getRoomId();
                Long teachingClassId = tableMessage.getTeachingClassId();
                String[] str = tableMessage.getWeekNum().split(",");
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ ClassroomCacheUtil.getRoomName(roomID);
                arrangeMap.add(teachingClassId, timeStr);
            }
            List<TeachingClassCache> teacherClass = teachingClassTeacherDao.findTeacherClass(ids);
            Map<Long, List<TeachingClassCache>> map = teacherClass.stream().collect(Collectors.groupingBy(TeachingClassCache::getTeachClassId));
            for (ElcStudentVo elcStudentVo : elcStudentVos) {
                Long teachingClassId = elcStudentVo.getTeachingClassId();
                List<TeachingClassCache> teachingClassCaches = map.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                    Set<String> set = teachingClassCaches.stream().map(TeachingClassCache::getTeacherName).collect(Collectors.toSet());
                    String teacherName = String.join(",",set);
                    elcStudentVo.setTeacherName(teacherName);
                }
                List<String> times = arrangeMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(times)) {
                    elcStudentVo.setCourseArrange(String.join(",", times));
                }
            }
        }
    }

    /**
     * 判断当前用户角色及选课维护开关状态
     * @param session
     * @param calendarId
     * @return
     */
    private Integer getChooseObj(Session session, Long calendarId) {
        Integer chooseObj;
        if (StringUtils.equals(session.getCurrentRole(), "1") && session.isAdmin()) {
            chooseObj = 3;
        } else if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            chooseObj = 2;
            boolean switchStatus = elecResultSwitchService.getSwitchStatus(calendarId, session.getCurrentManageDptId());
            // 教务员需判断选课开关是否开启
            if (!switchStatus) {
                throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled"));
            }
        } else {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.loginError"));
        }
        return chooseObj;
    }

    /**
     * 获取要添加教学班中跟学生已选课程上课时间冲突的教学班id
     * @param addCourseArrange
     * @param selectedCourseArrange
     */
    private Map<Long, Long> getCourseConflict(List<TimeTableMessage> addCourseArrange, List<TimeTableMessage> selectedCourseArrange){
        Map<Long, Long> map = new HashMap<>(addCourseArrange.size());
        loop: for (TimeTableMessage addTable : addCourseArrange) {
            String[] split = addTable.getWeekNum().split(",");
            Set<String> addWeeks = new HashSet<>(Arrays.asList(split));
            int dayOfWeek = addTable.getDayOfWeek().intValue();
            int timeStart = addTable.getTimeStart().intValue();
            int timeEnd = addTable.getTimeEnd().intValue();
            int addSize = addWeeks.size();
            Long teachingClassId = addTable.getTeachingClassId();
            for (TimeTableMessage selectTable : selectedCourseArrange) {
                String[] week = selectTable.getWeekNum().split(",");
                Set<String> selectWeeks = new HashSet<>(Arrays.asList(week));
                int selectSize = selectWeeks.size();
                Set<String> weeks = new HashSet<>(addSize + selectSize);
                weeks.addAll(addWeeks);
                weeks.addAll(selectWeeks);
                // 判断上课周是否冲突
                if (addSize + selectSize > weeks.size()) {
                    //上课周冲突，判断上课天
                    if (dayOfWeek == selectTable.getDayOfWeek().intValue()) {
                        // 上课天相同，比价上课节次
                        int start = selectTable.getTimeStart().intValue();
                        int end = selectTable.getTimeEnd().intValue();
                        // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                        if ( (start <= timeStart && timeStart <= end)
                                || (start <= timeEnd && timeEnd <= end)
                                || (timeStart <= start && start <= timeEnd)
                                || (timeStart <= end && end <= timeEnd)) {
                            map.put(teachingClassId, selectTable.getTeachingClassId());
                            continue loop;
                        }
                    }
                }
            }
        }
        return map;
    }

    /**
     * 保存选课数据
     * @param session
     * @param chooseObj
     * @param courseDto
     * @param elcStudentVos
     * @param elcCourseTakes
     * @param elcLogs
     */
    private void addToList(Session session, Integer chooseObj, AddCourseDto courseDto, List<ElcStudentVo> elcStudentVos, List<ElcCourseTake> elcCourseTakes, List<ElcLog> elcLogs) {
        String uid = session.getUid();
        String name = session.getName();
        String ip = session.getIp();
        String studentId = courseDto.getStudentId();
        Long calendarId = courseDto.getCalendarId();
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            ElcCourseTake elcCourseTake = new ElcCourseTake();
            elcCourseTake.setStudentId(studentId);
            elcCourseTake.setCalendarId(calendarId);
            String courseCode = elcStudentVo.getCourseCode();
            elcCourseTake.setCourseCode(courseCode);
            elcCourseTake.setTeachingClassId(elcStudentVo.getTeachingClassId());
            elcCourseTake.setMode(1);
            elcCourseTake.setChooseObj(chooseObj);
            elcCourseTake.setCreatedAt(new Date());
            elcCourseTake.setTurn(0);
            // 课程维护加课只加正常修读的课
            elcCourseTake.setCourseTakeType(1);
            elcCourseTakes.add(elcCourseTake);
            ElcLog elcLog = new ElcLog();
            elcLog.setStudentId(studentId);
            elcLog.setCourseCode(elcStudentVo.getCourseCode());
            elcLog.setCourseName(elcStudentVo.getCourseName());
            elcLog.setTeachingClassCode(elcStudentVo.getClassCode());
            elcLog.setCalendarId(calendarId);
            elcLog.setType(1);
            elcLog.setMode(2);
            elcLog.setTurn(0);
            elcLog.setCreateBy(uid);
            elcLog.setCreateName(name);
            elcLog.setCreateIp(ip);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
    }

	@Override
	public Integer getRetakeNumber(String studentId,Long calendarId) {
        int index = TableIndexUtil.getIndex(calendarId);
        List<Integer> retakeNumber = courseTakeDao.getRetakeNumber(studentId,index);
		return retakeNumber.size();
	}

    @Override
    public ExcelResult export(ElcCourseTakeQuery query) {
        ExcelResult excelResult =
                ExportExcelUtils.submitTask("classList", new ExcelExecuter()
                {
                    @Override
                    public GeneralExcelDesigner getExcelDesigner()
                    {
                        ExcelResult result = this.getResult();
                        PageCondition<ElcCourseTakeQuery> page = new PageCondition<>();
                        List<ElcCourseTakeVo> datas = new ArrayList<>();
                        page.setCondition(query);
                        int pageNum = 0;
                        page.setPageNum_(pageNum);
                        if (CollectionUtil.isEmpty(query.getIds())) {
                            while (true)
                            {
                                pageNum++;
                                page.setPageNum_(pageNum);
                                page.setPageSize_(300);
                                PageResult<ElcCourseTakeVo> res = listPage(page);
                                result.setTotal((int)res.getTotal_());
                                datas.addAll(res.getList());
                                Double count = datas.size()/1.5;
                                result.setDoneCount(count.intValue());
                                this.updateResult(result);
                                if (datas.size() == res.getTotal_())
                                {
                                    break;
                                }
                            }
                        } else {
                            page.setPageSize_(1000);
                            PageResult<ElcCourseTakeVo> res = listPage(page);
                            datas.addAll(res.getList());
                            result.setTotal((int)res.getTotal_());
                        }
                        //组装excel
                        GeneralExcelDesigner design = getDesign();
                        //将数据放入excel对象中
                        design.setDatas(datas);
                        result.setDoneCount(datas.size());
                        return design;
                    }
                });
        return excelResult;
    }

    private GeneralExcelDesigner getDesign()
    {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell("学号", "studentId");
        design.addCell("姓名", "studentName");
        design.addCell("课程序号", "teachingClassCode");
        design.addCell("课程名称", "courseName");
        design.addCell("开课学院", "faculty")
                .setValueHandler((value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value); });
        design.addCell("校区", "campus")
                .setValueHandler((value, rawData, cell) -> {
                    return dictionaryService.query("X_XQ", value); });
        design.addCell("课程性质", "isElective")
                .setValueHandler((value, rawData, cell) -> {
                    return dictionaryService.query("K_BKKCXZ", value); });
        design.addCell("学分", "credits");
        design.addCell("专业", "profession").setValueHandler((value, rawData, cell) -> {
            return dictionaryService.query("G_ZY", value); });
        design.addCell("教学安排", "timeAndRoom");
        design.addCell("修读类别", "courseTakeType")
                .setValueHandler((value, rawData, cell) -> {
                    return dictionaryService.query("X_XDLX", value); });
        return design;
    }

	@Override
	@Transactional
	public void withdrawByTeachingClassId(Long teachingClassId, String status) {
        logger.info("--------------------------withdrawByTeachingClassId:"+teachingClassId+"------------------------------");
        TeachingClassVo teachingClass = teachingClassDao.getTeachingClassVo(teachingClassId);
        if(teachingClass==null) {
            throw new ParameterValidateException("教学班信息不存在");
        }
        TeachingClass t = new TeachingClass();
        t.setUpdatedAt(new Date());
        t.setStatus(status);
        t.setId(teachingClass.getId());
        //如果是关闭，则调用退课操作
        if ("1".equals(status)) {
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria().andEqualTo("calendarId", teachingClass.getCalendarId()).andEqualTo("teachingClassId", teachingClass.getId());
            List<ElcCourseTake> list = courseTakeDao.selectByExample(example);
            logger.info("---------------------------take length:"+list.size()+"--------------------------------------------------------------");
            if(CollectionUtil.isNotEmpty(list)) {
            	withdraw(list);
            }
        }
        teachingClassDao.updateByPrimaryKeySelective(t);
	}

    @Transactional
    public void withdrawNow(List<ElcCourseTake> value)
    {
        Session currentSession = SessionUtils.getCurrentSession();
        Map<String, ElcCourseTakeVo> classInfoMap = new HashMap<>();
        List<ElcLog> logList = new ArrayList<>();
        Map<String, ElcCourseTake> withdrawMap = new HashMap<>();
//        int count =classDao.clearElcNumber(value.get(0).getTeachingClassId());
        for (ElcCourseTake take : value)
        {
            Long calendarId = take.getCalendarId();
            String studentId = take.getStudentId();
            Long teachingClassId = take.getTeachingClassId();
            Integer turn = take.getTurn()!=null?take.getTurn():0;
            //删除选课记录
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria()
                    .andEqualTo("calendarId", calendarId)
                    .andEqualTo("studentId", studentId)
                    .andEqualTo("teachingClassId", teachingClassId);
            ElcCourseTake elcCourseTake = courseTakeDao.selectOneByExample(example);

            //更新选课申请数据
            electionApplyService
                    .update(studentId, calendarId, elcCourseTake.getCourseCode(),ElectRuleType.WITHDRAW);
            Example example1 = new Example(Course.class);
            example1.createCriteria().andEqualTo("code",elcCourseTake.getCourseCode());
            Course course = courseDao.selectOneByExample(example1);
            courseTakeDao.deleteByExample(example);
            //减少选课人数
            logger.info("-----------------decrElcNumber: "+teachingClassId+"---------------");

            Student stu  = studentDao.findStudentByCode(studentId);
            ElcCourseTakeVo vo = null;
            String key = calendarId + "-" + teachingClassId;
            if (!classInfoMap.containsKey(key))
            {
                vo = this.courseTakeDao
                        .getTeachingClassInfo(calendarId, teachingClassId, null);
                classInfoMap.put(key, vo);
            }
            else
            {
                vo = classInfoMap.get(key);
            }
            //ElecContextUtil.updateSelectedCourse(calendarId, studentId);
            // 记录退课日志
            if (null != vo)
            {
                String teachingClassCode = vo.getTeachingClassCode();
                ElcLog log = new ElcLog();
                log.setCalendarId(calendarId);
                log.setCourseCode(vo.getCourseCode());
                log.setCourseName(vo.getCourseName());
                log.setCreateBy(currentSession.getUid());
                log.setCreatedAt(new Date());
                log.setCreateIp(currentSession.getIp());
                log.setMode(ElcLogVo.MODE_2);
                log.setStudentId(studentId);
                log.setTeachingClassCode(teachingClassCode);
                log.setTurn(turn);
                log.setType(ElcLogVo.TYPE_2);
                logList.add(log);

                vo.setCalendarId(calendarId);
                vo.setStudentId(studentId);
//                try{
//                    elecBkService.syncRemindTime(calendarId,2,studentId,stu.getName(),course.getName()+"("+elcCourseTake.getCourseCode()+")");
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
                withdrawMap.put(
                        String
                                .format("%s-%s", vo.getCalendarId(), vo.getStudentId()),
                        vo);
            }
            else
            {
                logger.warn(
                        "not find teachingClassInfo calendarId={},teachingClassId={}",
                        calendarId,
                        teachingClassId);
            }
        }
        if (CollectionUtil.isNotEmpty(logList))
        {
            this.elcLogDao.insertList(logList);
            for (Entry<String, ElcCourseTake> entry : withdrawMap.entrySet())
            {
                ElcCourseTake take = entry.getValue();
                applicationContext.publishEvent(new ElectLoadEvent(
                        take.getCalendarId(), take.getStudentId()));
                String elecStatus = Constants.UN_ELEC;
                //更新培养的选课状态
                StudentPlanCoure studentPlanCoure = new StudentPlanCoure();
                studentPlanCoure.setStudentId(take.getStudentId());
                studentPlanCoure.setCourseCode(take.getCourseCode());
                studentPlanCoure.setElecStatus(elecStatus);
                try {
                    CultureSerivceInvoker.updateElecStatus(studentPlanCoure);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
	public PageResult<ElcCourseTakeVo> stuHonorPage(
	        PageCondition<StuHonorDto> page){
		StuHonorDto cond = page.getCondition();
        PageHelper.startPage(page.getPageNum_() ,page.getPageSize_());
        cond.setIndex(TableIndexUtil.getIndex(cond.getCalendarId()));
        Page<ElcCourseTakeVo> listPage = courseTakeDao.stuHonorPage(cond);
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
	};
    
    private void canWithdrawByFee(ElcCourseTake courseTake,Boolean isAdmin){
        if(courseTake != null){
            Integer courseTakeType = courseTake.getCourseTakeType();
            Integer paid = courseTake.getPaid();
            //重修并且缴费，只能管理员可以退课
            if(Constants.SECOND.equals(courseTakeType) && Constants.NORMAL_MODEL.equals(paid)){
                //教务员不能退课
                if(!isAdmin){
                    throw  new ParameterValidateException("学号："+courseTake.getStudentId() +",修读重修课程:"+courseTake.getCourseCode()+"已经缴费,请联系管理员退课");
                }
            }
        }
    }

}
