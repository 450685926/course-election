package com.server.edu.election.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.service.ElcLogService;
import com.server.edu.election.vo.ElcLogVo;

@Service
public class ElcLogServiceImpl implements ElcLogService
{
    @Autowired
    private ElcLogDao elcLogDao;
    
    @Override
    public PageResult<ElcLogVo> listPage(PageCondition<ElcLog> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        
        
        Page<ElcLogVo> p = elcLogDao.listPage(page.getCondition());
        
        return new PageResult<>(p);
    }

    /**选退课日志*/
    @Override
    public void addCourseLog(ElcLog log) {
        elcLogDao.insertSelective(log);
    }

}
