package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.query.ElecRoundStuQuery;
import com.server.edu.election.service.ElecRoundStuService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

@Service
public class ElecRoundStuServiceImpl implements ElecRoundStuService
{
    @Autowired
    private ElecRoundStuDao elecRoundStuDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    @Override
    public PageResult<Student4Elc> listPage(
        PageCondition<ElecRoundStuQuery> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        
        ElecRoundStuQuery stu = condition.getCondition();
        Page<Student4Elc> listPage =
            elecRoundStuDao.listPage(stu, stu.getRoundId());
        
        PageResult<Student4Elc> result = new PageResult<>(listPage);
        return result;
    }
    
    @Transactional
    @Override
    public String add(Long roundId, List<String> studentCodes)
    {
        ElectionRounds rounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (rounds == null)
        {
            throw new ParameterValidateException("选课轮次不存在");
        }
        List<String> listExistStu = elecRoundStuDao.listExistStu(studentCodes);
        List<String> listAddedStu =
            elecRoundStuDao.listAddedStu(roundId, studentCodes);
        List<String> notExistStu = new ArrayList<>();
        for (String code : studentCodes)
        {
            if (listExistStu.contains(code) && !listAddedStu.contains(code))
            {
                elecRoundStuDao.add(roundId, code);
            }
            else
            {
                notExistStu.add(code);
            }
        }
        return StringUtils.join(notExistStu, ",");
    }
    
    @Transactional
    @Override
    public void addByCondition(ElecRoundStuQuery stu)
    {
        List<Student4Elc> listStudent = elecRoundStuDao.listStudent(stu);
        if (CollectionUtil.isNotEmpty(listStudent))
        {
            for (Student4Elc info : listStudent)
            {
                elecRoundStuDao.add(stu.getRoundId(), info.getStudentId());
            }
        }
    }
    
    @Transactional
    @Override
    public void delete(Long roundId, List<String> studentCodes)
    {
        for (String code : studentCodes)
        {
            elecRoundStuDao.delete(roundId, code);
        }
    }
    
    @Transactional
    @Override
    public void deleteByCondition(ElecRoundStuQuery stu)
    {
        Page<Student4Elc> listStudent = elecRoundStuDao.listPage(stu, stu.getRoundId());
        if (CollectionUtil.isNotEmpty(listStudent))
        {
            for (Student4Elc info : listStudent)
            {
                elecRoundStuDao.delete(stu.getRoundId(), info.getStudentId());
            }
        }
    }
}
