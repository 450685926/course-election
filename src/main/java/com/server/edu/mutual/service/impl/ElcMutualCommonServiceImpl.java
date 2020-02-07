package com.server.edu.mutual.service.impl;

import com.server.edu.election.constants.Constants;
import com.server.edu.mutual.service.ElcMutualCommonService;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 功能描述：获取教务员管理的全量学院（当前教务员所属学院+管理的其他学院）
 *
 * @ClassName ElcMutualManageCollegeImpl
 * @Author zhaoerhu
 * @Date 2020/2/6 11:49
 */
@Service
public class ElcMutualCommonServiceImpl implements ElcMutualCommonService {
    @Override
    public List<String> getCollegeList(Session session) {
        // session中当前学院和管理的学院集合
        List<String> collegeList = new ArrayList<>();
        //添加当前学院
        collegeList.add(session.getFaculty());
        //获取当前教务员管理的学院
        String manageFaculty = session.getManageFaculty();
        if (StringUtils.isNotEmpty(manageFaculty)) {
            List<String> manageFacultyList = Arrays.asList(session.getManageFaculty().split(","));
            //添加当前教务员管理的学院集合
            collegeList.addAll(manageFacultyList);
        }
        return collegeList;
    }

    public boolean isDepartAdmin() {
        Session session = SessionUtils.getCurrentSession();
        boolean isDepartAdmin = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
                && !session.isAdmin() && session.isAcdemicDean();
        return isDepartAdmin;
    }
}
