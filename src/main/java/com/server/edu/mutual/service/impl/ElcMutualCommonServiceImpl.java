package com.server.edu.mutual.service.impl;

import com.server.edu.election.constants.Constants;
import com.server.edu.mutual.service.ElcMutualCommonService;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static Logger LOG = LoggerFactory.getLogger(ElcMutualCommonServiceImpl.class);

    @Override
    public List<String> getCollegeList(Session session) {
        // session中当前学院和管理的学院集合
//    	有管理学院以管理学院为准，没有管理学院以所属学院为准
        List<String> collegeList = new ArrayList<>();
        //添加当前学院
       
        //获取当前教务员管理的学院
        String manageFaculty = session.getManageFaculty();
        if (StringUtils.isNotEmpty(manageFaculty)) {
            List<String> manageFacultyList = Arrays.asList(session.getManageFaculty().split(","));
            //添加当前教务员管理的学院集合
            collegeList.addAll(manageFacultyList);
            return collegeList;
        }
        if (StringUtils.isNotEmpty(session.getFaculty())) {
        	collegeList.add(session.getFaculty());
        	return collegeList;
        }
        return collegeList;
    }

    @Override
    public List<String> getCollegeList() {
        Session session = SessionUtils.getCurrentSession();
        LOG.info("current session :" + session);
        // session中当前学院和管理的学院集合
//    	有管理学院以管理学院为准，没有管理学院以所属学院为准
        List<String> collegeList = new ArrayList<>();
        //添加当前学院

        //获取当前教务员管理的学院
        String manageFaculty = session.getManageFaculty();
        LOG.info("current manageFaculty：" + manageFaculty);
        if (StringUtils.isNotEmpty(manageFaculty)) {
            List<String> manageFacultyList = Arrays.asList(session.getManageFaculty().split(","));
            //添加当前教务员管理的学院集合
            collegeList.addAll(manageFacultyList);
            LOG.info("collegeList1:" + collegeList.toString());
            return collegeList;
        }
        LOG.info("当前学院：" + session.getFaculty());
        if (StringUtils.isNotEmpty(session.getFaculty())) {
            collegeList.add(session.getFaculty());
            LOG.info("collegeList2:" + collegeList.toString());
            return collegeList;
        }
        LOG.info("collegeList3:" + collegeList.toString());
        return collegeList;
    }

    public boolean isDepartAdmin() {
        Session session = SessionUtils.getCurrentSession();
        boolean isDepartAdmin = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
                && !session.isAdmin() && session.isAcdemicDean();
        return isDepartAdmin;
    }
}
