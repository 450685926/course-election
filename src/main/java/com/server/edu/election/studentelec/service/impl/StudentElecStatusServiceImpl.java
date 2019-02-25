package com.server.edu.election.studentelec.service.impl;

import com.server.edu.election.studentelec.service.StudentElecStatusService;
import com.server.edu.election.studentelec.utils.ElecStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS_LOCK;
import static com.server.edu.election.studentelec.utils.Keys.STD_STATUS;

@Service
public class StudentElecStatusServiceImpl implements StudentElecStatusService {

    private final StringRedisTemplate redisTemplate;
    // 用于储存加锁的key
    private final ThreadLocal<String> threadKey = new ThreadLocal<>();

    @Autowired
    public StudentElecStatusServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(Integer roundId, String studentId) {
        if (threadKey.get() != null) {
            // 在同一线程中重复使用tryLock()
            throw new RuntimeException("Repeated call tryLock() in the same thread");
        }
        String key = UUID.randomUUID().toString();
        String redisKey = String.format(STD_STATUS_LOCK, roundId, studentId);
        if (redisTemplate.opsForValue().setIfAbsent(redisKey,key)) {
            threadKey.set(key);
            return true;
        }
        return false;
    }

    @Override
    public void unlock(Integer roundId, String studentId) {
        String redisKey = String.format(STD_STATUS_LOCK,roundId,studentId);
        String value = redisTemplate.opsForValue().get(redisKey);
        if (value != null && value.equals(threadKey.get())) {
            redisTemplate.delete(redisKey);
            threadKey.remove();
        }else {
            // 在同一线程中未执行tryLock() 就执行unlock()
            throw new RuntimeException("without tryLock() before calling unlock() in the same thread");
        }
    }

    @Override
    public ElecStatus getElecStatus(Integer roundId, String studentId) {
        String value = redisTemplate.opsForValue().get(String.format(STD_STATUS,roundId,studentId));
        return ElecStatus.getStatus(value);
    }

    @Override
    public void setElecStatus(Integer roundId, String studentId, ElecStatus status) {
        redisTemplate.opsForValue().set(String.format(STD_STATUS,roundId,studentId),status.toString());
    }
}
