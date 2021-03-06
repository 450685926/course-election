package com.server.edu.election.studentelec.service.impl;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.dto.CultureRuleDto;
import com.server.edu.common.dto.PlanCourseDto;
import com.server.edu.common.dto.PlanCourseTypeDto;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.common.vo.ScoreStudentResultVo;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.election.dao.*;
import com.server.edu.election.dto.AutoRemoveDto;
import com.server.edu.election.dto.GradeStuPlanCourseDto;
import com.server.edu.election.dto.PaidMail;
import com.server.edu.election.dto.StudentChangeDto;
import com.server.edu.election.dto.StudentScoreDto;
import com.server.edu.election.entity.*;
import com.server.edu.election.query.PublicCourseQuery;
import com.server.edu.election.query.PublicCourseTag;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.CourseGroup;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.bk.*;
import com.server.edu.election.studentelec.preload.BKCourseGradeLoad;
import com.server.edu.election.studentelec.preload.BKCoursePlanLoad;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.studentelec.utils.*;
import com.server.edu.election.util.CourseCalendarNameUtil;
import com.server.edu.election.util.EmailSend;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.election.vo.PublicCourseVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractLoginRuleExceutorBk;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.async.AsyncExecuter;
import com.server.edu.util.async.AsyncProcessUtil;
import com.server.edu.util.async.AsyncResult;

import tk.mybatis.mapper.entity.Example;

