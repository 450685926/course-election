package com.server.edu.election;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import com.server.edu.election.dao.CourseTakeTableCreateDao;

@ActiveProfiles("dev")
public class ShardingJdbcTest1 extends ApplicationTest
{
    @Autowired
    CourseTakeTableCreateDao courseTakeDao;
    
    @Test
    public void test() {
        courseTakeDao.createTableIfNotExists();
    }
}
