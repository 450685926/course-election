package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.query.ElcLogQuery;
import com.server.edu.election.service.ElcLogService;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcLogVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;

@Service
public class ElcLogServiceImpl implements ElcLogService
{
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Override
    public PageResult<ElcLogVo> listPage(PageCondition<ElcLogQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        
        Page<ElcLogVo> p = elcLogDao.listPage(page.getCondition());
        
        return new PageResult<>(p);
    }
    
    /**选退课日志*/
    @Override
    public void addCourseLog(ElcLog log)
    {
        elcLogDao.insertSelective(log);
    }
    
    @Override
    public ElcLog createElcLog(Long calendarId, Long teachingClassId,
        String teachingClassCode)
    {
        ElcLog log = new ElcLog();
        ElcCourseTakeVo vo = this.courseTakeDao
            .getTeachingClassInfo(calendarId, teachingClassId, null);
        if (null != vo)
        {
            String code = vo.getTeachingClassCode();
            // 添加选课日志
            log.setCalendarId(calendarId);
            log.setCourseCode(vo.getCourseCode());
            log.setCourseName(vo.getCourseName());
            Session currentSession = SessionUtils.getCurrentSession();
            log.setCreateBy(currentSession.getUid());
            log.setCreatedAt(new Date());
            log.setCreateIp(currentSession.getIp());
            log.setTeachingClassCode(code);
        }
        return log;
    }
    
    @Override
    public void addList(List<ElcLog> list)
    {
        elcLogDao.insertList(list);
    }
    
}
