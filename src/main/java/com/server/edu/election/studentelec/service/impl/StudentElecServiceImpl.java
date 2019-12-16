package com.server.edu.election.studentelec.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.*;
import com.server.edu.election.entity.*;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.context.bk.ElecContextLogin;
import com.server.edu.election.studentelec.rules.AbstractLoginRuleExceutorBk;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.util.CollectionUtil;
import tk.mybatis.mapper.entity.Example;

@Service
public class StudentElecServiceImpl extends AbstractCacheService
    implements StudentElecService
{
    Logger LOG = LoggerFactory.getLogger(getClass());

    @Autowired
    private StudentUndergraduateScoreInfoDao scoreInfoDao;

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
    private TeachingClassElectiveRestrictAttrDao restrictAttrDao;

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private ElecRoundsDao roundDao;
    
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
        Assert.notNull(round, "elec.roundCourseExistTip");
        Long calendarId = round.getCalendarId();
        elecRequest.setCalendarId(calendarId);
        ElecRespose elecRespose = new ElecRespose();
        elecRespose.setStatus(ElecStatus.Init);
        ElecContextLogin context =
                new ElecContextLogin(elecRequest,elecRespose);
    	List<ElectionRuleVo> rules = dataProvider.getRules(roundId);

        List<ElectionRuleVo> collect = rules.stream().filter(c -> "LoserNotElcRule".equals(c.getServiceName())).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(collect)){
            Example example = new Example(StudentUndergraduateScoreInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("studentNum", studentId);
            criteria.andEqualTo("isPass", Constants.UN_PASS);
            List<StudentUndergraduateScoreInfo> stuList = scoreInfoDao.selectByExample(example);
            Double creditTotal = stuList.stream().mapToDouble(StudentUndergraduateScoreInfo::getCredit).sum();
            Example example2 = new Example(ElcStudentLimit.class);
            example2.createCriteria().andEqualTo("calendarId",elecRequest.getCalendarId()).andEqualTo("projectId",Constants.PROJ_UNGRADUATE).andEqualTo("studentId",studentId);
            List<ElcStudentLimit> elcStudentLimits = elcStudentLimitDao.selectByExample(example2);
            ElcStudentLimit elcStudentLimit = new ElcStudentLimit();
            Double newLimitCredits = 0.0;
            if (creditTotal.doubleValue() >= 20.0 && creditTotal.doubleValue() <= 40){
            	newLimitCredits =10.0;
            }else if(creditTotal.doubleValue() > 40){
            	newLimitCredits =5.0;
            }
            if(newLimitCredits>0.0) {
            	elcStudentLimit.setNewLimitCredits(newLimitCredits);
                elcStudentLimit.setCalendarId(elecRequest.getCalendarId());
                elcStudentLimit.setProjectId(Constants.PROJ_UNGRADUATE);
                elcStudentLimit.setStudentId(studentId);
                elcStudentLimit.setTotalLimitCredits(0.0);
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
        ElecRespose respose = context.getRespose();
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
            return RestResult.successData(respose);
        }
        return RestResult.successData(new ElecRespose());
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
            ElcRoundCondition roundCondition = dataProvider.getRoundCondition(roundId);
            if (compare(roundCondition.getCampus(), stu.getCampus())
                    && compare(roundCondition.getFacultys(), stu.getFaculty())
                    && compare(roundCondition.getGrades(), stu.getGrade().toString())
                    && compare(roundCondition.getTrainingLevels(), stu.getTrainingLevel())

            ) {
//                List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
//                if (CollectionUtil.isNotEmpty(rules)) {
//                    List<String> collect = rules.stream().map(ElectionRuleVo::getServiceName).collect(Collectors.toList());
//                    if (collect.contains("MustInElectableListRule")) {
//                        Student student = stuDao.findStuRound(roundId, studentId);
//                        if (student == null) {
//                            return null;
//                        }
//                    }
//                }
            	Session session = SessionUtils.getCurrentSession();
                if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
                    List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
                    if (stu.getFaculty() != null && deptIds.contains(stu.getFaculty())) {
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
    public void getConflict(Long calendarId, String studentId, String courseCode, Long teachClassId) {
        TeachingClassCache teachingClassCache =
                teachClassCacheService.getTeachClassByTeachClassId(teachClassId);
        if (teachingClassCache != null) {
            List<ClassTimeUnit> times = teachingClassCache.getTimes();
            if (CollectionUtil.isNotEmpty(times)) {
                // 获取已选课程
                ElecContextBk context = new ElecContextBk(studentId, calendarId);
                Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
                if (CollectionUtil.isNotEmpty(selectedCourses)) {
                    List<ClassTimeUnit> classTimeUnits = new ArrayList<>(20);
                    Map map = new HashMap(selectedCourses.size());
                    for (SelectedCourse selectedCours : selectedCourses) {
                        TeachingClassCache course = selectedCours.getCourse();
                        List<ClassTimeUnit> time = course.getTimes();
                        String code = course.getCourseCode();
                        String name = course.getCourseName();
                        StringBuffer sb = new StringBuffer("[").append(name).append("(").append(code).append(")").append("]");
                        if (CollectionUtil.isNotEmpty(time)) {
                            map.put(course.getTeachClassId(), sb.toString());
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
                                            throw new ParameterValidateException("该课程与已选课程" + map.get(classTimeUnit.getTeachClassId()) + "上课时间冲突");
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
}
