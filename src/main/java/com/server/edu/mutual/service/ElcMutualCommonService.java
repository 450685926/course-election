package com.server.edu.mutual.service;

import com.server.edu.session.util.entity.Session;

import java.util.List;

/**
 * 功能描述：本研互选+跨学科选课 通用服务
 *
 * @ClassName ElcMutualManageCollege
 * @Author zhaoerhu
 * @Date 2020/2/6 11:47
 */
public interface ElcMutualCommonService {
    //获取教务员管理的全量学院（当前教务员所属学院+管理的其他学院）
    List<String> getCollegeList(Session session);

    //判断当前登录人是否是教务员
    boolean isDepartAdmin();
}
