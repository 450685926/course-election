package com.server.edu.election.studentelec.service.impl;

import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.utils.ElecContextUtil;

/**
 * 
 * redis死锁删除任务, 每隔10分钟检查redis中是否有死锁
 * 
 */
@Component
@Lazy(false)
public class DeadLockDelTimer
{
    @Scheduled(cron = "0 0/10 * * * *")
    public void load()
    {
        ElecContextUtil.updateLockTime();
    }
}
