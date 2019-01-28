package com.server.edu.arrangement;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.server.edu.election.ServiceApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
@SpringBootTest(classes = ServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {
    @BeforeClass
    public static void init() {
        System.out.println("\n\n\n开始测试-----------------");
    }

    @AfterClass
    public static void after() {
        System.out.println("测试结束-----------------");
    }
}