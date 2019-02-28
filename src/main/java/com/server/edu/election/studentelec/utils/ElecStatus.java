package com.server.edu.election.studentelec.utils;

/**
 * 学生选课状态
 * <ul>
 * <li>Init 当选课轮次开始后 所有名单中的学生应该处于状态0,当学生离开选课界面，清除个人缓存后也应该为此
 * <li>Loading 当学生进入选课，加载数据时
 * <li>Ready 当数据加载完毕，或者选课结果已返回
 * <li>Processing 选课中
 * </ul>
 */
public enum ElecStatus
{
    Refuse, Init, Loading, Ready, Processing;
}
