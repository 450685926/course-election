package com.server.edu.election.controller;

import java.util.*;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.JSONObject;
import com.server.edu.election.util.CommonConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.common.validator.ValidatorUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.service.ElecYjsService;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.validate.AgentElcGroup;
import com.server.edu.election.vo.ElectionRoundsVo;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "研究生代理选课", version = ""))
@RestSchema(schemaId = "ElecAgentYjsController")
@RequestMapping("agentElc")
public class ElecAgentYjsController
{
    Logger LOG = LoggerFactory.getLogger(ElecAgentYjsController.class);
    
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private ElecYjsService yjsService;
    
    private static final String LIST_RULE = "yjsMustInElectableListRule";
    
    @ApiOperation(value = "研究生代理选课获取生效的轮次")
    @PostMapping("/getGraduateRounds")
    public RestResult<List<ElectionRoundsVo>> getRounds(
        @RequestParam("electionObj") @NotBlank String electionObj,
        @RequestParam("projectId") @NotBlank String projectId,
        @RequestParam(name = "mode") @NotNull Integer mode,
        @RequestParam(name = "studentId", required = false) String studentId)
    {
    	// 研究生(研究生只有教务员代理选课需要查询轮次信息)
        List<ElectionRoundsVo> data = new ArrayList<>();
        List<ElectionRounds> allRound = dataProvider.getAllRound();
        Date date = new Date();
        for (ElectionRounds round : allRound)
        {
        	Long roundId = round.getId();
            if (StringUtils.equals(projectId, round.getProjectId())
                && StringUtils.equals(electionObj, round.getElectionObj())
                && Objects.equals(mode, round.getMode())
                && date.after(round.getBeginTime())
                && date.before(round.getEndTime())
                && dataProvider.containsStuCondition(round.getId(),studentId,projectId))
            {
            	List<ElectionRuleVo> rules = dataProvider.getRules(roundId);
            	boolean flag = true;
            	if (CollectionUtil.isNotEmpty(rules)) {
            		List<ElectionRuleVo> nameList = rules.stream().filter(vo->StringUtils.equals(vo.getServiceName(), LIST_RULE)).collect(Collectors.toList());
            		if (CollectionUtil.isNotEmpty(nameList)) {
            			if (!dataProvider.containsStu(roundId, studentId) ) {
            				flag = false;
    					}
					}
				}
            	if (flag) {
            		ElectionRoundsVo vo = new ElectionRoundsVo(round);
                    vo.setRuleVos(rules);
                    data.add(vo);
				}
            }
        }
        return RestResult.successData(data);
    }
    
    @ApiOperation(value = "获取研究生选课数据")
    @PostMapping("/getData")
    public RestResult<ElecContext> getData(
        @RequestBody(required = false) ElecRequest elecRequest)
    {
        ValidatorUtil.validateAndThrow(elecRequest, AgentElcGroup.class);
        
        Session session = SessionUtils.getCurrentSession();
        String studentId = elecRequest.getStudentId();
        
        ElectionRounds round = new ElectionRounds();
        ElecContext c = new ElecContext();
        if (elecRequest.getRoundId() != null)
        { // 教务员
            round = dataProvider.getRound(elecRequest.getRoundId());
            if (round == null)
            {
                return RestResult.error("elec.roundNotExistTip");
            }
            c = new ElecContext(studentId, round.getCalendarId());
        }
        else
        { // 管理员
            c = new ElecContext(studentId, elecRequest.getCalendarId());
        }
        
        if (!Constants.PROJ_UNGRADUATE.equals(session.getCurrentManageDptId()))
        {
            if (elecRequest.getChooseObj().intValue() == Constants.TOW)
            { // 教务员
                c = yjsService
                    .setData(studentId, c, elecRequest.getRoundId(), null);
            }
            else if (elecRequest.getChooseObj().intValue() == Constants.THREE)
            { // 管理员
                c = yjsService
                    .setData(studentId, c, null, elecRequest.getCalendarId());
                //c = elecService.setData(c,null,calendarId);
            }
        }
        return RestResult.successData(c);
    }
    
    @ApiOperation(value = "获取被代理选课的学生列表")
    @PostMapping("/findAgentElcStudentList")
    public RestResult<PageResult<NoSelectCourseStdsDto>> findAgentElcStudentList(
        @RequestBody PageCondition<NoSelectCourseStdsDto> condition)
    {
        Session session = SessionUtils.getCurrentSession();
        
        if (!StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)))
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("agentElc.role.err"));
        }
        // 教务员
        NoSelectCourseStdsDto noSelectCourseStds = condition.getCondition();
        if (StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
            && !session.isAdmin() && session.isAcdemicDean())
        {
            noSelectCourseStds.setFaculty(session.getFaculty());
            noSelectCourseStds.setRole(Constants.DEPART_ADMIN);
        }
        // 部门
        noSelectCourseStds.setProjId(CommonConstant.getProjId(noSelectCourseStds.getProjId()));
        // 查询列表集合
        LOG.info("-----LSG---the findAgentElcStudentList parames is:{}", JSONObject.toJSONString(condition.getCondition()));
        PageResult<NoSelectCourseStdsDto> list =
            yjsService.findAgentElcStudentList(condition);
        return RestResult.successData(list);
    }

}
