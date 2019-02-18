package com.server.edu.election.studentelec.service;


/**
 * 轮次数据预加载
 * 轮次不需要考虑并发问题，可以不用严格的加锁
 */
public interface RoundPreloadingService {
    void load();
    void unload();
}
