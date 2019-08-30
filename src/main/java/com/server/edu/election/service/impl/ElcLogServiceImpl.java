package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.query.ElcCourseTakeQuery;
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
    
    @Autowired
    private ElectionConstantsDao constantsDao;
    
    @Override
    public PageResult<ElcLogVo> listPage(PageCondition<ElcLogQuery> page)
    {
        ElcLogQuery cond = page.getCondition();
        List<String> includeCodes = new ArrayList<>();
        // 1体育课
        if (Objects.equals(cond.getCourseType(), ElcCourseTakeQuery.PE_COURSE_TYPE))
        {
            String findPECourses = constantsDao.findPECourses();
            if (StringUtils.isNotBlank(findPECourses))
            {
                includeCodes.addAll(Arrays.asList(findPECourses.split(",")));
            }
        }
        else if (Objects.equals(cond.getCourseType(), ElcCourseTakeQuery.EN_COURSE_TYPE))
        {   // 2英语课
            String findEnglishCourses = constantsDao.findEnglishCourses();
            if (StringUtils.isNotBlank(findEnglishCourses))
            {
                includeCodes
                    .addAll(Arrays.asList(findEnglishCourses.split(",")));
            }
        }
        cond.setSpeCourseCodes(includeCodes);
        
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<ElcLogVo> p = elcLogDao.listPage(cond);
        
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
