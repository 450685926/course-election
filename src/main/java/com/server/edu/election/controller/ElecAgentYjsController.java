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
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dto.NoSelectCourseStdsDto;
import com.server.edu.election.studentelec.service.ElecYjsService;
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
    private ElecYjsService yjsService;
    
    @ApiOperation(value = "获取被代理选课的学生列表")
    @PostMapping("/findAgentElcStudentList")
    public RestResult<PageResult<NoSelectCourseStdsDto>> findAgentElcStudentList(
        @RequestBody PageCondition<NoSelectCourseStdsDto> condition)
    {
        Session session = SessionUtils.getCurrentSession();
        
        if (!StringUtils.equals(session.getCurrentRole(), "1"))
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("agentElc.role.err"));
        }
        
        if (StringUtils.equals(session.getCurrentRole(), "1")
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
