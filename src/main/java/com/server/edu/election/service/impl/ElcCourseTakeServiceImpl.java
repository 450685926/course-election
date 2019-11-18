package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.*;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElecResultSwitchService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.studentelec.service.impl.ElecYjsServiceImpl;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.*;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Service
public class ElcCourseTakeServiceImpl implements ElcCourseTakeService
{
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
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

    @Value("${cache.directory}")
    private String cacheDirectory;
    
    @Override
    public PageResult<ElcCourseTakeVo> listPage(
        PageCondition<ElcCourseTakeQuery> page)
    {
        ElcCourseTakeQuery cond = page.getCondition();
        List<String> includeCodes = new ArrayList<>();
        // 1体育课
        if (Objects.equals(cond.getCourseType(), ElcCourseTakeQuery.PE_COURSE_TYPE))
        {
            String findPECourses = constantsDao.findPECourses();
            if (StringUtils.isNotBlank(findPECourses))
            {
                includeCodes.addAll(Arrays.asList(findPECourses.split(",")));
            }
        }
        else if (Objects.equals(cond.getCourseType(), ElcCourseTakeQuery.EN_COURSE_TYPE))
        {// 2英语课
            String findEnglishCourses = constantsDao.findEnglishCourses();
            if (StringUtils.isNotBlank(findEnglishCourses))
            {
                includeCodes
                    .addAll(Arrays.asList(findEnglishCourses.split(",")));
            }
        }
        cond.setIncludeCourseCodes(includeCodes);
        PageHelper.startPage(page.getPageNum_() ,page.getPageSize_());
        cond.setIndex(TableIndexUtil.getIndex(cond.getCalendarId()));
        Page<ElcCourseTakeVo> listPage = courseTakeDao.listPage(cond);
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);

