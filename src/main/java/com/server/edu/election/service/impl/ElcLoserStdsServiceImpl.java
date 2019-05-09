package com.server.edu.election.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.common.vo.SchoolCalendarVo;
import com.server.edu.election.dao.ElcLoserStdsDao;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.election.service.ElcLoserStdsService;
import com.server.edu.election.vo.ElcLoserStdsVo;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description: 预警学生
 * @author: bear
 * @create: 2019-05-09 16:44
 */
@Service
@Primary
public class ElcLoserStdsServiceImpl implements ElcLoserStdsService {

    @Autowired
    private ElcLoserStdsDao stdsDao;

    @Override
    public PageResult<ElcLoserStdsVo> findElcLoserStds(PageCondition<ElcLoserStdsVo> condition) {
        PageHelper.startPage(condition.getPageNum_(),condition.getPageSize_());
        Page<ElcLoserStdsVo> page=stdsDao.findElcLoserStds(condition.getCondition());
        if(page!=null){
            List<ElcLoserStdsVo> result = page.getResult();
            if(CollectionUtil.isNotEmpty(result)){
                SchoolCalendarVo calendarVo = BaseresServiceInvoker.getSchoolCalendarById(condition.getCondition().getCalendarId());
                for (ElcLoserStdsVo elcLoserStdsVo : result) {
                    elcLoserStdsVo.setCalendarName(calendarVo.getFullName());
                }

            }
        }
        return new PageResult<>(page);
    }
}
