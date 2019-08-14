package com.server.edu.election;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.servicecomb.foundation.common.utils.JsonUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.alibaba.fastjson.JSON;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.CourseTakeTableCreateDao;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.studentelec.context.bk.SelectedCourse;
import com.server.edu.election.studentelec.preload.BKCourseGradeLoad;
import com.server.edu.election.vo.ElcCourseTakeVo;

@ActiveProfiles("dev")
public class ShardingJdbcTest1 extends ApplicationTest
{
    @Autowired
    CourseTakeTableCreateDao courseTakeDao;
    
    @Autowired
    private ElcCourseTakeService courseTakeService;
    
    @Autowired
    private BKCourseGradeLoad bkCourseGradeLoad;
    
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
            "{\"projectId\":\"1\",\"calendarId\":107,\"studentId\":\"\",\"stuName\":\"\",\"stuFaculty\":\"\",\"stuProfession\":\"\",\"courseCode\":\"\",\"teachingClassCode\":\"\",\"keyword\":\"000101\",\"mode\":1}";
        condition.setCondition(JSON.parseObject(json, ElcCourseTakeQuery.class));
        condition.setPageNum_(1);
        condition.setPageSize_(10);
        PageResult<ElcCourseTakeVo> list =
            courseTakeService.listPage(condition);
        
        JsonUtils.writeValueAsString(list);
        
    }
    
    public static void main(String[] args) throws IOException
    {
        ElcCourseTakeVo vo = new ElcCourseTakeVo();
        vo.setId(337234878019928065L);
        System.out.println(JsonUtils.writeValueAsString(vo));
        
        String json = "{\"id\":\"337234878019928065\",\"studentId\":null,\"calendarId\":null,\"courseCode\":null,\"teachingClassId\":null,\"courseTakeType\":null,\"turn\":null,\"mode\":null,\"chooseObj\":null,\"paid\":null,\"billId\":null,\"createdAt\":null,\"studentName\":null,\"courseName\":null,\"credits\":null,\"period\":null,\"campus\":null,\"profession\":null,\"teachingClassCode\":null,\"teachingClassName\":null,\"isPublicCourse\":null,\"electionApplyId\":null,\"apply\":null,\"teachingCode\":null,\"teachingName\":null}";
        vo = JsonUtils.readValue(json.getBytes(), ElcCourseTakeVo.class);
        System.out.println(vo.getId());
    }
    
    @Autowired
    ElcCourseTakeDao takeDao;
    
    @Test
    public void test1() {
        takeDao.editStudyType(3, Arrays.asList(337234878019928065L), 107L);
    }
    
    @Test
    public void test2() {
        Set<SelectedCourse> selectedCourses = new HashSet<>();
        bkCourseGradeLoad.loadSelectedCourses("1856002", selectedCourses , 107L);
        
        System.out.println(selectedCourses.size());
    }
}
