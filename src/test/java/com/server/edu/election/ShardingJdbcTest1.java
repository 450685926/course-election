package com.server.edu.election;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.alibaba.fastjson.JSON;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.CourseTakeTableCreateDao;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.vo.ElcCourseTakeVo;

@ActiveProfiles("dev")
public class ShardingJdbcTest1 extends ApplicationTest
{
    @Autowired
    CourseTakeTableCreateDao courseTakeDao;
    
    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    @Test
    public void test()
    {
        courseTakeDao.createTableIfNotExists();
    }
    
    @Test
    public void page()
        throws Exception
    {
        PageCondition<ElcCourseTakeQuery> condition = new PageCondition<>();
        
        String json =
            "{\"projectId\":1,\"calendarId\":107,\"studentId\":\"\",\"stuName\":\"\",\"stuFaculty\":\"\",\"stuProfession\":\"\",\"courseCode\":\"\",\"teachingClassCode\":\"\",\"mode\":1}";
        condition.setCondition(JSON.parseObject(json, ElcCourseTakeQuery.class));
        condition.setPageNum_(1);
        condition.setPageSize_(10);
        PageResult<ElcCourseTakeVo> list =
            courseTakeService.listPage(condition);
        
    }
}
