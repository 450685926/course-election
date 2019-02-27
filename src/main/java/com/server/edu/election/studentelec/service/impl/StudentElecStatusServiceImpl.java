package com.server.edu.election.studentelec.service.impl;

import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS;
import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS_LOCK;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.server.edu.election.studentelec.service.StudentElecStatusService;
import com.server.edu.election.studentelec.utils.ElecStatus;

@Service
public class StudentElecStatusServiceImpl implements StudentElecStatusService
{
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override
    public boolean tryLock(Integer roundId, String studentId)
    {
        String value = UUID.randomUUID().toString();
        String redisKey = String.format(STD_STATUS_LOCK, roundId, studentId);
        if (redisTemplate.opsForValue().setIfAbsent(redisKey, value))
        {
            return true;
        }
        return false;
    }
    
    @Override
    public void unlock(Integer roundId, String studentId)
    {
        String redisKey = String.format(STD_STATUS_LOCK, roundId, studentId);
        if (redisTemplate.hasKey(redisKey))
        {
            redisTemplate.delete(redisKey);
        }
    }
    
    @Override
    public ElecStatus getElecStatus(Integer roundId, String studentId)
    {
        String value = redisTemplate.opsForValue()
            .get(String.format(STD_STATUS, roundId, studentId));
        
        return ElecStatus.getStatus(value);
    }
    
    @Override
    public void setElecStatus(Integer roundId, String studentId,
        ElecStatus status)
    {
        redisTemplate.opsForValue()
            .set(String.format(STD_STATUS, roundId, studentId),
                status.toString());
    }
}
