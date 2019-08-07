package com.server.edu.election.controller;

import org.apache.commons.lang3.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

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
        	LOG.info("===========getData============RoundId:" + elecRequest.getRoundId());
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
            if (elecRequest.getChooseObj() == Constants.TOW)
            { // 教务员
                c = yjsService
                    .setData(studentId, c, elecRequest.getRoundId(), null);
            }
            else if (elecRequest.getChooseObj() == Constants.THREE)
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
        
        if (StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
            && !session.isAdmin() && session.isAcdemicDean())
        {// 教务员
            NoSelectCourseStdsDto noSelectCourseStds = condition.getCondition();
            noSelectCourseStds.setRole(Constants.DEPART_ADMIN);
            noSelectCourseStds.setFaculty(session.getFaculty());
        }
        PageResult<NoSelectCourseStdsDto> list =
            yjsService.findAgentElcStudentList(condition);
        return RestResult.successData(list);
    }
    
}