@Service
public class StudentElecServiceImpl extends AbstractCacheService
    implements StudentElecService
{
    Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private StudentNumDao studentNumDao;

    @Autowired
    private ElcStudentLimitDao elcStudentLimitDao;

    @Autowired
    private ElecQueueService<ElecRequest> queueService;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private StudentDao stuDao;

    @Autowired
    private TeachClassCacheService teachClassCacheService;

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private ElectionParameterDao electionParameterDao;

    @Autowired
    private TeachingClassElectiveRestrictAttrDao restrictAttrDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ElecRoundsDao roundDao;

    @Autowired
    private ElcCourseTakeDao takeDao;

    @Autowired
    private ElectionApplyDao electionApplyDao;
    
    @Autowired
    private StringRedisTemplate strTemplate;
    
    @Autowired
    private ElecRoundStuDao roundStuDao;
    
    @Autowired
    private ElectionConstantsDao electionConstantsDao;

    @Autowired
    private TeachingClassDao tClassDao;

    @Autowired
    private RebuildCourseNoChargeTypeDao noChargeTypeDao;

    @Autowired
    private ElcRebuildChargeTimeSetDao elcRebuildChargeTimeSetDao;
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private BKCoursePlanLoad load;
    
	@Autowired
	private StudentUndergraduateScoreInfoDao infoDao; 

    @Override
    public RestResult<ElecRespose> loading(ElecRequest elecRequest)
    {
        Long roundId = elecRequest.getRoundId();
        Integer chooseObj = elecRequest.getChooseObj();
        String studentId = elecRequest.getStudentId();
        String projectId = elecRequest.getProjectId();
        Long calendarId = null;
        
        // 研究生管理员代理选课
        if (!Constants.PROJ_UNGRADUATE.equals(projectId)
            && Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            calendarId = elecRequest.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        else
        {
//            ElectionRounds round = dataProvider.getRound(roundId);
//            Assert.notNull(round, "elec.roundCourseExistTip");
//            calendarId = round.getCalendarId();
//            elecRequest.setCalendarId(calendarId);
//            ElecContextBk context =
//                    new ElecContextBk(studentId, calendarId, elecRequest);
//        	List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
//        	List<AbstractLoginRuleExceutorBk> loginExceutors = new ArrayList<>();
//        	 // 获取执行规则
//            @SuppressWarnings("rawtypes")
//			Map<String, AbstractRuleExceutor> map =
//                applicationContext.getBeansOfType(AbstractRuleExceutor.class);
//            for (ElectionRuleVo ruleVo : rules)
//            {
//                @SuppressWarnings("rawtypes")
//				AbstractRuleExceutor excetor = map.get(ruleVo.getServiceName());
//                if (null != excetor)
//                {
//                    excetor.setProjectId(ruleVo.getManagerDeptId());
//                    ElectRuleType type = ElectRuleType.valueOf(ruleVo.getType());
//                    excetor.setType(type);
//                    excetor.setDescription(ruleVo.getName());
//                    if (ElectRuleType.GENERAL.equals(type))
//                    {
//                    	loginExceutors.add((AbstractLoginRuleExceutorBk)excetor);
//                    }
//                }
//            }
//            ElecRespose respose = context.getRespose();
//            Map<String, String> failedReasons = respose.getFailedReasons();
//            TeachingClassCache teachClass = new TeachingClassCache();
//            int i = 0;
//            for (AbstractLoginRuleExceutorBk exceutor : loginExceutors)
//            {
//            	
//                if (!exceutor.checkRule(context, teachClass))
//                {
//                    // 校验不通过时跳过后面的校验进行下一个
//                	failedReasons.put(Integer.toString(i), exceutor.getDescription());
//                	i++;
//                    break;
//                }
//            }
//            return RestResult.successData(respose);
            ElectionRounds round = dataProvider.getRound(roundId);
            if(round ==null) {
            	round = roundDao.selectByPrimaryKey(roundId);
            }
            Assert.notNull(round, "elec.roundCourseExistTip");
            calendarId = round.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        // 如果当前是Init状态，进行初始化
        if (ElecStatus.Init.equals(currentStatus))
        {
            ElecContextUtil.saveElecResponse(studentId, new ElecRespose());
            // 加入队列
            currentStatus = ElecStatus.Loading;
            ElecContextUtil.setElecStatus(calendarId, studentId, currentStatus);
            if (!queueService.add(QueueGroups.STUDENT_LOADING, elecRequest))
            {
                currentStatus = ElecStatus.Init;
                ElecContextUtil
                    .setElecStatus(calendarId, studentId, currentStatus);
                return RestResult.fail("请稍后再试");
            }
        }
        
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    @Override
    public RestResult<ElecRespose> elect(ElecRequest elecRequest)
    {
        if (CollectionUtil.isEmpty(elecRequest.getElecClassList())
            && CollectionUtil.isEmpty(elecRequest.getWithdrawClassList()))
        {
            return RestResult.fail("你没选择任何课程");
        }
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        String projectId = elecRequest.getProjectId();
        Integer chooseObj = elecRequest.getChooseObj();
        Long calendarId = null;
        
        // 研究生管理员代理选课
        if (!Constants.PROJ_UNGRADUATE.equals(projectId)
            && Objects.equals(ChooseObj.ADMIN.type(), chooseObj))
        {
            calendarId = elecRequest.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        else
        {
            ElectionRounds round = dataProvider.getRound(roundId);
            if(round ==null) {
            	round = roundDao.selectByPrimaryKey(roundId);
            }
            Assert.notNull(round, "elec.roundCourseExistTip");
            calendarId = round.getCalendarId();
            elecRequest.setCalendarId(calendarId);
        }
        ElecStatus currentStatus =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        if (ElecStatus.Ready.equals(currentStatus)
            && ElecContextUtil.tryLock(calendarId, studentId))
        {
            try
            {
                ElecContextUtil.saveElecResponse(studentId, new ElecRespose());
                // 加入选课队列
                if (queueService.add(QueueGroups.STUDENT_ELEC, elecRequest))
                {
                    ElecContextUtil.setElecStatus(calendarId,
                        studentId,
                        ElecStatus.Processing);
                    currentStatus = ElecStatus.Processing;
                }
                else
                {
                    return RestResult.fail("请稍后再试");
                }
            }
            finally
            {
                ElecContextUtil.unlock(calendarId, studentId);
            }
        }
        return RestResult.successData(new ElecRespose(currentStatus));
    }
    
    public RestResult<ElecRespose> loginCheck(ElecRequest elecRequest){
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        if(roundId==null) {
			throw new ParameterValidateException("轮次ID不能为空"); 
        }
        if(org.apache.commons.lang3.StringUtils.isBlank(studentId)) {
			throw new ParameterValidateException("学生学号不能为空"); 
        }
        ElectionRounds round = dataProvider.getRound(roundId);
        if(round ==null) {
        	round = roundDao.selectByPrimaryKey(roundId);
        }
        Student student = stuDao.findStudentByCode(studentId);
        Assert.notNull(round, "elec.roundCourseExistTip");
        Long calendarId = round.getCalendarId();
        elecRequest.setCalendarId(calendarId);
        ElecRespose elecRespose = new ElecRespose();
        elecRespose.setStatus(ElecStatus.Init);
        ElecContextLogin context =
                new ElecContextLogin(elecRequest,elecRespose);
    	List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
        List<ElectionRuleVo> collect = rules.stream().filter(c -> "LoserNotElcRule".equals(c.getServiceName())).collect(Collectors.toList());
        List<ElectionRuleVo> planRules = rules.stream().filter(r -> "PlanCourseGroupCreditsRule".equals(r.getServiceName())).collect(Collectors.toList());
        List<ElectionRuleVo> onlyRetakeRules = rules.stream().filter(r -> "OnlyRetakeFilter".equals(r.getServiceName())).collect(Collectors.toList());
        List<ElectionRuleVo> noRetakeRules = rules.stream().filter(r -> "NoRetakeRule".equals(r.getServiceName())).collect(Collectors.toList());
        List<ElectionRuleVo> limitRules = rules.stream().filter(r -> "TimeConflictCheckerRule".equals(r.getServiceName())).collect(Collectors.toList());
        List<ElectionRuleVo> peRules = rules.stream().filter(r -> "OnePeCourseCheckerRule".equals(r.getServiceName())).collect(Collectors.toList());
        Integer semester = 0;
        if (CollectionUtil.isNotEmpty(peRules)){
            //查询规则参数是否生效
            Example example = new Example(ElectionParameter.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("ruleId", peRules.get(0).getId());
            List<ElectionParameter> paramList =
                    electionParameterDao.selectByExample(example);
            if (CollectionUtil.isNotEmpty(paramList)){
                ElectionParameter elecparam = paramList.get(0);
                if (Boolean.parseBoolean(elecparam.getValue())) {
                    //获取学生的当前年级
                    Integer grade = student.getGrade();
                    //获取当前学年
                    SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                    Integer year = schoolCalendar.getYear();
                    Integer term = schoolCalendar.getTerm();
                    semester = (year.intValue() - grade.intValue()) * 2 +  term.intValue();

                }
            }
        }

        Integer onlyRetakeRule = 0;
        Integer noRetakeRule = 0;
        //是否展示已修页签
        Integer isShowCompletedTag = 0;
        if (CollectionUtil.isNotEmpty(onlyRetakeRules)){
            onlyRetakeRule = 1;
            isShowCompletedTag = 1;
        }
        if (CollectionUtil.isNotEmpty(noRetakeRules)){
            noRetakeRule = 1;
        }
        if(CollectionUtil.isEmpty(onlyRetakeRules) && CollectionUtil.isEmpty(noRetakeRules)) {
        	isShowCompletedTag = 1;
        }
        Integer isPlan = 1;
        Integer isLimit = 1;
    	if(CollectionUtil.isEmpty(planRules)) {
    		isPlan = 0;
    	}
        if(CollectionUtil.isEmpty(limitRules)) {
            isLimit = 0;
        }
        if (CollectionUtil.isEmpty(collect)){
            Example example = new Example(StudentNum.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("studentNum", studentId);
            criteria.andEqualTo("isPass", Constants.UN_PASS);
            List<StudentNum> stuList = studentNumDao.selectByExample(example);
            Double creditTotal = stuList.stream().mapToDouble(StudentNum::getCredit).sum();
            Example example2 = new Example(ElcStudentLimit.class);
            example2.createCriteria().andEqualTo("calendarId",elecRequest.getCalendarId()).andEqualTo("projectId",Constants.PROJ_UNGRADUATE).andEqualTo("studentId",studentId);
            List<ElcStudentLimit> elcStudentLimits = elcStudentLimitDao.selectByExample(example2);
            Example conExample = new Example(ElectionConstants.class);
            conExample.createCriteria().andEqualTo("managerDeptId","1").andEqualTo("key","MAXWARNINGSCORE").orEqualTo("key", "MINWARNINGSCORE");
            List<ElectionConstants> list = electionConstantsDao.selectByExample(conExample);
            ElcStudentLimit elcStudentLimit = new ElcStudentLimit();
            Double newLimitCredits = 0.0;
            Double minWarningScore = 25.0;
            Double maxWarningScore = 40.0;
            if(CollectionUtil.isNotEmpty(list)) {
            	ElectionConstants maxElectionConstants = list.stream().filter(c->Constants.MAX_WARNINGSCORE.equals(c.getKey())).findFirst().orElse(null);
            	ElectionConstants minElectionConstants = list.stream().filter(c->Constants.MIN_WARNINGSCORE.equals(c.getKey())).findFirst().orElse(null);
            	if(maxElectionConstants !=null && org.apache.commons.lang3.StringUtils.isNotBlank(maxElectionConstants.getValue())) {
            		maxWarningScore = Double.parseDouble(maxElectionConstants.getValue());
            	}
            	if(minElectionConstants !=null && org.apache.commons.lang3.StringUtils.isNotBlank(minElectionConstants.getValue())) {
            		minWarningScore = Double.parseDouble(minElectionConstants.getValue());
            	}
            }
            if (creditTotal.doubleValue() >= minWarningScore && creditTotal.doubleValue() <= maxWarningScore){
            	newLimitCredits =10.0;
            }else if(creditTotal.doubleValue() > maxWarningScore){
            	newLimitCredits =5.0;
            }
            if(newLimitCredits>0.0) {
            	elcStudentLimit.setNewLimitCredits(newLimitCredits);
                elcStudentLimit.setCalendarId(elecRequest.getCalendarId());
                elcStudentLimit.setProjectId(Constants.PROJ_UNGRADUATE);
                elcStudentLimit.setStudentId(studentId);
                elcStudentLimit.setTotalLimitCredits(newLimitCredits);
                elcStudentLimit.setRebuildLimitNumber(6);
                if (CollectionUtil.isEmpty(elcStudentLimits)){
                    elcStudentLimitDao.insertSelective(elcStudentLimit);
                }
            }
        }
        List<AbstractLoginRuleExceutorBk> loginExceutors = new ArrayList<>();
    	 // 获取执行规则
        @SuppressWarnings("rawtypes")
		Map<String, AbstractRuleExceutor> map =
            applicationContext.getBeansOfType(AbstractRuleExceutor.class);
        for (ElectionRuleVo ruleVo : rules)
        {
            @SuppressWarnings("rawtypes")
			AbstractRuleExceutor excetor = map.get(ruleVo.getServiceName());
            if (null != excetor)
            {
                excetor.setProjectId(ruleVo.getManagerDeptId());
                ElectRuleType type = ElectRuleType.valueOf(ruleVo.getType());
                excetor.setType(type);
                excetor.setDescription(ruleVo.getName());
                if (ElectRuleType.GENERAL.equals(type))
                {
                	loginExceutors.add((AbstractLoginRuleExceutorBk)excetor);
                }
            }
        }
        //查询学生是否编级、降转学生
        Integer isDetainedStudent = 0;
        //查询学生是否编级学生
//        Integer isEditLevelStudent = 0;
        //获取选课学期是否为当前学期
        Long nowCalendarId = BaseresServiceInvoker.getCurrentCalendar();
        //判断学生学籍变动范围
        Long oldCalendarId = calendarId;
        if (calendarId > nowCalendarId){
            oldCalendarId = nowCalendarId;
        }
        SchoolCalendarVo calendarVo1 = SchoolCalendarCacheUtil.getCalendar(oldCalendarId);
        SchoolCalendarVo calendarVo2 = SchoolCalendarCacheUtil.getCalendar(oldCalendarId - 1);
        Integer year1 = calendarVo1.getYear();
        Integer term1 = calendarVo1.getTerm();
        Integer year2 = calendarVo2.getYear();
        Integer term2 = calendarVo2.getTerm();
        //查看学生是否有学籍异动信息
        Integer xjydType = 0;
        List<Integer> xjydTypes = Arrays.asList(1,5);
        StudentChangeDto dto = new StudentChangeDto();
        dto.setStudentId(studentId);
        dto.setYear(year1);
        dto.setTerm(term1);
        dto.setXjydTypes(xjydTypes);
        List<StudentChangeDto> studentChangeList1 = tClassDao.getStudentChangeInfo(dto);
        dto.setYear(year2);
        dto.setTerm(term2);
        List<StudentChangeDto> studentChangeList2 = tClassDao.getStudentChangeInfo(dto);
        if (studentChangeList1.size() > 0 || studentChangeList2.size() > 0){
            isDetainedStudent = 1;
        }
        if(CollectionUtil.isNotEmpty(studentChangeList1)) {
        	List<StudentChangeDto> studentChanges1 = studentChangeList1.stream().filter(c->Constants.FIFTH.equals(c.getXjydType())).collect(Collectors.toList());
        	if(Constants.FIRST.equals(noRetakeRule)&&studentChanges1.size()>0) {
        		isShowCompletedTag = 1;
        	}
        }
        if(CollectionUtil.isNotEmpty(studentChangeList2)) {
        	List<StudentChangeDto> studentChanges2 = studentChangeList2.stream().filter(c->Constants.FIFTH.equals(c.getXjydType())).collect(Collectors.toList());
        	if(Constants.FIRST.equals(noRetakeRule)&&studentChanges2.size()>0) {
        		isShowCompletedTag = 1;
        	}
        }
        ElecRespose respose = context.getRespose();
        respose.setTurn(round.getTurn());
        respose.setIsPlan(isPlan);
        respose.setIsLimit(isLimit);
        respose.setSemester(semester);
        respose.setIsDetainedStudent(isDetainedStudent);
        respose.setOnlyRetakeFilter(onlyRetakeRule);
        respose.setNoRetakeRule(noRetakeRule);
        respose.setIsOverseas(student.getIsOverseas());
//        respose.setIsEditLevelStudent(isEditLevelStudent);
        respose.setIsShowCompletedTag(isShowCompletedTag);
        TeachingClassCache teachClass = new TeachingClassCache();
        if(CollectionUtil.isNotEmpty(loginExceutors)) {
            for(int i=0;i<loginExceutors.size();i++) {
            	AbstractLoginRuleExceutorBk exceutor = loginExceutors.get(i);
            	 if (!exceutor.checkRule(context, teachClass))
                 {
                     // 校验不通过时跳过后面的校验进行下一个
//                 	failedReasons.put(Integer.toString(i), exceutor.getDescription());
                    break;
                 }
            }
        }
        return RestResult.successData(respose);
    }
    
    @Override
    public ElecRespose getElectResult(ElecRequest elecRequest)
    {
        Long roundId = elecRequest.getRoundId();
        String studentId = elecRequest.getStudentId();
        Long calendarId = elecRequest.getCalendarId();
        
        // 本科生/研究生教务员
        if (roundId != null) {
            ElectionRounds round = dataProvider.getRound(roundId);
            if(round ==null) {
            	round = roundDao.selectByPrimaryKey(roundId);
            }
            Assert.notNull(round, "elec.roundCourseExistTip");
            calendarId = round.getCalendarId();
        }
        
        ElecRespose response =
            ElecContextUtil.getElecRespose(studentId);
        ElecStatus status =
            ElecContextUtil.getElecStatus(calendarId, studentId);
        if (response == null)
        {
            response = new ElecRespose(status);
        }
        else
        {
            response.setStatus(status);
        }
        return response;
    }
    
    /**根据轮次查询学生信息*/
    @Override
    public Student findStuRound(Long roundId, String studentId)
    {
        Student stu = stuDao.findStudentByCode(studentId);

        if (stu != null) {
            String major = stuDao.getStudentMajor(stu.getGrade(),stu.getProfession());
            ElcRoundCondition roundCondition = dataProvider.getRoundCondition(roundId);
            if (compare(roundCondition.getCampus(), stu.getCampus())
                    && compare(roundCondition.getFacultys(), stu.getFaculty())
                    && compare(roundCondition.getGrades(), stu.getGrade().toString())
                    && compare(roundCondition.getTrainingLevels(), stu.getTrainingLevel())

            ) {
                List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
                if (CollectionUtil.isNotEmpty(rules)) {
                    List<String> collect = rules.stream().map(ElectionRuleVo::getServiceName).collect(Collectors.toList());
                    if (collect.contains("MustInElectableListRule")) {
                        Student student = stuDao.findStuRound(roundId, studentId);
                        if (student == null) {
//                            return null;
                            throw new ParameterValidateException("该学生不在可选名单内，不能进入选课");
                        }
                    }
                }
                stu.setProfession(major);
            	Session session = SessionUtils.getCurrentSession();
                if (StringUtils.equals(session.getCurrentRole(), "1")) {
                    List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
                    // 如果教务员是英语学院或体育学院，则可以给全校学生代选英语体育课
                    if ((stu.getFaculty() != null && deptIds.contains(stu.getFaculty()))
                            || deptIds.contains("000293") || deptIds.contains("000268")) {
                        return stu;
                    } else {
                        return null;
                    }
                }else {
                	return stu;
                }
            }
        }
        return null;
    }

    @Override
    public RestResult getConflict(Long roundId, String studentId, Long teachClassId) {
        List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
        if (CollectionUtil.isEmpty(rules)) {
            return RestResult.successData(400);
        }
        List<String> list = rules.stream().map(ElectionRuleVo::getServiceName).collect(Collectors.toList());
        if (!list.contains("TimeConflictCheckerRule")) {
            return RestResult.successData(400);
        }
        ElectionRounds round = dataProvider.getRound(roundId);
        Long calendarId = round.getCalendarId();
        TeachingClassCache teachingClassCache =
                teachClassCacheService.getTeachClassByTeachClassId(teachClassId);
        // 获取已选课程
        ElecContextBk context = new ElecContextBk(studentId, calendarId);
        Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
        if (CollectionUtil.isNotEmpty(selectedCourses)) {
            List<Long> collect = selectedCourses.stream().map(s -> s.getCourse().getTeachClassId()).collect(Collectors.toList());
            if (collect.contains(teachClassId)) {
                return RestResult.successData(300);
            }
            if (teachingClassCache != null) {
                List<ClassTimeUnit> times = teachingClassCache.getTimes();
                if (CollectionUtil.isNotEmpty(times)) {
                    List<ClassTimeUnit> classTimeUnits = new ArrayList<>(20);
                    Map<Long,TeachingClassCache> map = new HashMap(selectedCourses.size());
                    for (SelectedCourse selectedCours : selectedCourses) {
                        TeachingClassCache course = selectedCours.getCourse();
                        List<ClassTimeUnit> time = course.getTimes();

                        if (CollectionUtil.isNotEmpty(time)) {
                            map.put(course.getTeachClassId(), course);
                            classTimeUnits.addAll(time);
                        }
                    }
                    if (CollectionUtil.isNotEmpty(classTimeUnits)) {
                        // 比较课程冲突
                        for (ClassTimeUnit time : times) {
                            List<Integer> weeks = time.getWeeks();
                            int size1 = weeks.size();
                            int dayOfWeek = time.getDayOfWeek();
                            int timeStart = time.getTimeStart();
                            int timeEnd = time.getTimeEnd();
                            for (ClassTimeUnit classTimeUnit : classTimeUnits) {
                                List<Integer> selWeeks = classTimeUnit.getWeeks();
                                int size2 = selWeeks.size();
                                Set<Integer> all = new HashSet<>();
                                all.addAll(weeks);
                                all.addAll(selWeeks);
                                // 上课周冲突
                                if (size1 + size2 > all.size() ) {
                                    // 判断上课天是否一样
                                    if (dayOfWeek == classTimeUnit.getDayOfWeek()) {
                                        // 判断要添加课程上课开始、结束节次是否与已选课上课节次冲突
                                        int start = classTimeUnit.getTimeStart();
                                        int end = classTimeUnit.getTimeEnd();
                                        if ( (start <= timeStart && timeStart <= end)
                                                || (start <= timeEnd && timeEnd <= end)
                                                || (timeStart <= start && start <= timeEnd)
                                                || (timeStart <= end && end <= timeEnd)) {
                                            TeachingClassCache tc = map.get(classTimeUnit.getTeachClassId());
                                            String code = tc.getCourseCode();
                                            String name = tc.getCourseName();
                                            if (!code.equals(teachingClassCache.getCourseCode())) {
                                                StringBuffer sb = new StringBuffer("[").append(name).append("(").append(code).append(")").append("]");
                                                throw new ParameterValidateException("该课程与已选课程" + sb.toString() + "上课时间冲突");
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return RestResult.success();
    }

    @Override
    public List<TeachingClassCache> getTeachClass4Limit(List<TeachingClassCache> teachClasss, Long studentId) {

        List<TeachingClassCache> list = new ArrayList<>();
        for (TeachingClassCache classs : teachClasss) {
            //限制学生
            List<String> stringList =
                    restrictAttrDao.selectRestrictStudent(classs.getTeachClassId());//限制学生
            if(CollectionUtil.isEmpty(stringList)){
                list.add(classs);
            }else if (CollectionUtil.isNotEmpty(stringList) && stringList.contains(studentId)){
                list.add(classs);
            }
        }
        return list;
    }

    @Override
    public void getDataBk(ElecContextBk c, Long roundId) {
//        ElecRequest request = c.getRequest();
//        List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
//        List<ElectionRuleVo> collect = rules.stream().filter(r -> "PlanCourseGroupCreditsRule".equals(r.getServiceName())).collect(Collectors.toList());
//        if (CollectionUtil.isNotEmpty(collect)){
//        	Set<PlanCourse> planCourses = c.getPlanCourses();
//            if (CollectionUtil.isNotEmpty(planCourses)){
//            	Set<PlanCourse> collect1 = new HashSet<>();
//            	for(PlanCourse planCourse:planCourses) {
//            		ElecCourse course = planCourse.getCourse();
//            		if(Constants.FIRST.equals(course.getChosen())) {
//            			collect1.add(planCourse);
//            		}
//            	}
//                c.setOnePlanCourses(collect1);
//            }
//        }else{
//            c.setOnePlanCourses(c.getPlanCourses());
//        }
        ElectionRounds round = dataProvider.getRound(roundId);
        Long calendarId = round.getCalendarId();
        StudentInfoCache studentInfo = c.getStudentInfo();

//        Set<PlanCourse> planCourses = c.getPlanCourses();
//        if (CollectionUtil.isNotEmpty(planCourses)) {
//            Iterator<PlanCourse> iterator = planCourses.iterator();
//            while (iterator.hasNext()) {
//                PlanCourse planCours = iterator.next();
//                List<TeachingClassCache> teachClasss = teachClassCacheService.getTeachClasss(roundId, planCours.getCourseCode());
//                if (CollectionUtil.isEmpty(teachClasss)) {
//                    iterator.remove();
//                }
//            }
//        }
//        Set<TsCourse> publicCourses = c.getPublicCourses();
//        if (CollectionUtil.isNotEmpty(publicCourses)) {
//            Iterator<TsCourse> iterator = publicCourses.iterator();
//            while (iterator.hasNext()) {
//                TsCourse tsCourse = iterator.next();
//                ElecCourse course = tsCourse.getCourse();
//                if (course != null) {
//                    List<TeachingClassCache> teachClasss = teachClassCacheService.getTeachClasss(roundId, course.getCourseCode());
//                    if (CollectionUtil.isEmpty(teachClasss)) {
//                        iterator.remove();
//                    } else {
//                        Set<String> set = new HashSet(5);
//                        for (TeachingClassCache teachingClassCach : teachClasss) {
//                            String campus = teachingClassCach.getCampus();
//                            if (StringUtils.isNotBlank(campus)) {
//                                campus = dictionaryService.query("X_XQ", campus);
//                                if (StringUtils.isNotBlank(campus)) {
//                                    set.add(campus);
//                                }
//                            }
//                        }
//                        course.setCampus(String.join(",", set));
//                    }
//                }
//            }
//        }
//        Set<HonorCourseBK> honorCourses = c.getHonorCourses();
//        if (CollectionUtil.isNotEmpty(honorCourses)) {
//            Iterator<HonorCourseBK> iterator = honorCourses.iterator();
//            while (iterator.hasNext()) {
//                HonorCourseBK honorCourse = iterator.next();
//                List<TeachingClassCache> teachClasss = teachClassCacheService.getTeachClasss(roundId, honorCourse.getCourse().getCourseCode());
//                if (CollectionUtil.isEmpty(teachClasss)) {
//                    iterator.remove();
//                }
//            }
//        }
//        Set<PlanCourse> onlyCourses = c.getOnlyCourses();
//        if (CollectionUtil.isNotEmpty(onlyCourses)) {
//            Iterator<PlanCourse> iterator = onlyCourses.iterator();
//            while (iterator.hasNext()) {
//                PlanCourse planCours = iterator.next();
//                List<TeachingClassCache> teachClasss = teachClassCacheService.getTeachClasss(roundId, planCours.getCourseCode());
//                if (CollectionUtil.isEmpty(teachClasss)) {
//                    iterator.remove();
//                }
//            }
//        }


        //同步查询已选教学班信息，查看是否存在rides信息丢失，如果丢失，同步更新选课数据
        List<ElcCourseTakeVo> courseTakes = takeDao.findBkSelectedCourses(studentInfo.getStudentId(), calendarId, TableIndexUtil.getIndex(calendarId));
        Set<SelectedCourse> selectedCourses = c.getSelectedCourses();
        if (courseTakes.size() != selectedCourses.size()){


            c.getSelectedCourses().clear();
            for (ElcCourseTakeVo selectedCours : courseTakes) {
                TeachingClassCache teachClass =
                        dataProvider.getTeachClass(round.getId(),
                                selectedCours.getCourseCode(),
                                selectedCours.getTeachingClassId());
                SelectedCourse course = new SelectedCourse(teachClass);
                course.setTurn(selectedCours.getTurn());
                course.setCourseTakeType(selectedCours.getCourseTakeType());
                course.setChooseObj(selectedCours.getChooseObj());
                c.getSelectedCourses().add(course);
            }
        }
        Set<ElectionApply> elecApplyCourses = c.getElecApplyCourses();
        elecApplyCourses.clear();
        Example aExample = new Example(ElectionApply.class);
        Example.Criteria aCriteria = aExample.createCriteria();
        aCriteria.andEqualTo("studentId", studentInfo.getStudentId()).andEqualTo("calendarId", calendarId);
        List<ElectionApply> electionApplys =
                electionApplyDao.selectByExample(aExample);
        elecApplyCourses.addAll(electionApplys);
        //去除培养计划中的重复课程
//        Set<PlanCourse> planCourses = c.getPlanCourses();
//        ArrayList<PlanCourse> newPlanCourse = planCourses.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getCourse().getCourseCode() + ";" + o.getSemester()))), ArrayList::new));
//        planCourses.clear();
//        planCourses.addAll(newPlanCourse);
//        Set<PlanCourse> onlyCourses = c.getOnlyCourses();
//        ArrayList<PlanCourse> newOnlyCourses = onlyCourses.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(o -> o.getCourse().getCourseCode() + ";" + o.getSemester()))), ArrayList::new));
//        onlyCourses.clear();
//        onlyCourses.addAll(newOnlyCourses);
    }

    /**
     * 比较学生是否符合轮次筛选条件
     * @param round 轮次学院，年级等条件
     * @param stu 学生学院，年级等条件
     * @return
     */
    private boolean compare(String round, String stu) {
        if (StringUtils.isNotBlank(round) && (StringUtils.isBlank(stu) || !round.contains(stu))) {
            return false;
        }
        return true;
    }
    
    public AsyncResult asyncInitAllStuCache(Long roundId) {
    	AsyncResult resul = AsyncProcessUtil.submitTask("initRoundStuCache", new AsyncExecuter() {
            @Override
            public void execute() {
            	 ElectionRounds round = roundDao.selectByPrimaryKey(roundId);
                 Long calendarId = round.getCalendarId();
                 //String key = Keys.getRoundStuKey(round.getId());
                 //HashOperations<String, String, String> ops = strTemplate.opsForHash();
                 List<String> stuIds = roundStuDao.findStuByRoundId(roundId);
                 //Set<String> allFields = ops.keys(key);
                 if(CollectionUtil.isNotEmpty(stuIds)) {
                    AsyncResult result = this.getResult();
                 	result.setTotal(stuIds.size());
                 	int num = 0;
             		for(String studentId :stuIds) {
                        ElecContextUtil.setElecStatus(calendarId,
                        		studentId,
                                ElecStatus.Init);
             			num++;
             			result.setDoneCount(num);
             			this.updateResult(result);
             		}
                 }
            }
        });
		return resul;
    }
    
    
    public void initStuCache(Long roundId) {
    	ElectionRounds round = roundDao.selectByPrimaryKey(roundId);
        Long calendarId = round.getCalendarId();
        List<String> stuIds = roundStuDao.findStuByRoundId(roundId);
        if(CollectionUtil.isNotEmpty(stuIds)) {
    		for(String studentId :stuIds) {
               ElecContextUtil.setElecStatus(calendarId,
               		studentId,
                       ElecStatus.Init);
    		}
        }
    }
    
    public AsyncResult asyncLoad() {
    	AsyncResult resul = AsyncProcessUtil.submitTask("asyncLoad", new AsyncExecuter() {
            @Override
            public void execute() {
            	dataProvider.load();
            }
        });
		return resul;
    }

    @Override
    public List<PublicCourseTag> getPublicCourse(PublicCourseQuery query) {
        Long roundId = query.getRoundId();
        ElectionRounds round = dataProvider.getRound(roundId);
        Long calendarId = round.getCalendarId();
        ElecContextBk context = new ElecContextBk(query.getStudentId(), calendarId);
        Set<TsCourse> publicCourses = context.getPublicCourses();
        List<PublicCourseTag> list = new ArrayList<>(5);
        if (CollectionUtil.isEmpty(publicCourses)) {
            return list;
        }
        int day = query.getDay();
        int start = query.getStart();
        int end = query.getEnd();
        Map<String, List<TsCourse>> map = publicCourses.stream().collect(Collectors.groupingBy(TsCourse::getTag));
        Set<String> keySet = map.keySet();
        for (String tag : keySet) {
            PublicCourseTag publicCourseTag = new PublicCourseTag();
            publicCourseTag.setTag(tag);
            List<TsCourse> tsCourses = map.get(tag);
            List<PublicCourseVo> publicCourseVos = new ArrayList<>(20);
            if (CollectionUtil.isNotEmpty(tsCourses)) {
                for (TsCourse tsCours : tsCourses) {
                    ElecCourse course = tsCours.getCourse();
                    if (course != null) {
                        List<TeachingClassCache> teachingClassCaches = teachClassCacheService.getTeachClasss(roundId, course.getCourseCode());
                        for (TeachingClassCache teachingClassCach : teachingClassCaches) {
                            List<ClassTimeUnit> times = teachingClassCach.getTimes();
                            if (CollectionUtil.isNotEmpty(times)) {
                                for (ClassTimeUnit time : times) {
                                    if (time.getDayOfWeek() == day
                                            && time.getTimeStart() >= start
                                            && time.getTimeEnd() <= end) {
                                        PublicCourseVo publicCourseVo = new PublicCourseVo();
                                        publicCourseVo.setTeachClassId(teachingClassCach.getTeachClassId());
                                        publicCourseVo.setTeachClassCode(teachingClassCach.getTeachClassCode());
                                        publicCourseVo.setCampus(teachingClassCach.getCampus());
                                        publicCourseVo.setCredits(teachingClassCach.getCredits());
                                        publicCourseVo.setCourseCode(course.getCourseCode());
                                        publicCourseVo.setCourseName(course.getCourseName());
                                        publicCourseVo.setJp(course.getJp());
                                        publicCourseVo.setCx(course.isCx());
                                        publicCourseVo.setYs(course.isYs());
                                        publicCourseVo.setTimes(times);
                                        publicCourseVos.add(publicCourseVo);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            publicCourseTag.setList(publicCourseVos);
            list.add(publicCourseTag);
        }
        return list;
    }

    @Override
    public void sendEmail() {
        Long currentCalendar =
                BaseresServiceInvoker.getCurrentCalendar();
        SchoolCalendarVo schoolCalendar =
                BaseresServiceInvoker.getSchoolCalendarById(currentCalendar);
        String calendarName = schoolCalendar.getFullName();
        int index = TableIndexUtil.getIndex(currentCalendar);

        PageInfo<PaidMail> page = new PageInfo<>();
        page.setNextPage(1);
        page.setHasNextPage(true);
        List<RebuildCourseNoChargeType> noStuPay = noChargeTypeDao.selectAll();
        long abnormalStatuEndTime = System.currentTimeMillis();
        long abnormalStatuStartTime = abnormalStatuEndTime - (365*24*60*60*1000L);
        List<String> noPayStudent = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(noStuPay)) {
            noPayStudent = takeDao.findNoPayStudent(
                    currentCalendar, noStuPay,
                    abnormalStatuStartTime, abnormalStatuEndTime);
        }

        Example example = new Example(ElcRebuildChargeTimeSet.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId", currentCalendar);
        criteria.andEqualTo("projId", "1");
        ElcRebuildChargeTimeSet elcRebuildChargeTimeSet = elcRebuildChargeTimeSetDao.selectOneByExample(example);
        Date endTime = elcRebuildChargeTimeSet.getEndtime();
        if (new Date().after(endTime)) {
            return;
        }
        Date startTime = elcRebuildChargeTimeSet.getStrattime();
        String str = "yyyy年MM月dd日 HH点mm分";
        SimpleDateFormat sdf=new SimpleDateFormat(str);
        String start = sdf.format(startTime);
        String end = sdf.format(endTime);
        //数据量不大，一次查完再发邮件
//        while (page.isHasNextPage()){
//            PageHelper.startPage(page.getNextPage(),100);
//            List<PaidMail> list = takeDao.findStudent(index, currentCalendar, noPayStudent);
//            page = new PageInfo<>(list);
//            EmailSend emailSend = new EmailSend();
//            emailSend.sendEmail(list, calendarName, start, end);
//        }

        List<PaidMail> list = takeDao.findStudent(index, currentCalendar, noPayStudent);
        EmailSend emailSend = new EmailSend();
        emailSend.sendEmail(list, calendarName, start, end);
    }
    
    public List<GradePlanCourse> getGradeStuPlanCourse(GradeStuPlanCourseDto gdto){
    	List<GradePlanCourse> planCourses = new ArrayList<>();
        Example example = new Example(Course.class);
        Example.Criteria criteria =example.createCriteria();
        criteria.andEqualTo("college", "000293");
        List<Course> courses = courseDao.selectByExample(example);
        List<String> PEList = courses.stream().map(Course::getCode).collect(Collectors.toList());
        List<PlanCourseDto> courseType = CultureSerivceInvoker.findUnGraduateCourse(gdto.getStudentId());
   	    StudentScoreDto dto = new StudentScoreDto();
        dto.setStudentId(gdto.getStudentId());
        List<ScoreStudentResultVo> stuScore = infoDao.getStudentScoreList(dto);
        List<String> selectedCourse = new ArrayList<>();
        if(CollectionUtil.isNotEmpty(stuScore)) {
            selectedCourse = stuScore.stream().map(ScoreStudentResultVo::getCourseCode).collect(Collectors.toList());
        }
        Long roundId = gdto.getRoundId();
        if(CollectionUtil.isNotEmpty(courseType)){
            for (PlanCourseDto planCourse : courseType) {
                List<PlanCourseTypeDto> list = planCourse.getList();
                CultureRuleDto rule = planCourse.getRule();
                Long labelId = planCourse.getLabel();
                String labelName = planCourse.getLabelName();
                if(CollectionUtil.isNotEmpty(list)){
                    for (PlanCourseTypeDto pct : list) {//培养课程
                    	Integer isHaveScore =0;
                        String courseCode = pct.getCourseCode();
                        if(StringUtils.isBlank(courseCode) ||(CollectionUtil.isNotEmpty(selectedCourse) && selectedCourse.contains(courseCode)) ) {
                            continue;
                        }
                        boolean isEnglishFlag = load.checkCultureEnglish(gdto.getStudentId(), courseCode);
                        if(!isEnglishFlag){
                            continue;
                        }
                        GradePlanCourse pl=new GradePlanCourse();
//                        Example example = new Example(Course.class);
//                        example.createCriteria().andEqualTo("code", courseCode);
//                        Course course = courseDao.selectOneByExample(example);
                        List<TeachingClassCache> teachingClassCaches =teachClassCacheService.getTeachClasss(roundId, courseCode);
                        if (CollectionUtil.isNotEmpty(teachingClassCaches)) {
                            ElecCourse course2 = new ElecCourse();
                            course2.setCourseCode(courseCode);
                            course2.setCourseName(pct.getName());
                            course2.setNameEn(pct.getNameEn());
                            course2.setCredits(pct.getCredits());
                            course2.setCompulsory(pct.getCompulsory());
                            course2.setLabelId(labelId);
                            course2.setLabelName(labelName);
                            course2.setChosen(pct.getChosen());
                            course2.setIsQhClass(pct.getIsQhClass());
                            if(CollectionUtil.isNotEmpty(PEList)){
                                if (PEList.contains(courseCode)){
                                    course2.setIsPE(Constants.ONE);
                                    pl.setIsPE(Constants.ONE);
                                }else{
                                    course2.setIsPE(Constants.ZERO);
                                    pl.setIsPE(Constants.ZERO);
                                }
                            }else{
                                course2.setIsPE(Constants.ZERO);
                                pl.setIsPE(Constants.ZERO);
                            }
                            if(StringUtils.isBlank(courseCode) ||(CollectionUtil.isNotEmpty(selectedCourse) && selectedCourse.contains(courseCode)) ) {
                            	isHaveScore = Constants.FIRST;
                            }
                        	pl.setIsHaveScore(isHaveScore);
                            pl.setCourse(course2);
                            pl.setSemester(pct.getSemester());
                            pl.setWeekType(pct.getWeekType());
                            pl.setSubCourseCode(pct.getSubCourseCode());
                            pl.setLabel(labelId);
                            pl.setLabelName(labelName);
                            planCourses.add(pl);
                        }
                    }
                }
            }
        }
        return planCourses;
         
    }

}
