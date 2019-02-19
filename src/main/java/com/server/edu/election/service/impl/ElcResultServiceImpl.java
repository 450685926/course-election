package com.server.edu.election.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcResultService;
import com.server.edu.election.vo.TeachingClassVo;

@Service
public class ElcResultServiceImpl implements ElcResultService
{
    @Autowired
    private TeachingClassDao classDao;
    
    @Override
    public PageResult<TeachingClassVo> listPage(
        PageCondition<ElcResultQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<TeachingClassVo> listPage = classDao.listPage(page.getCondition());
        
        return new PageResult<>(listPage);
    }
    
}
