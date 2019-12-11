package com.server.edu.election.studentelec.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.election.entity.ElcRoundCondition;
import com.server.edu.election.vo.ElectionRuleVo;
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
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.rules.AbstractLoginRuleExceutorBk;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.service.ElecQueueService;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.cache.AbstractCacheService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.QueueGroups;
import com.server.edu.util.CollectionUtil;

@Service
public class StudentElecServiceImpl extends AbstractCacheService
    implements StudentElecService
{
    Logger LOG = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ElecQueueService<ElecRequest> queueService;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private StudentDao stuDao;
    
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
        ElectionRounds round = roundDao.selectByPrimaryKey(roundId);
        //Assert.notNull(round, "elec.roundCourseExistTip");
        Long calendarId = round.getCalendarId();
        elecRequest.setCalendarId(calendarId);
        ElecContextBk context =
                new ElecContextBk(studentId, calendarId, elecRequest);
    	List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
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
        Map<String, String> failedReasons = respose.getFailedReasons();
        TeachingClassCache teachClass = new TeachingClassCache();
        if(CollectionUtil.isNotEmpty(loginExceutors)) {
            for(int i=1;i<loginExceutors.size()+1;i++) {
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
