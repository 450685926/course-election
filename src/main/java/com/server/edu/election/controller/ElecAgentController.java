package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.entity.ElcRoundCondition;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.Assert;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.bk.ElecContextBk;
import com.server.edu.election.studentelec.service.StudentElecService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.studentelec.utils.Keys;
import com.server.edu.election.validate.AgentElcGroup;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import com.server.edu.util.async.AsyncProcessUtil;
import com.server.edu.util.async.AsyncResult;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;
import tk.mybatis.mapper.entity.Example;

@SwaggerDefinition(info = @Info(title = "代理选课", version = ""))
@RestSchema(schemaId = "ElecAgentController")
@RequestMapping("agentElc")
public class ElecAgentController
{
    Logger LOG = LoggerFactory.getLogger(ElecAgentController.class);
    
    @Autowired
    private StudentElecService elecService;
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private RedisTemplate redisTemplate;
    
    @Autowired
    private ElecRoundsDao roundsDao;
    
    @ApiOperation(value = "获取生效的轮次")
    @PostMapping("/getRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode)
    {
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        if(CollectionUtil.isEmpty(allRound)) {
        	Date now = new Date();
        	Example example = new Example(ElectionRounds.class);
        	example.createCriteria().andEqualTo("projectId", Constants.ONE).andEqualTo("deleteStatus", Constants.ZERO).andEqualTo("openFlag", 1).andGreaterThan("beginTime", now).andLessThan("endTime", now);
        	allRound = roundsDao.selectByExample(example);
        	for(ElectionRounds electionRounds :allRound) {
        		dataProvider.updateRoundCache(electionRounds.getId());
        	}
        }
        Date date = new Date();
        for (ElectionRounds round : allRound)
        {
            if (StringUtils.equals(projectId, round.getProjectId())
                && StringUtils.equals(electionObj, round.getElectionObj())
                && Objects.equals(mode, round.getMode())
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime()))
            {
                Long id = round.getId();
                // 教务员过滤
                Session session = SessionUtils.getCurrentSession();
                if (StringUtils.equals(session.getCurrentRole(), "1")) {
                    List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
                    ElcRoundCondition roundCondition = dataProvider.getRoundCondition(id);
                    String facultys = roundCondition.getFacultys();
                    if (StringUtils.isNotBlank(facultys)) {
                        boolean flag = true;
                        for (String deptId : deptIds) {
                            if (facultys.contains(deptId)) {
                                flag = false;
                            }
                        }
                        if (flag) {
                            continue;
                        }
                    }
                }
                ElectionRoundsVo vo = new ElectionRoundsVo(round);
                List<ElectionRuleVo> rules =
                    dataProvider.getRules(id);
                vo.setRuleVos(rules);
                data.add(vo);
            }
        }
        return RestResult.successData(data);
    }
    
    /**
     * 进入选课,此时要加载数据
     * 登录选课界面时预加载数据的请求，只需要包括studentId status，前端会定时执行请求直到status变为ready 即加载完成
     */
    @ApiOperation(value = "数据加载")
    @PostMapping("/loading")
    public RestResult<ElecRespose> studentLoading(
        @RequestBody(required = false) ElecRequest elecRequest)
    {
        if (elecRequest.getChooseObj() == null)
        {
            throw new ParameterValidateException("chooseObj not be null");
        }
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        return elecService.loading(elecRequest);
    }
    
    @ApiOperation(value = "获取本科生选课数据")
    @PostMapping("/getDataBk")
    public RestResult<ElecContextBk> getDataBk(
        @RequestBody(required = false) ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = dataProvider.getRound(elecRequest.getRoundId());
        Assert.notNull(round, "elec.roundNotExistTip");
        
        ElecContextBk c = new ElecContextBk(studentId, round.getCalendarId());
        elecService.getDataBk(c,elecRequest.getRoundId());
        return RestResult.successData(c);
    }
    
    /**
     * 选课请求,选课时发送一次，此时应该返回ElecRespose.status=processing
     */
    @ApiOperation(value = "代理学生选课")
    @PostMapping("/elect")
    public RestResult<ElecRespose> elect(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        if (elecRequest.getChooseObj() == null)
        {
            throw new ParameterValidateException("chooseObj not be null");
        }
        Session session = SessionUtils.getCurrentSession();
        elecRequest.setCreateBy(session.getUid());
        elecRequest.setRequestIp(SessionUtils.getRequestIp());
        return elecService.elect(elecRequest);
    }
    
