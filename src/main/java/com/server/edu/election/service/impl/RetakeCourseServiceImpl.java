package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.common.vo.StudentScoreVo;
import com.server.edu.election.dao.*;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.rpc.ScoreServiceInvoker;
import com.server.edu.election.service.RetakeCourseService;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.FailedCourseVo;
import com.server.edu.election.vo.RebuildCourseVo;
import com.server.edu.election.vo.RetakeCourseCountVo;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RetakeCourseServiceImpl implements RetakeCourseService {
    @Autowired
    private RetakeCourseSetDao retakeCourseSetDao;

    @Autowired
    private RetakeSetRefRuleDao retakeSetRefRuleDao;

    @Autowired
    private RetakeCourseCountDao retakeCourseCountDao;

    @Autowired
    private ElcCourseTakeDao courseTakeDao;

    @Autowired
    private CourseOpenDao courseOpenDao;

    @Override
    @Transactional
    public void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo) {
        Long retakeSetId = retakeCourseSetDao.findRetakeSetId(elcRetakeSetVo.getCalendarId(), elcRetakeSetVo.getProjectId());
        if (retakeSetId == null) {
            elcRetakeSetVo.setCreateAt(new Date());
            retakeCourseSetDao.insertRetakeCourseSet(elcRetakeSetVo);
        } else {
            retakeCourseSetDao.updateRetakeCourseSet(elcRetakeSetVo);
            retakeSetRefRuleDao.deleteByRetakeSetId(retakeSetId);
            elcRetakeSetVo.setRetakeSetId(retakeSetId);
        }
        List<Long> ruleIds = elcRetakeSetVo.getRuleIds();
        if (CollectionUtil.isNotEmpty(ruleIds)) {
            retakeSetRefRuleDao.saveRetakeSetRefRule(elcRetakeSetVo);
        }
    }

    @Override
    public PageResult<RetakeCourseCountVo> findRetakeCourseCountList(PageCondition<RetakeCourseCountVo> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<RetakeCourseCountVo> retakeCourseCountVos = retakeCourseCountDao.findRetakeCourseCountList(condition.getCondition());
        return new PageResult<>(retakeCourseCountVos);
    }

    @Override
    public void updateRetakeCourseCount(RetakeCourseCountVo retakeCourseCountVo) {
        Long id = retakeCourseCountVo.getId();
        if (id == null) {
            retakeCourseCountDao.saveRetakeCourseCount(retakeCourseCountVo);
        } else {
            retakeCourseCountDao.updateRetakeCourseCount(retakeCourseCountVo);
        }
    }

    @Override
    public void deleteRetakeCourseCount(Long retakeCourseCountId) {
        retakeCourseCountDao.deleteRetakeCourseCount(retakeCourseCountId);
    }

    @Override
    public ElcRetakeSetVo getRetakeRul(Long calendarId, String projectId) {
        return retakeCourseSetDao.findRetakeSet(calendarId, projectId);
    }

    @Override
    public List<FailedCourseVo> failedCourseList(String uid, Long calendarId) {
        List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(uid);
        List<FailedCourseVo> failedCourseInfo = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(failedCourseCodes)) {
            failedCourseInfo = courseOpenDao.findFailedCourseInfo(failedCourseCodes, calendarId);
            for (FailedCourseVo failedCourseVo : failedCourseInfo) {
                SchoolCalendarVo schoolCalendar = BaseresServiceInvoker.getSchoolCalendarById(calendarId);
                failedCourseVo.setCalendarName(schoolCalendar.getFullName());
                int count = courseTakeDao.findIsEletionCourse(uid, calendarId, failedCourseVo.getCourseCode());
                if (count == 0) {
                    failedCourseVo.setSelected(false);
                } else {
                    failedCourseVo.setSelected(true);
                }
            }
        }
        return failedCourseInfo;
    }

    @Override
    public List<RebuildCourseVo> findRebuildCourseList(String studentId, Long calendarId, String keyWord, String currentManageDptId) {
        List<String> failedCourseCodes = ScoreServiceInvoker.findStuFailedCourseCodes(studentId);
        if (CollectionUtil.isNotEmpty(failedCourseCodes)) {

        }
        return null;
    }


}
