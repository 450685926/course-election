package com.server.edu.election.studentelec.service;

import com.server.edu.election.studentelec.utils.ElecStatus;

/**
 * ElecStatus 的四种状态的转换关系<br>
 * Init->Loading->Ready->Processing->Ready<br>
 * 修改状态应该加锁
 * @author liuzheng 2019/2/15 10:09
 */
public interface StudentElecStatusService {
    /**
     * 给status 加锁，并返回key用于解锁
     * @return true 成功 false 失败
     */
    boolean tryLock(Integer roundId, String studentId);

    /**
     * 解锁，只能解除在当前线程加的锁，否则什么也不会发生
     */
    void unlock(Integer roundId, String studentId);

    ElecStatus getElecStatus(Integer roundId, String studentId);

    void  setElecStatus(Integer roundId, String studentId, ElecStatus status);
}