    /**
     * 获取选课结果的请求 未完成时status为processing， 前端会定时执行请求直到status变为ready，此时应返回所有选课结果
     */
    @ApiOperation(value = "查询选课结果")
    @PostMapping("/electRes")
    public RestResult<ElecRespose> getElect(
        @RequestBody ElecRequest elecRequest)
    {
        ElecRespose response = elecService.getElectResult(elecRequest);
        return RestResult.successData(response);
    }
    
    /**
    *@Description: 通过学号和轮次获取学生信息
    *@Param:
    *@return: 
    *@Author: bear
    *@date: 2019/3/5 14:13
    */
    @ApiOperation(value = "查询轮次学生信息")
    @PostMapping("/findStuRound")
    public RestResult<Student> findStuRound(
        @RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        Student stu = elecService.findStuRound(elecRequest.getRoundId(),
            elecRequest.getStudentId());
        return RestResult.successData(stu);
    }
    
    @ApiOperation(value = "清除学生选课缓存数据")
    @PostMapping("/clearCache")
    public RestResult<?> clearCache(@RequestBody ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        ElectionRounds round = dataProvider.getRound(elecRequest.getRoundId());
        Long calendarId = round.getCalendarId();
        
        ElecContextUtil.setElecStatus(calendarId,
            elecRequest.getStudentId(),
            ElecStatus.Init);
        
        return RestResult.success();
    }
    
    @ApiOperation(value = "清除轮次所有学生缓存状态")
    @PostMapping("/asyncInitAllStuCache")
    public RestResult<?> asyncInitAllStuCache(@RequestParam("roundId") Long roundId)
    {
        AsyncResult asyncResult = elecService.asyncInitAllStuCache(roundId);
        return RestResult.successData(asyncResult);
    }
    
    
    @ApiOperation(value = "清除轮次所有学生缓存状态")
    @PostMapping("/initStuCache")
    public RestResult<?> initStuCache(@RequestParam("roundId") Long roundId)
    {
        elecService.initStuCache(roundId);
        return RestResult.success();
    }
    
    @ApiOperation(value = "外部服务清除学生选课缓存数据")
    @PostMapping("/outInitElcStuCache")
    public RestResult<?> outInitElcStuCache(@RequestParam("studentId") @NotBlank String studentId,@RequestParam("calendarId") @NotNull Long calendarId)
    {
        ElecContextUtil.setElecStatus(calendarId,
        		studentId,
            ElecStatus.Init);
        return RestResult.success();
    }
    
    @ApiOperation(value = "初始化学生当前学期下学期选课缓存数据")
    @PostMapping("/outInitNowNextElcStuCache")
    public RestResult<?> outInitNowNextElcStuCache(@RequestParam("studentId") @NotBlank String studentId)
    {
        ElecContextUtil.initCurrentAndNextCalendarStu(studentId);
        return RestResult.success();
    }
    
    
    @ApiOperation(value = "清除选课所有缓存数据")
    @PostMapping("/clearElecCache")
    public RestResult<?> clearElecCache()
    {
    	Set<String> keys = redisTemplate.keys("elec:" + "*");
        redisTemplate.delete(keys);
        return RestResult.success();
    }
    
    @ApiOperation(value = "查询批量初始化选课状态")
    @GetMapping("/findInitRoundStuCache")
    public RestResult<AsyncResult> findInitRoundStuCache(@RequestParam String key){
        AsyncResult asyncResult = AsyncProcessUtil.getResult(key);
        return RestResult.successData(asyncResult);
    }
    
    @ApiOperation(value = "释放选课定时分布式锁")
    @PostMapping("/clearTimeLock")
    public RestResult<?> clearTimeLock()
    {
    	Long caKey = 0L;
        String key = "dataLoad";
    	ElecContextUtil.unlock(caKey, key);
        return RestResult.success();
    }
    
    @ApiOperation(value = "选课异步缓存加载")
    @PostMapping("/asyncLoad")
    public RestResult<?> asyncLoad()
    {
    	AsyncResult asyncResult = elecService.asyncLoad();
        return RestResult.successData(asyncResult);
    }
    
}
