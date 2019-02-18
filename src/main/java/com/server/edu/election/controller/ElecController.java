package com.server.edu.election.controller;

import com.server.edu.election.studentelec.context.ElecRequest;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.service.ElecService;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.rest.RestResult;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "学生选课", version = ""))
@RestSchema(schemaId = "ElecController")
@RequestMapping("student")
public class ElecController
{
    private final ElecService elecService;

    @Autowired
    public ElecController(ElecService elecService) {
        this.elecService = elecService;
    }

    /**
     * 进入选课,此时要加载数据
     * 登录选课界面时预加载数据的请求，只需要包括studentId status，前端会定时执行请求直到status变为ready 即加载完成
     */
    @PostMapping("{roundId}/loading")
    public RestResult<ElecRespose> studentLoading(@PathVariable("roundId") Integer roundId) {
        Session session = SessionUtils.getCurrentSession();
        final int STUDENT = 2;
        if (session.realType()!= STUDENT) {
            return RestResult.fail("not a student");
        }
        return elecService.loading(roundId,session.realUid());
    }

    /**
     * 选课请求,选课时发送一次，此时应该返回ElecRespose.status=processing
     */
    @PostMapping("{roundId}/elect")
    public RestResult<ElecRespose> elect(@PathVariable("roundId") Integer roundId) {
        Session session = SessionUtils.getCurrentSession();
        final int STUDENT = 2;
        if (session.realType()!= STUDENT) {
            return RestResult.fail("not a student");
        }
        return elecService.loading(roundId,session.realUid());
    }

    /**
     * 获取选课结果的请求 未完成时status为processing， 前端会定时执行请求直到status变为ready，此时应返回所有选课结果
     */
    @GetMapping("{roundId}/elect")
    public RestResult<ElecRespose> getElect(@PathVariable("roundId") Integer roundId) {
        Session session = SessionUtils.getCurrentSession();
        final int STUDENT = 2;
        if (session.realType()!= STUDENT) {
            return RestResult.fail("not a student");
        }
        return elecService.loading(roundId,session.realUid());
    }

}
