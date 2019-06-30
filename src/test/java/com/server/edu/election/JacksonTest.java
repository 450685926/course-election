package com.server.edu.election;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.servicecomb.common.rest.codec.RestObjectMapperFactory;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ActiveProfiles;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.server.edu.common.jackson.JacksonUtil;
import com.server.edu.election.entity.ElecContextMock;

@ActiveProfiles("dev")
public class JacksonTest extends ApplicationTest
{
    Logger logger = LoggerFactory.getLogger(getClass());
    
    @Test
    public void test()
        throws JsonProcessingException
    {
        ExecutorService pool = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 100; i++)
        {
            pool.execute(() -> {
                try
                {
                    long begin = System.currentTimeMillis();
                    ElecContextMock c = new ElecContextMock("1659350", 107L);
                    String ss = RestObjectMapperFactory.getRestObjectMapper()
                        .writeValueAsString(c);
                    long end = System.currentTimeMillis();
                    logger.error(String.format("jackson use time: %s, data: {}",
                        TimeUnit.MILLISECONDS.toSeconds(begin - end)));
                }
                catch (JsonProcessingException e)
                {
                    e.printStackTrace();
                }
            });
        }
        try
        {
            TimeUnit.MINUTES.sleep(5);
        }
        catch (InterruptedException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void test1()
    {
        ElecContextMock c = new ElecContextMock("1659350", 107L);
        JSONObject convertObj = JacksonUtil.convertObj(c);
        System.out.println(convertObj.getJSONObject("studentInfo").getString("trainingLevelI18n"));
        
        List<ElecContextMock> list = new ArrayList<>();
        list.add(c);
        List<JSONObject> convertList = JacksonUtil.convertList(list);
        System.out.println(convertList);
    }
}
