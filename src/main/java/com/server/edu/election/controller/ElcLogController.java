package com.server.edu.election.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import com.server.edu.common.enums.GroupDataEnum;
import org.apache.commons.lang.StringUtils;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.query.ElcLogQuery;
import com.server.edu.election.service.ElcLogService;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "选课日志", version = ""))
@RestSchema(schemaId = "ElcLogController")
@RequestMapping("elcLog")
public class ElcLogController
{
    @Autowired
    private ElcLogService service;
    
    /**
     * 选课日志列表
     * 
     * @param condition
     * @return
     * @see [类、类#方法、类#成员]
     */
    @ApiOperation(value = "选课日志列表")
    @PostMapping("/page")
    public RestResult<PageResult<ElcLogVo>> list(
        @RequestBody @Valid PageCondition<ElcLogQuery> condition)
        throws Exception
    {
    	Session session = SessionUtils.getCurrentSession();
    	String projId = session.getCurrentManageDptId();
        ElcLogQuery condition2 = condition.getCondition();
        condition2.setDeptId(projId);
        if (null != condition2
            && CollectionUtil.isNotEmpty(condition2.getStudentIds()))
        {
            Set<String> ids = new HashSet<>();
            ids.addAll(condition2.getStudentIds());
            condition2.getStudentIds().clear();
            condition2.getStudentIds().addAll(ids);
        }
        if (StringUtils.equals(session.getCurrentRole(), "1") && !session.isAdmin() && session.isAcdemicDean()) {
            List<String> deptIds = SessionUtils.getCurrentSession().getGroupData().get(GroupDataEnum.department.getValue());
            condition2.setFaculties(deptIds);
        }
        PageResult<ElcLogVo> list = service.listPage(condition);
        
        return RestResult.successData(list);
    }
}
