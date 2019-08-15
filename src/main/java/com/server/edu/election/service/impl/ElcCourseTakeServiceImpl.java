package com.server.edu.election.service.impl;

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

import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.*;
import com.server.edu.election.entity.*;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.util.WeekUtil;
import com.server.edu.election.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.springmvc.reference.RestTemplateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.ServicePathEnum;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.service.ElecResultSwitchService;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CalUtil;
import com.server.edu.util.CollectionUtil;

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
    private RetakeCourseCountDao retakeCourseCountDao;

    @Autowired
    private TeachingClassDao teachingClassDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TeachingClassTeacherDao teachingClassTeacherDao;

    @Autowired
    private ElectionRuleDao electionRuleDao;

    @Autowired
    private RetakeCourseSetDao retakeCourseSetDao;

    @Autowired
    private ElecResultSwitchService elecResultSwitchService;

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
	public void graduateWithdraw(ElcCourseTakeWithDrawDto value, int realType) {
    	
    	Date date = new Date();
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
        List<ElcCourseTake> values = new ArrayList<ElcCourseTake>();
        for (String studentId : value.getStudents()) {
        	ElcCourseTake elcCourseTake = new ElcCourseTake();
        	elcCourseTake.setStudentId(studentId);
        	elcCourseTake.setCalendarId(value.getCalendarId());
        	elcCourseTake.setCourseCode(value.getCourseCode());
        	elcCourseTake.setTeachingClassId(value.getTeachingClassId());
        	values.add(elcCourseTake);
		}
		this.withdraw(values);
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
		Session currentSession = SessionUtils.getCurrentSession();
		condition.getCondition().setProjectId(currentSession.getCurrentManageDptId());
		if (StringUtils.isNotEmpty(condition.getCondition().getIncludeCourseCode())) {
			condition.getCondition().getIncludeCourseCodes().add(condition.getCondition().getIncludeCourseCode());
		}
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
    public PageResult<ElcStudentVo> addCourseList(PageCondition<ElcCourseTakeQuery> condition) {
        ElcCourseTakeQuery courseTakeQuerytudentVo = condition.getCondition();
        String studentId = courseTakeQuerytudentVo.getStudentId();
        Long calendarId = courseTakeQuerytudentVo.getCalendarId();
        String keyword = courseTakeQuerytudentVo.getKeyword();
        // 获取学生所有已修课程成绩
        List<StudentScoreVo> stuScoreBest = ScoreServiceInvoker.findStuScoreBest(studentId);
        // 获取学生通过课程集合
        List<StudentScoreVo> collect = stuScoreBest.stream().filter(item -> item.getIsPass().intValue() == 1).collect(Collectors.toList());
        List<String> passedCourseCodes = collect.stream().map(StudentScoreVo::getCourseCode).collect(Collectors.toList());
        //通过研究生培养计划获取学生所有需要修读的课程
        String path = ServicePathEnum.CULTURESERVICE.getPath("/culturePlan/getCourseCode?id={id}&isPass={isPass}");
        RestResult<List<String>> restResult = restTemplate.getForObject(path, RestResult.class, studentId, 0);
        List<String> allCourseCode = restResult.getData();
        if (allCourseCode == null) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.planCultureError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        //获取学生本学期已选的课程
        List<String> codes = courseTakeDao.findSelectedCourseCode(studentId, calendarId);
        //剔除培养计划课程集合中学生已通过的课程，获取学生还需要修读的课程
        List<String> elcCourses = allCourseCode.stream()
                .filter(item -> !passedCourseCodes.contains(item) && !codes.contains(item))
                .collect(Collectors.toList());
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Session session = SessionUtils.getCurrentSession();
        Page<ElcStudentVo> elcStudentVos;
        if (StringUtils.equals(session.getCurrentRole(), "1") && session.isAdmin()) {
            elcStudentVos = courseTakeDao.findAddCourseList(elcCourses, calendarId, keyword);
        } else if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            elcStudentVos = courseTakeDao.findAddCourseList(elcCourses, calendarId, keyword);
        } else {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.loginError",I18nUtil.getMsg("elecResultSwitch.operationalerror")));
        }

        setCourseArrange(elcStudentVos);
        return new PageResult<>(elcStudentVos);
    }

    @Override
    @Transactional
    public Integer addCourse(AddCourseDto courseDto) {
        Session session = SessionUtils.getCurrentSession();
        String uid = session.getUid();
        String name = session.getName();
        Long calendarId = courseDto.getCalendarId();
        // 判断登录角色及选课维护开关状态
        Integer chooseObj = getChooseObj(session, calendarId);
        // 查询要添加教学班的课程信息
        List<Long> teachingClassIds = courseDto.getTeachingClassId();
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassIds);
        int size = elcStudentVos.size();
        // 重修课程集合
        List<ElcStudentVo> failedClass = new ArrayList<>(size);
        // 正常修读课程集合
        List<ElcStudentVo> normalClass = new ArrayList<>(size);
        String studentId = courseDto.getStudentId();
        for (ElcStudentVo elcStudentVo : elcStudentVos) {
            String courseCode = elcStudentVo.getCourseCode();
            int count = courseTakeDao.courseCount(courseCode, studentId);
            int i = 0;
            if(count != 0) {
                elcStudentVo.setCourseTakeType(2);
                failedClass.add(elcStudentVo);
            }else {
                elcStudentVo.setCourseTakeType(1);
                normalClass.add(elcStudentVo);
            }
        }
        // 查询已选课程上课时间,判断上课时间是否冲突
        List<Long> ids = courseTakeDao.findTeachingClassIdByStudentId(courseDto.getStudentId(), calendarId);
        List<TimeTableMessage> selectCourseArrange = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            selectCourseArrange = courseTakeDao.findCourseArrange(ids);
        }
        // 如果含有重修课程且代选课对象为教务员，则需根据重修规则对课程进行判断
        if (CollectionUtil.isNotEmpty(failedClass) && chooseObj.intValue() == 2) {
            Student student = studentDao.findStudentByCode(studentId);
            List<Integer> courseRole = getCourseRole(calendarId, session.getCurrentManageDptId());
            failedClassCompare(failedClass, selectCourseArrange, courseRole, student);
            Integer maxCount = retakeCourseCountDao.findRetakeCount(student);
            if (maxCount == null) {
                throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.countLimitError"));
            }
            Set<String> retakeCount = courseTakeDao.findRetakeCount(studentId);
            if (retakeCount.size() + failedClass.size() > maxCount) {
                throw new ParameterValidateException(I18nUtil.getMsg("rebuildCourse.countLimit"));
            }
        }
        // 正常修读课程上课时间冲突判断
        getConflict(normalClass, selectCourseArrange);
        List<ElcCourseTake> elcCourseTakes = new ArrayList<>();
        List<ElcLog> elcLogs = new ArrayList<>();
        // 保存数据
        addToList(uid, name, chooseObj, courseDto, elcStudentVos, elcCourseTakes, elcLogs);
        Integer count = courseTakeDao.saveCourseTask(elcCourseTakes);
        if (count.intValue() != teachingClassIds.size()) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.addCourseError"));
        }
        teachingClassDao.increElcNumberList(teachingClassIds);
        Integer logCount = elcLogDao.saveCourseLog(elcLogs);
        if (logCount != count) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.addCourseLogError"));
        }
        return count;
    }

    @Override
    @Transactional
    public Integer removedCourse(List<ElcCourseTake> value) {
        Session session = SessionUtils.getCurrentSession();
        String id = session.getUid();
        String name = session.getName();
        // 验证选课维护开关状态
        getChooseObj(session, value.get(0).getCalendarId());

        int count = courseTakeDao.deleteByCourseTask(value);
        List<Long> teachingClassIds = value.stream().map(ElcCourseTake::getTeachingClassId).collect(Collectors.toList());
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
        List<ElcStudentVo> elcStudentVos = courseTakeDao.findCourseInfo(teachingClassIds);
        int size = elcStudentVos.size();
        if (elcStudentVos.size() != delSize) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.teachingTaskError"));
        }
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
            elcLog.setCreateBy(id);
            elcLog.setCreateName(name);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
        Integer logCount = elcLogDao.saveCourseLog(elcLogs);
        if (logCount != delSize) {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.addCourseLogError"));
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
            MultiValueMap<Long, String> nameMap = new LinkedMultiValueMap<>(size);
            for (TimeTableMessage tableMessage : tableMessages) {
                Integer dayOfWeek = tableMessage.getDayOfWeek();
                Integer timeStart = tableMessage.getTimeStart();
                Integer timeEnd = tableMessage.getTimeEnd();
                String roomID = tableMessage.getRoomId();
                String teacherCode = tableMessage.getTeacherCode();
                Long teachingClassId = tableMessage.getTeachingClassId();
                if (teacherCode != null) {
                    String[] split = teacherCode.split(",");
                    for (String s : split) {
                        String name = teachingClassTeacherDao.findTeacherName(s);
                        nameMap.add(teachingClassId, name);
                    }
                }
                String[] str = tableMessage.getWeekNum().split(",");
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ ClassroomCacheUtil.getRoomName(roomID);
                arrangeMap.add(teachingClassId, timeStr);
            }
            for (ElcCourseTakeVo elcCourseTakeVo : elcCourseTakeVos) {
                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(elcCourseTakeVo.getCalendarId());
                elcCourseTakeVo.setCalendarName(schoolCalendar.getFullName());
                Long teachingClassId = elcCourseTakeVo.getTeachingClassId();
                List<String> arr = arrangeMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(arr)) {
                    elcCourseTakeVo.setCourseArrange(String.join(",", arr));
                }
                List<String> names = nameMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(names)) {
                    Set<String> set = new HashSet<>(names.size());
                    set.addAll(names);
                    elcCourseTakeVo.setTeachingName(String.join(",", set));
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
            MultiValueMap<Long, String> nameMap = new LinkedMultiValueMap<>(size);
            for (TimeTableMessage tableMessage : tableMessages) {
                Integer dayOfWeek = tableMessage.getDayOfWeek();
                Integer timeStart = tableMessage.getTimeStart();
                Integer timeEnd = tableMessage.getTimeEnd();
                String roomID = tableMessage.getRoomId();
                String teacherCode = tableMessage.getTeacherCode();
                Long teachingClassId = tableMessage.getTeachingClassId();
                if (teacherCode != null) {
                    String[] split = teacherCode.split(",");
                    for (String s : split) {
                        String name = teachingClassTeacherDao.findTeacherName(s);
                        nameMap.add(teachingClassId, name);
                    }
                }
                String[] str = tableMessage.getWeekNum().split(",");
                List<Integer> weeks = Arrays.asList(str).stream().map(Integer::parseInt).collect(Collectors.toList());
                List<String> weekNums = CalUtil.getWeekNums(weeks.toArray(new Integer[] {}));
                String weekNumStr = weekNums.toString();//周次
                String weekstr = WeekUtil.findWeek(dayOfWeek);//星期
                String timeStr=weekstr+" "+timeStart+"-"+timeEnd+"节"+weekNumStr+ ClassroomCacheUtil.getRoomName(roomID);
                arrangeMap.add(teachingClassId, timeStr);
            }
            for (ElcStudentVo elcStudentVo : elcStudentVos) {
                Long teachingClassId = elcStudentVo.getTeachingClassId();
                List<String> times = arrangeMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(times)) {
                    elcStudentVo.setCourseArrange(String.join(",", times));
                }
                List<String> names = nameMap.get(teachingClassId);
                if (CollectionUtil.isNotEmpty(names)) {
                    Set<String> set = new HashSet<>(names.size());
                    set.addAll(names);
                    elcStudentVo.setTeacherName(String.join(",", set));
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
            boolean switchStatus = elecResultSwitchService.getSwitchStatus(calendarId, "2");
            // 教务员需判断选课开关是否开启
            if (!switchStatus) {
                throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled"));
            }
        } else {
            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.loginError"));
        }
        boolean switchStatus = elecResultSwitchService.getSwitchStatus(calendarId, session.getCurrentManageDptId());
        // 教务员需判断选课开关是否开启
        if (!switchStatus) {
            throw new ParameterValidateException(I18nUtil.getMsg("elecResultSwitch.notEnabled"));
        }
        return chooseObj;
    }

    /**
     * 判断添加的重修课程是否符合规则
     * @param failedClass
     * @param selectCourseArrange
     * @param courseRole
     * @param student
     */
    private void failedClassCompare(List<ElcStudentVo> failedClass,
                                    List<TimeTableMessage> selectCourseArrange,
                                    List<Integer> courseRole,
                                    Student student){
        if (courseRole.isEmpty()) {
            return;
        }
        if (courseRole.size() == 1) {
            Integer i = courseRole.get(0);
            // 不能跨校区选课
            if (i == 1) {
                String campus = student.getCampus();
                for (ElcStudentVo elcStudentVo : failedClass) {
                    if (!campus.equals(elcStudentVo.getCampus())) {
                        throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.campusError", elcStudentVo.getTeachingClassId()));
                    }
                }
            }
            // 不能超出人数上限
            else if (i == 2) {
                for (ElcStudentVo elcStudentVo : failedClass) {
                    if (elcStudentVo.getElcNumber().intValue() >= elcStudentVo.getNumber().intValue()) {
                        throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.elcNumberError", elcStudentVo.getTeachingClassId()));
                    }
                }
            }
            // 不允许时间冲突
            else if (i == 3) {
                getConflict(failedClass, selectCourseArrange);
            }
        } else if (courseRole.size() == 2) {
            if (courseRole.contains(1) && courseRole.contains(2)) {
                String campus = student.getCampus();
                for (ElcStudentVo elcStudentVo : failedClass) {
                    if (campus.equals(elcStudentVo.getCampus())) {
                        if (elcStudentVo.getElcNumber().intValue() >= elcStudentVo.getNumber().intValue()) {
                            throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.elcNumberError", elcStudentVo.getTeachingClassId()));
                        }
                    }else {
                        throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.campusError", elcStudentVo.getTeachingClassId()));
                    }
                }
            } else if (courseRole.contains(1) && courseRole.contains(3)) {
                String campus = student.getCampus();
                for (ElcStudentVo elcStudentVo : failedClass) {
                    if (campus.equals(elcStudentVo.getCampus())) {
                        getConflict(failedClass, selectCourseArrange);
                    }else {
                        throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.campusError", elcStudentVo.getTeachingClassId()));
                    }
                }
            } else if (courseRole.contains(2) && courseRole.contains(3)) {
                for (ElcStudentVo elcStudentVo : failedClass) {
                    if (elcStudentVo.getElcNumber().intValue() >= elcStudentVo.getNumber().intValue()) {
                        throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.elcNumberError", elcStudentVo.getTeachingClassId()));
                    } else {
                        getConflict(failedClass, selectCourseArrange);
                    }
                }
            }
        } else {
            String campus = student.getCampus();
            for (ElcStudentVo elcStudentVo : failedClass) {
                if (campus.equals(elcStudentVo.getCampus())) {
                    if (elcStudentVo.getElcNumber().intValue() >= elcStudentVo.getNumber().intValue()) {
                        throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.elcNumberError", elcStudentVo.getTeachingClassId()));
                    } else {
                        getConflict(failedClass, selectCourseArrange);
                    }
                }else {
                    throw new ParameterValidateException(I18nUtil.getMsg("elcCourseUphold.campusError", elcStudentVo.getTeachingClassId()));
                }
            }
        }
    }

    /**
     * 判断要添加的课程上课时间是否冲突
     * @param addClass 要添加的教学班课程信息
     * @param selectCourseArrange 学生已选课程教学安排
     */
    private void getConflict(List<ElcStudentVo> addClass, List<TimeTableMessage> selectCourseArrange) {
        List<Long> ids = addClass.stream().map(ElcStudentVo::getTeachingClassId).collect(Collectors.toList());
        // 要添加的教学班课程安排
        List<TimeTableMessage> rebuildCourseArrange = courseTakeDao.findCourseArrange(ids);
        // 判断要添加的课程集合上课时间是否冲突
        getParamCourseConflict(rebuildCourseArrange);
        // 判断要添加的课程集合与已选课程集合上课时间是否冲突
        getCourseConflict(rebuildCourseArrange, selectCourseArrange);
    }

    /**
     * 判断要添加的课程集合上课时间是否冲突
     * @param addCourseArrange
     */
    private void getParamCourseConflict(List<TimeTableMessage> addCourseArrange) {
        if (CollectionUtil.isNotEmpty(addCourseArrange)) {
            Map<Long, List<TimeTableMessage>> map = addCourseArrange.stream().collect(Collectors.groupingBy(TimeTableMessage::getTeachingClassId));
            Set<Long> ids = map.keySet();
            if (ids.size() > 1) {
                for (Long id : ids) {
                    List<TimeTableMessage> timeTableMessages = map.get(id);
                    getCourseConflict(timeTableMessages, addCourseArrange);
                }
            }
        }
    }

    /**
     * 判断两个教学安排集合是否冲突
     * @param courseArrange1 教学班教学安排
     * @param courseArrange2 教学班教学安排
     */
    private void getCourseConflict(List<TimeTableMessage> courseArrange1, List<TimeTableMessage> courseArrange2){
        for (TimeTableMessage addTable : courseArrange1) {
            String[] split = addTable.getWeekNum().split(",");
            Set<String> addWeeks = new HashSet<>(Arrays.asList(split));
            int dayOfWeek = addTable.getDayOfWeek().intValue();
            int timeStart = addTable.getTimeStart().intValue();
            int timeEnd = addTable.getTimeEnd().intValue();
            int addSize = addWeeks.size();
            Long teachingClassId = addTable.getTeachingClassId();
            for (TimeTableMessage selectTable : courseArrange2) {
                Long selectTableTeachingClassId = selectTable.getTeachingClassId();
                if (teachingClassId.intValue() == selectTableTeachingClassId.intValue()) {
                    continue;
                }
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
                            throw new ParameterValidateException(I18nUtil.getMsg(I18nUtil.getMsg("elcCourseUphold.timeConflict",teachingClassId + "",selectTableTeachingClassId + "")));
                        }
                    }
                }
            }
        }
    }


    /**
     * 返回选课规则
     *
     * @param calendarId
     * @param manageDptId
     * @return
     */
    public List<Integer> getCourseRole(Long calendarId, String manageDptId) {
        List<Long> ruleIds = retakeCourseSetDao.findRuleIds(calendarId, manageDptId);
        if (ruleIds.isEmpty()) {
            return new ArrayList<>();
        }
        Example example = new Example(ElectionRule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ruleIds);
        List<ElectionRule> electionRules = electionRuleDao.selectByExample(example);
        List<Integer> list = new ArrayList<>(3);
        Set<Integer> set = new HashSet<>(electionRules.size());
        for (ElectionRule electionRule : electionRules) {
            String name = electionRule.getName();
            if ("不能跨校区选课".equals(name)) {
                set.add(1);
            } else if ("不能超出人数上限".equals(name)) {
                set.add(2);
            }else if ("不允许时间冲突".equals(name)) {
                set.add(3);
            }
        }
        list.addAll(set);
        return list;
    }

    /**
     * 保存选课数据
     * @param uid
     * @param name
     * @param chooseObj
     * @param courseDto
     * @param elcStudentVos
     * @param elcCourseTakes
     * @param elcLogs
     */
    private void addToList(String uid, String name, Integer chooseObj, AddCourseDto courseDto, List<ElcStudentVo> elcStudentVos, List<ElcCourseTake> elcCourseTakes, List<ElcLog> elcLogs) {
        String studentId = courseDto.getStudentId();
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
            elcLog.setCreateBy(uid);
            elcLog.setCreateName(name);
            elcLog.setCreatedAt(new Date());
            elcLogs.add(elcLog);
        }
    }

}
