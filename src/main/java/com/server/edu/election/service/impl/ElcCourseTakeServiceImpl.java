package com.server.edu.election.service.impl;

import java.io.FileOutputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.rest.RestResult;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.*;
import com.server.edu.election.vo.ElcStudentVo;
import com.server.edu.util.CalUtil;
import com.server.edu.util.FileUtil;
import com.server.edu.util.excel.ExcelWriterUtil;
import com.server.edu.util.excel.GeneralExcelDesigner;
import com.server.edu.util.excel.GeneralExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElcResultSwitch;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElecResultSwitchService;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.vo.ElcCourseTakeNameListVo;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ElcCourseTakeServiceImpl implements ElcCourseTakeService
{
    Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate = RestTemplateBuilder.create();
    
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
    private ApplicationContext applicationContext;
    
    @Autowired
    private ElecResultSwitchService elecResultSwitchService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ScoreStudentScoreDao scoreStudentScoreDao;

    @Value("${cache.directory}")
    private String cacheDirectory;

    @Override
    public PageResult<ElcCourseTakeVo> listPage(
        PageCondition<ElcCourseTakeQuery> page)
    {
        ElcCourseTakeQuery cond = page.getCondition();
        List<String> includeCodes = new ArrayList<>();
        // 1体育课
        if (Objects.equals(cond.getCourseType(), 1))
        {
            String findPECourses = constantsDao.findPECourses();
            if (StringUtils.isNotBlank(findPECourses))
            {
                includeCodes.addAll(Arrays.asList(findPECourses.split(",")));
            }
        }
        else if (Objects.equals(cond.getCourseType(), 2))
        {// 2英语课
            String findEnglishCourses = constantsDao.findEnglishCourses();
            if (StringUtils.isNotBlank(findEnglishCourses))
            {
                includeCodes
                    .addAll(Arrays.asList(findEnglishCourses.split(",")));
            }
        }
        cond.setIncludeCourseCodes(includeCodes);
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<ElcCourseTakeVo> listPage = courseTakeDao.listPage(cond);
        setTeachingArrange(listPage);
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
    }

    @Override
    public PageResult<ElcCourseTakeVo> allSelectedCourse(PageCondition<String> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcCourseTakeVo> listPage = courseTakeDao.allSelectedCourse(condition.getCondition());
        setTeachingArrange(listPage);
        return new PageResult<>(listPage);
    }

    private void setTeachingArrange(Page<ElcCourseTakeVo> elcCourseTakeVos) {
        if (CollectionUtil.isNotEmpty(elcCourseTakeVos)) {
            List<Long> ids = elcCourseTakeVos.stream().map(ElcCourseTakeVo::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = getTimeById(ids);
            Map<Long, List<TimeTableMessage>> listMap = tableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                List<TimeTableMessage> timeTableMessages = listMap.get(elcCourseTakeVo.getTeachingClassId());
                if (CollectionUtil.isNotEmpty(timeTableMessages)) {
                    List<String> timeAndRooms = timeTableMessages.stream().map(TimeTableMessage::getTimeAndRoom).collect(Collectors.toList());
                    elcCourseTakeVo.setCourseArrange(String.join(",", timeAndRooms));
                }
            }
        }
    }

    private List<TimeTableMessage> getTimeById(List<Long> teachingClassId) {
        List<TimeTableMessage> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(teachingClassId)) {
            List<ClassTeacherDto> classTimeAndRoom = courseTakeDao.findClassTimeAndRoom(teachingClassId);
            if (CollectionUtil.isNotEmpty(classTimeAndRoom)) {
                for (ClassTeacherDto classTeacherDto : classTimeAndRoom) {
                    Integer dayOfWeek = classTeacherDto.getDayOfWeek();
                    Integer timeStart = classTeacherDto.getTimeStart();
                    Integer timeEnd = classTeacherDto.getTimeEnd();
                    String weekNumber = classTeacherDto.getWeekNumberStr();
                    String[] str = weekNumber.split(",");

                    List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                    List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[]{}));
                    String weekNumStr = weekNums.toString();//周次
                    String weekstr = findWeek(dayOfWeek);//星期
                    String timeStr = weekstr + " " + timeStart + "-" + timeEnd + "节" + weekNumStr + dictionaryService.query("X_XQ",classTeacherDto.getCampus());
                    TimeTableMessage time = new TimeTableMessage();
                    time.setTeachingClassId(classTeacherDto.getTeachingClassId());
                    time.setTimeAndRoom(timeStr);
                    list.add(time);
                }
            }
        }
        return list;
    }

    public String findWeek(Integer number) {
        String week = "";
        switch (number) {
            case 1:
                week = "星期一";
                break;
            case 2:
                week = "星期二";
                break;
            case 3:
                week = "星期三";
                break;
            case 4:
                week = "星期四";
                break;
            case 5:
                week = "星期五";
                break;
            case 6:
                week = "星期六";
                break;
            case 7:
                week = "星期日";
                break;
        }
        return week;
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
        for (String studentId : studentIds)
        {
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
    private void addTake(Date date, Long calendarId, String studentId,
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
            take.setCourseTakeType(CourseTakeType.NORMAL.type());
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
	public String graduateAdd(ElcCourseTakeAddDto value, int realType) {
		Date date = new Date();
		if (CollectionUtil.isEmpty(value.getStudentIds())) {
			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.noStudent",I18nUtil.getMsg("elecResultSwitch.noStudent")));
		}
    	//如果当前操作人是老师
        if (realType == UserTypeEnum.TEACHER.getValue())
        {
        	//判断选课结果开关状态
    		ElcResultSwitch elcResultSwitch = elecResultSwitchService.getSwitch(value.getCalendarId());
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
		return this.add(value);
	}

    
    @Transactional
    @Override
    public void withdraw(List<ElcCourseTake> value)
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
	public void graduateWithdraw(Long calendarId, Long teachingClassId,String courseCode, List<String> students, int realType) {
    	
    	Date date = new Date();
    	//如果当前操作人是老师
        if (realType == UserTypeEnum.TEACHER.getValue())
        {
        	//判断选课结果开关状态
    		ElcResultSwitch elcResultSwitch = elecResultSwitchService.getSwitch(calendarId);
    		if (elcResultSwitch.getStatus() == Constants.ZERO) {
    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled",I18nUtil.getMsg("elecResultSwitch.notEnabled")));
    		} else if(date.getTime() > elcResultSwitch.getOpenTimeEnd().getTime() ||  date.getTime() < elcResultSwitch.getOpenTimeStart().getTime()){
    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.operationalerror",I18nUtil.getMsg("elecResultSwitch.operationalerror")));
    		}
    		
    		//判断课程性质
    		
    		Example example = new Example(Course.class);
    		Criteria createCriteria = example.createCriteria();
    		createCriteria.andEqualTo("code",courseCode);
    		Course course = courseDao.selectOneByExample(example);
    		if(course.getIsElective().intValue() == Constants.ONE){
    			throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.noPower",I18nUtil.getMsg("elecResultSwitch.noPower")));
    		}

        }
        List<ElcCourseTake> value = new ArrayList<ElcCourseTake>();
        for (String studentId : students) {
        	ElcCourseTake elcCourseTake = new ElcCourseTake();
        	elcCourseTake.setStudentId(studentId);
        	elcCourseTake.setCalendarId(calendarId);
        	elcCourseTake.setCourseCode(courseCode);
        	elcCourseTake.setTeachingClassId(teachingClassId);
        	value.add(elcCourseTake);
		}
		this.withdraw(value);
	}
    

	@Override
	public PageResult<Student4Elc> getGraduateStudentForCulturePlan(PageCondition<ElcResultQuery> page) {
		ElcResultQuery cond = page.getCondition();
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
		PageResult<ElcCourseTakeVo> list = listPage(condition);
		List<ElcCourseTakeNameListVo> nameList = new ArrayList<>();
    	for (ElcCourseTakeVo elcCourseTakeVo : list.getList()) {
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

    @Override
    public PageResult<ElcStudentVo> addCourseList(PageCondition<ElcStudentVo> condition) {
        ElcStudentVo elcStudentVo = condition.getCondition();
        String studentId = elcStudentVo.getStudentId();
        //通过研究生培养计划获取学生需要修的课程
        String path = ServicePathEnum.CULTURESERVICE.getPath("/culturePlan/getCourseCode?id={id}&isPass={isPass}");
        RestResult<List<String>> restResult = restTemplate.getForObject(path, RestResult.class, studentId, 0);
        List<String> allCourseCode = restResult.getData();
        Page<ElcStudentVo> elcStudentVos = new Page<ElcStudentVo>();
        List<String> elcCourses = new ArrayList<>();
        //查询未通过的教学班和已选择的教学班
        List<String> failedTeachingClassIds = scoreStudentScoreDao.findFailedTeachingClassId(studentId);
        List<String> selectedTeachingClassIds = courseTakeDao.findSelectedTeachingClassId(studentId);
        if (CollectionUtil.isNotEmpty(selectedTeachingClassIds)) {
            //剔除已选择的教学班中未通过的教学班
            List<String> collect = selectedTeachingClassIds.stream().filter(item -> !failedTeachingClassIds.contains(item)).collect(Collectors.toList());
            //查询已选择的课程代码
            List<String> courseCodes = courseTakeDao.findCourseCode(collect);
            if (CollectionUtil.isNotEmpty(allCourseCode)){
                //获取学生还需要修的课程即为可选课程
                elcCourses = allCourseCode.stream().filter(item -> !courseCodes.contains(item)).collect(Collectors.toList());
            }
        }
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        elcStudentVos = courseTakeDao.findAddCourseList(elcCourses, elcStudentVo.getCalendarId());
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    @Override
    @Transactional
    public Integer addCourse(AddAndRemoveCourseDto courseDto) {
        List<Long> teachingClassIds = courseDto.getTeachingClassId();
        // 查询已选课程上课时间
        List<String> ids = courseTakeDao.findTeachingClassIdByStudentId(courseDto.getStudentId(), courseDto.getCalendarId());
        List<TimeTableMessage> timeTableMessages = courseTakeDao.findCourseArrange(ids);
        for (Long teachingClassId : teachingClassIds) {
            // 查询要添加课程的上课时间
            List<TimeTableMessage> timeTable= courseTakeDao.findCourseArrangeByTeachingClassId();
            if (CollectionUtil.isNotEmpty(timeTable)) {
                //获取要添加课程的上课周
                String[] week = timeTable.get(0).getWeekNum().split(",");
                //说明上课时间未安排，暂时不冲突，可以添加
                if (week.length == 0) {
                    break;
                }
                // 循环已选课程上课周，判断上课周是否冲突
                for (TimeTableMessage timeTableMessage : timeTableMessages) {
                    String[] split = timeTableMessage.getWeekNum().split(",");
                    if (split.length == 0) {
                        break;
                        }
                    Set<String> set = new HashSet<>();
                    set.addAll(Arrays.asList(week));
                    set.addAll(Arrays.asList(split));
                    // 数组长度之和大于set集合长度，说明上课周重复
                    if (week.length + split.length > set.size()) {
                        int dayOfWeek = timeTableMessage.getDayOfWeek().intValue();
                        int timeStart = timeTableMessage.getTimeStart().intValue();
                        int timeEnd = timeTableMessage.getTimeEnd().intValue();
                        for (TimeTableMessage tableMessage : timeTable) {
                            // 比较上课天是否相同
                            if (dayOfWeek == tableMessage.getDayOfWeek().intValue()) {
                                // 上课天相同，比价上课节次
                                int start = timeTableMessage.getTimeStart().intValue();
                                int end = tableMessage.getTimeEnd().intValue();
                                // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                                if ( (timeStart <= start && start <= timeEnd) || (timeStart <= end && end <= timeEnd)) {
                                    throw new ParameterValidateException(tableMessage.getCourseName() + "与已选课程上课时间冲突");
                                }
                            }
                        }
                    }
                }
            }
        }
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassIds);
        List<ElcCourseTake> elcCourseTakes = new ArrayList<>();
        List<ElcLog> elcLogs = new ArrayList<>();
        addToList(courseDto, elcStudentVos, elcCourseTakes, elcLogs);
        Integer count = courseTakeDao.saveCourseTask(elcCourseTakes);
        if (count != 0) {
            elcLogDao.saveCourseLog(elcLogs);
        }
        return count;
    }

    @Override
    @Transactional
    public Integer removedCourse(AddAndRemoveCourseDto courseDto) {
        List<Long> teachingClassId = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassId);
        int count = courseTakeDao.deleteCourseTask(teachingClassId, courseDto.getStudentId());
        if (count != 0) {
            List<ElcLog> elcLogs = new ArrayList<>();
            addToList(courseDto, elcStudentVos, elcLogs);
            elcLogDao.saveCourseLog(elcLogs);
        }
        return count;
    }

    @Override
    public PageResult<ElcStudentVo> removedCourseList(PageCondition<String> condition) {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcStudentVo> elcStudentVos = courseTakeDao.findRemovedCourseList(condition.getCondition());
        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    /*
     * 导出学生选课信息
     */
    @Override
    public String exportElcStudentInfo(PageCondition<ElcCourseTakeQuery> condition) throws Exception {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcStudentCourseDto> studentCourses = courseTakeDao.findElcStudentCourse(condition.getCondition());
        if (studentCourses != null) {
            GeneralExcelDesigner design = getDesignElcStudent();
            design.setDatas(studentCourses);
            ExcelWriterUtil generalExcelHandle;
            generalExcelHandle = GeneralExcelUtil.generalExcelHandle(design);
            FileUtil.mkdirs(cacheDirectory);
            String fileName = "elcStudentInfo.xls";
            String path = cacheDirectory + fileName;
            generalExcelHandle.writeExcel(new FileOutputStream(path));
            return fileName;
        }
        return "";
    }

    private GeneralExcelDesigner getDesignElcStudent() {
        GeneralExcelDesigner design = new GeneralExcelDesigner();
        design.setNullCellValue("");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentCode"), "studentCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.studentName"), "studentName");
        design.addCell(I18nUtil.getMsg("elcCourseUphold.teachingClassName"), "teachingClassName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.grade"), "grade");
        String lang = SessionUtils.getLang();
        design.addCell(I18nUtil.getMsg("rebuildCourse.trainingLevel"), "trainingLevel").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_PYCC", value, lang);
                });
        design.addCell(I18nUtil.getMsg("noElection.trainingCategory"), "trainingCategory").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_PYLB", value, lang);
                });
        design.addCell(I18nUtil.getMsg("noElection.degreeType"), "degreeType").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_XWLX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("noElection.formLearning"), "formLearning").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_XXXS", value, lang);
                });
        design.addCell(I18nUtil.getMsg("exemptionApply.faculty"), "faculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("exemptionApply.major"), "profession").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("G_ZY", value, lang);
                });
        design.addCell(I18nUtil.getMsg("rollBookManage.direction"), "researchDirection").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YJFX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("elcCourseUphold.courseFaculty"), "courseFaculty").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_YX", value, lang);
                });
        design.addCell(I18nUtil.getMsg("elcCourseUphold.nature"), "nature").setValueHandler(
                (value, rawData, cell) -> {
                    return dictionaryService.query("X_KCXZ", value, lang);
                });
        design.addCell(I18nUtil.getMsg("rebuildCourse.courseIndex"), "classCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseCode"), "courseCode");
        design.addCell(I18nUtil.getMsg("exemptionApply.courseName"), "courseName");
        design.addCell(I18nUtil.getMsg("rebuildCourse.revisionategory"), "courseTakeType").setValueHandler(
                (value, rawData, cell) -> {
                    String resp = "";
                    switch (value) {
                        case "1":
                            resp = "正常修读";
                            break;
                        case "2":
                            resp = "重修";
                            break;
                        case "3":
                            resp = "免修不免考";
                            break;
                        case "4":
                            resp = "免修";
                            break;
                    }
                    return resp;
                });
        ;
        design.addCell(I18nUtil.getMsg("elcCourseUphold.chooseObj"), "chooseObj").setValueHandler(
                (value, rawData, cell) -> {
                    return "1".equals(value) ? "自选" : "代选";
                });
        return design;
    }

    private void addToList(AddAndRemoveCourseDto courseDto, List<ElcStudentVo> elcStudentVos, List<ElcLog> elcLogs) {
        String studentId = courseDto.getStudentId();
        String id = courseDto.getId();
        String name = courseDto.getName();
        Long calendarId = courseDto.getCalendarId();
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            ElcLog elcLog = new ElcLog();
            elcLog.setStudentId(studentId);
            elcLog.setCourseCode(elcStudentVo.getCourseCode());
            elcLog.setCourseName(elcStudentVo.getCourseName());
            elcLog.setTeachingClassCode(elcStudentVo.getClassCode());
            elcLog.setCalendarId(calendarId);
            elcLog.setType(2);
            elcLog.setMode(2);
            elcLog.setCreateBy(id);
            elcLog.setCreateName(name);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
    }

    private void addToList(AddAndRemoveCourseDto courseDto, List<ElcStudentVo> elcStudentVos, List<ElcCourseTake> elcCourseTakes, List<ElcLog> elcLogs) {
        String studentId = courseDto.getStudentId();
        Integer chooseObj = courseDto.getChooseObj();
        String id = courseDto.getId();
        String name = courseDto.getName();
        Long calendarId = courseDto.getCalendarId();
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            ElcCourseTake elcCourseTake = new ElcCourseTake();
            elcCourseTake.setStudentId(studentId);
            elcCourseTake.setCalendarId(calendarId);
            String courseCode = elcStudentVo.getCourseCode();
            elcCourseTake.setCourseCode(courseCode);
            elcCourseTake.setTeachingClassId(elcStudentVo.getTeachingClassId());
            elcCourseTake.setMode(2);
            elcCourseTake.setChooseObj(chooseObj);
            elcCourseTake.setCreatedAt(new Date());
            int count = courseTakeDao.courseCount(courseCode, studentId);
            if(count != 0) {
                elcCourseTake.setCourseTakeType(2);
            } else {
                elcCourseTake.setCourseTakeType(1);
            }
            elcCourseTake.setTurn(0);
            elcCourseTakes.add(elcCourseTake);

            ElcLog elcLog = new ElcLog();
            elcLog.setStudentId(studentId);
            elcLog.setCourseCode(elcStudentVo.getCourseCode());
            elcLog.setCourseName(elcStudentVo.getCourseName());
            elcLog.setTeachingClassCode(elcStudentVo.getClassCode());
            elcLog.setCalendarId(calendarId);
            elcLog.setType(1);
            elcLog.setMode(2);
            elcLog.setCreateBy(id);
            elcLog.setCreateName(name);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
    }

    private void setCourseArrange(Page<ElcStudentVo> elcStudentVos) {
        if (CollectionUtil.isNotEmpty(elcStudentVos)) {
            List<Long> ids = elcStudentVos.stream().map(ElcStudentVo::getTeachingClassId).collect(Collectors.toList());
            List<TimeTableMessage> tableMessages = getTimeById(ids);
            Map<Long, List<TimeTableMessage>> listMap = tableMessages.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            for (ElcStudentVo elcCourseTakeVo : elcStudentVos) {
                List<TimeTableMessage> timeTableMessages = listMap.get(elcCourseTakeVo.getTeachingClassId());
                if (CollectionUtil.isNotEmpty(timeTableMessages)) {
                    List<String> timeAndRooms = timeTableMessages.stream().map(TimeTableMessage::getTimeAndRoom).collect(Collectors.toList());
                    elcCourseTakeVo.setCourseArrange(String.join(",", timeAndRooms));
                }
            }
        }
    }
}