        return result;
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
        Integer status = add.getStatus();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        // 教学班容量与上课时间冲突校验
        if (status.intValue() != 1) {
            List<TeachingClass> teachingClasses = teachingClassDao.findTeachingClasses(teachingClassIds);
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
                            Set<String> all = new HashSet<>(size3);
                            // 上课周冲突
                            if (size3 > all.size() ) {
                                // 判断上课天是否一样
                                if (dayOfWeek.intValue() == classTimeUnit.getDayOfWeek()) {
                                    // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                                    int start = classTimeUnit.getTimeStart();
                                    int end = classTimeUnit.getTimeEnd();
                                    if ( (timeStart <= start && start <= timeEnd) || (timeStart <= end && end <= timeEnd)) {
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
                    addTake(date, calendarId, studentId, vo, mode);
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
        return StringUtils.EMPTY;
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
                int isPass = courseTakeDao.findIsPass(studentId, courseCode);
                // 判断学生是否重修
                if (isPass == 0) {
                    take.setCourseTakeType(CourseTakeType.RETAKE.type());
                } else {
                    take.setCourseTakeType(CourseTakeType.NORMAL.type());
                }
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
            //ElecContextUtil.updateSelectedCourse(calendarId, studentId);
            applicationContext
                .publishEvent(new ElectLoadEvent(calendarId, studentId));
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
    

	@Override
	public String graduateAdd(ElcCourseTakeAddDto value, String currentRole, boolean adminFlag, String projId) {
		Date date = new Date();
		if (CollectionUtil.isEmpty(value.getStudentIds())) {
			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.noStudent",I18nUtil.getMsg("elecResultSwitch.noStudent")));
		}
    	//如果当前操作人是老师
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
        return this.graduateAddCourse(value);
	}
    @Transactional
    private String graduateAddCourse(ElcCourseTakeAddDto add)
    {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        Integer mode = add.getMode();
        for (String studentId : studentIds)
        {
            for (int i = 0; i < teachingClassIds.size(); i++)
            {
                Long teachingClassId = teachingClassIds.get(i);
                ElcCourseTakeVo vo = courseTakeDao
                    .getTeachingClassInfo(calendarId, teachingClassId, null);
                if (null != vo && vo.getCourseCode() != null)
                {
                	graduateAddCourseTake(date, calendarId, studentId, vo, mode);
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
		cond.setStudentCodes(collect);
		PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<Student4Elc> listPage = studentDao.getStudent4CulturePlan(cond);
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
		condition.getCondition().setProjectId(currentSession.getCurrentManageDptId());
		
//		PageResult<ElcCourseTakeVo> list = listPage(condition);
		PageResult<ElcCourseTakeVo> list = courseTakeNameList(condition);
		
		List<ElcCourseTakeVo> list2 = list.getList();
		if (CollectionUtil.isNotEmpty(list2)) {
			Iterator<ElcCourseTakeVo> iterator = list2.iterator();
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
			}
		}
		
		list2.sort(Comparator.comparing(ElcCourseTakeVo::getStudentCode));
		
		List<ElcCourseTakeNameListVo> nameList = new ArrayList<>();
    	for (ElcCourseTakeVo elcCourseTakeVo : list2) {
    		ElcCourseTakeNameListVo elcCourseTakeNameListVo = new ElcCourseTakeNameListVo();
    		elcCourseTakeNameListVo.setElcCourseTakeVo(elcCourseTakeVo);
    		Student stu = studentDao.findStudentByCode(elcCourseTakeVo.getStudentId());
    		elcCourseTakeNameListVo.setStudentInfo(stu);
    		nameList.add(elcCourseTakeNameListVo);
		}
    	PageResult<ElcCourseTakeNameListVo> result = new PageResult<>();
    	result.setList(nameList);
    	result.setPageNum_(list.getPageNum_());
    	result.setPageSize_(list.getPageSize_());
    	result.setTotal_(list.getTotal_());
		return result;
	}
	
	/** 研究生选课结果处理学生名单 */
	public PageResult<ElcCourseTakeVo> courseTakeNameList(PageCondition<ElcCourseTakeQuery> condition){
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcCourseTakeVo> listPage = courseTakeDao.courseTakeNameList(condition.getCondition());
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
	}

    @Override
    public PageResult<ElcStudentVo> addCourseList(PageCondition<ElcCourseTakeQuery> condition) {
        ElcCourseTakeQuery courseTakeQuerytudentVo = condition.getCondition();
        String studentId = courseTakeQuerytudentVo.getStudentId();
        Long calendarId = courseTakeQuerytudentVo.getCalendarId();
        String keyword = courseTakeQuerytudentVo.getKeyword();
        // 获取学生所有已修课程成绩
        List<ScoreStudentResultVo> stuScore = ScoreServiceInvoker.findStuScore(studentId);
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
            planCourseTypeDtos.addAll(list);
        }
        List<String> allCourseCode = planCourseTypeDtos.stream().map(PlanCourseTypeDto::getCourseCode).collect(Collectors.toList());
        //获取学生本学期已选的课程
        List<String> codes = courseTakeDao.findSelectedCourseCode(studentId, calendarId);
        // 获取学生通过课程集合
        List<ScoreStudentResultVo> collect = stuScore.stream().filter(item -> item.getIsPass().intValue() == 1).collect(Collectors.toList());
        List<String> passedCourseCodes = collect.stream().map(ScoreStudentResultVo::getCourseCode).collect(Collectors.toList());
        // 组合学生已选课程和考试通过课程
        codes.addAll(passedCourseCodes);
        //剔除培养计划课程集合中学生已通过的课程，获取学生还需要修读的课程
        List<String> elcCourses = allCourseCode.stream()
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
    public CourseConflictVo addCourse(AddCourseDto courseDto) {
        Session session = SessionUtils.getCurrentSession();
        Long calendarId = courseDto.getCalendarId();
        // 判断登录角色及选课维护开关状态
        Integer chooseObj = getChooseObj(session, calendarId);
        // 查询要添加教学班的课程信息
        List<Long> teachingClassIds = courseDto.getTeachingClassId();
        // 查询要添加教学班的课程信息
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassIds);
        int size = elcStudentVos.size();
        // 重修课程集合
        List<ElcStudentVo> failedClass = new ArrayList<>(size);
        String studentId = courseDto.getStudentId();
        Map<Long, String> addMap = new HashMap<>(size);
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            String courseCode = elcStudentVo.getCourseCode();
            addMap.put(elcStudentVo.getTeachingClassId(), courseCode + elcStudentVo.getCourseName());
            int count = courseTakeDao.courseCount(courseCode, studentId);
            if(count != 0) {
                elcStudentVo.setCourseTakeType(2);
                failedClass.add(elcStudentVo);
            }else {
                elcStudentVo.setCourseTakeType(1);
            }
        }
        // 如果含有重修课程且代选课对象为教务员，则需根据重修规则对课程进行判断
        if (CollectionUtil.isNotEmpty(failedClass) && chooseObj.intValue() == 2) {
            Student student = studentDao.findStudentByCode(studentId);
            Integer maxCount = retakeCourseCountDao.findRetakeCount(student);
            if (maxCount == null) {
                throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.countLimitError"));
            }
            Set<String> retakeCourseCode = courseTakeDao.findRetakeCount(studentId);
            if (retakeCourseCode.size() + failedClass.size() > maxCount) {
                throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.countLimit"));
            }
        }
        // 查询已选课程上课时间
        List<ElcCourseTakeVo> elcCourseTakeVos = courseTakeDao.findElcCourseTakeByStudentId(courseDto.getStudentId(), calendarId);
        if (CollectionUtil.isNotEmpty(elcCourseTakeVos)) {
            Map<Long, String> selectedMap = elcCourseTakeVos.stream().collect(Collectors.toMap(ElcCourseTakeVo::getTeachingClassId, s -> s.getCourseCode() + s.getCourseName()));
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
                CourseConflictVo courseConflictVo = new CourseConflictVo();
                courseConflictVo.setAddCourse(addCourses);
                courseConflictVo.setSelectedCourse(selectedCourses);
                return courseConflictVo;
            }
        }

        List<ElcCourseTake> elcCourseTakes = new ArrayList<>(size);
        List<ElcLog> elcLogs = new ArrayList<>(size);
        // 保存数据
        addToList(session, chooseObj, courseDto, elcStudentVos, elcCourseTakes, elcLogs);
        saveCourse(courseDto, elcCourseTakes, elcLogs);
        return null;
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
                        if ( (timeStart <= start && start <= timeEnd) || (timeStart <= end && end <= timeEnd)) {
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
            elcCourseTake.setCourseTakeType(elcStudentVo.getCourseTakeType());
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

}
