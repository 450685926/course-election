package com.server.edu.exam.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.dictionary.utils.ClassroomCacheUtil;
import com.server.edu.exam.dao.GraduateExamLogDao;
import com.server.edu.exam.entity.GraduateExamLog;
import com.server.edu.exam.query.GraduateExamLogQuery;
import com.server.edu.exam.service.GraduateExamLogService;
import com.server.edu.exam.util.GraduateExamTransTime;
import com.server.edu.exam.vo.GraduateExamLogVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @description:
 * @author: bear
 * @create: 2019-08-28 09:39
 */
@Service
@Primary
public class GraduateExamLogServiceImpl implements GraduateExamLogService{

    @Autowired
    private GraduateExamLogDao logDao;

    @Override
    public PageResult<GraduateExamLogVo> listGraduateExamLog(PageCondition<GraduateExamLogQuery> logVo) {
        GraduateExamLogQuery examLogVo = logVo.getCondition();
        if(examLogVo.getCalendarId() == null){
            throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
        }
        String dptId = SessionUtils.getCurrentSession().getCurrentManageDptId();
        examLogVo.setProjId(dptId);
        PageHelper.startPage(logVo.getPageNum_(),logVo.getPageSize_());
        Page<GraduateExamLogVo> page = logDao.listGraduateExamLog(examLogVo);
        return new PageResult<>(page);
    }

    @Override
    @Transactional
    public void addGraduateExamLog(GraduateExamLog examLog) {
        logDao.insertSelective(examLog);
    }
}
