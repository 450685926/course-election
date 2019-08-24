package com.server.edu.election;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.server.edu.common.PageCondition;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.service.impl.ElectionApplyCoursesServiceImpl;

@ActiveProfiles("dev")
public class ElectionApplyCoursesTest extends ApplicationTest
{
    @Autowired
    ElectionApplyCoursesServiceImpl applyCoursesServiceImpl;
    @Test
    public void test() {
        PageCondition<CourseDto> condition = new PageCondition<>();
        CourseDto condition1 = new CourseDto();
        condition1.setMode(3);
        condition1.setKeyword("32000");
        condition.setCondition(condition1 );
        applyCoursesServiceImpl.courseList(condition );
    }
}
