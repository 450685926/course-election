package com.server.edu.election.studentelec.service;


/**
 * 轮次数据预加载
 * 轮次加载不需要考虑并发问题
 */
public interface RoundPreloadingService {
    void load();
    void unload();
}
