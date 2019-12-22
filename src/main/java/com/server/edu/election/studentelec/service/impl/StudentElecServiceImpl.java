package com.server.edu.election.studentelec.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.dictionary.service.DictionaryService;
import com.server.edu.dictionary.utils.SchoolCalendarCacheUtil;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.election.dao.*;
import com.server.edu.election.entity.*;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.election.studentelec.context.ClassTimeUnit;
import com.server.edu.election.studentelec.context.ElecCourse;
import com.server.edu.election.studentelec.context.bk.*;
import com.server.edu.election.studentelec.preload.BKCourseGradeLoad;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import com.server.edu.election.util.TableIndexUtil;
import com.server.edu.election.vo.ElcCourseTakeVo;
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
    private TeachingClassElectiveRestrictAttrDao restrictAttrDao;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ElecRoundsDao roundDao;

    @Autowired
    private ElcCourseTakeDao takeDao;

    @Autowired
    private ElectionApplyDao electionApplyDao;

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
        List<ElectionRuleVo> planRules = rules.stream().filter(r -> "PlanCourseGroupCreditsRule".equals(r.getServiceName())).collect(Collectors.toList());
        List<ElectionRuleVo> limitRules = rules.stream().filter(r -> "TimeConflictCheckerRule".equals(r.getServiceName())).collect(Collectors.toList());
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
        respose.setIsPlan(isPlan);
        respose.setIsLimit(isLimit);
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
        respose = new ElecRespose();
        respose.setIsPlan(isPlan);
        respose.setIsLimit(isLimit);
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
                course.setTurn(round.getTurn());
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
