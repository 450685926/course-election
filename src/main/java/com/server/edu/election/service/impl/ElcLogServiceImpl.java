package com.server.edu.election.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.service.ElcLogService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class ElcLogServiceImpl implements ElcLogService
{
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Override
    public PageResult<ElcLog> listPage(PageCondition<ElcLog> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        
        Example example = new Example(ElcLog.class);
        
        ElcLog log = page.getCondition();
        Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId", log.getCalendarId());
        
        if (StringUtils.isNotBlank(log.getStudentId()))
        {
            criteria.andEqualTo("studentId", log.getStudentId());
        }
        if (log.getType() != null)
        {
            criteria.andEqualTo("type", log.getType());
        }
        if (log.getMode() != null)
        {
            criteria.andEqualTo("mode", log.getMode());
        }
        
        Page<ElcLog> p = (Page<ElcLog>)elcLogDao.selectByExample(example);
        
        return new PageResult<>(p);
    }
    
}
