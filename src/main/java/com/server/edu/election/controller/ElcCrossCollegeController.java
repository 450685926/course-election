package com.server.edu.election.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.servicecomb.provider.rest.common.RestSchema;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.Courses;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.rest.RestResult;

import io.swagger.annotations.Info;
import io.swagger.annotations.SwaggerDefinition;

@SwaggerDefinition(info = @Info(title = "跨专业选课", version = ""))
@RestSchema(schemaId = "ElcCrossCollegeController")
@RequestMapping("elcCrossCollege")
public class ElcCrossCollegeController
{
    /**
     * 获取学生跨专业课程
     * 
     * @see [类、类#方法、类#成员]
     */
    @PostMapping("/getStuCrossCourse")
    public RestResult<PageResult<Courses>> getStuCrossCourse(PageCondition<String> page)
    {
        PageResult<Courses> pageResult = new PageResult<>();
        // String studentId = page.getCondition();
        List<Courses> data = new ArrayList<>();
        Courses e = new Courses();
        e.setId(3L);
        e.setCode("210090");
        e.setName("挡土结构");
        e.setCredits(3D);
        data.add(e);
        // TODO 方法需要实现，给培养计划查询学生申请通过的跨专业课程
        
        pageResult.setList(data);
        return RestResult.successData(pageResult);
    }
}
