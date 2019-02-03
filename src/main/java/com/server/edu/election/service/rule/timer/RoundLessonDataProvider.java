package com.server.edu.election.service.rule.timer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dto.TeachingClassDto;
import com.server.edu.election.entity.ElectionRounds;

/**
 * 提供profileId 到 教学任务json的提供者<br>
 * 每隔10分钟秒刷新profile的教学任务json<br>
 * 每隔10分钟，清理已经过期的profile
 */
@Component
@Lazy(false)
public class RoundLessonDataProvider
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    static ScheduledExecutorService scheduledThreadPool =
        Executors.newScheduledThreadPool(2);
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    
    @Autowired
    private ElecRoundsDao roundsDao;
    
    static final String KEY = "elect";
    
    public RoundLessonDataProvider()
    {
        scheduledThreadPool.schedule(() -> {
            /*
             * profileId -> lessonId -> json
             */
            HashOperations<String, String, List<String>> ops =
                redisTemplate.opsForHash();
            /** 一小时后即将开始的选课参数 */
            List<ElectionRounds> selectBeStart = roundsDao.selectBeStart();
            Map<String, List<String>> map = new HashMap<>();
            for (ElectionRounds round : selectBeStart)
            {
                List<TeachingClassDto> lessons =
                    roundsDao.selectTeachingClassByRoundId(round.getId());
                List<String> list = new ArrayList<>();
                for (TeachingClassDto lesson : lessons)
                {
                    list.add(JSONObject.toJSONString(lesson));
                }
                map.put(round.getId().toString(), list);
            }
            ops.putAll(KEY, map);
            
        }, 10, TimeUnit.MINUTES);
    }
    /**
     * 得到轮次设置的所有可选课程
     * 
     * @param roundId
     * @return
     * @see [类、类#方法、类#成员]
     */
    public List<TeachingClassDto> getAllLesson(Long roundId)
    {
        HashOperations<String, String, List<String>> ops =
            redisTemplate.opsForHash();
        
        List<String> list = ops.get(KEY, roundId.toString());
        
        List<TeachingClassDto> lessons = new ArrayList<>();
        for (String json : list)
        {
            TeachingClassDto lesson =
                JSONObject.parseObject(json, TeachingClassDto.class);
            lessons.add(lesson);
        }
        return lessons;
        
    }
    
}
