package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.RetakeCourseCountDao;
import com.server.edu.election.dao.RetakeCourseSetDao;
import com.server.edu.election.dao.RetakeSetRefRuleDao;
import com.server.edu.election.service.RetakeCourseService;
import com.server.edu.election.vo.ElcRetakeSetVo;
import com.server.edu.election.vo.RetakeCourseCountVo;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RetakeCourseServiceImpl implements RetakeCourseService {
    @Autowired
    private RetakeCourseSetDao retakeCourseSetDao;

    @Autowired
    private RetakeSetRefRuleDao retakeSetRefRuleDao;

    @Autowired
    private RetakeCourseCountDao retakeCourseCountDao;

    @Override
    @Transactional
    public void setRetakeRules(ElcRetakeSetVo elcRetakeSetVo) {
        retakeCourseSetDao.saveRetakeCourseSet(elcRetakeSetVo);
        List<Long> ruleIds = elcRetakeSetVo.getRuleIds();
        if (CollectionUtil.isNotEmpty(ruleIds)) {
            Long retakeSetId = retakeCourseSetDao.findRetakeSetId(elcRetakeSetVo.getCalendarId());
            retakeSetRefRuleDao.saveRetakeSetRefRule(retakeSetId, ruleIds);
        }
    }

    @Override
    @Transactional
    public void deleteRetakeRules(Long calendarId) {
        Long retakeSetId = retakeCourseSetDao.findRetakeSetId(calendarId);
        retakeCourseSetDao.deleteByCalendarId(calendarId);
        retakeSetRefRuleDao.deleteByRetakeSetId(retakeSetId);
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


}
