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
import com.server.edu.election.constants.RoundMode;
import com.server.edu.election.dao.ElcNoGraduateStdsDao;
import com.server.edu.election.dao.ElecRoundStuDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.entity.ElcNoGraduateStds;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElecRoundStuQuery;
import com.server.edu.election.service.ElecRoundStuService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

@Service
public class ElecRoundStuServiceImpl implements ElecRoundStuService
{
    @Autowired
    private ElecRoundStuDao elecRoundStuDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private ElcNoGraduateStdsDao noGraduateStdsDao;
    
    @Override
    public PageResult<Student4Elc> listPage(
        PageCondition<ElecRoundStuQuery> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        
        ElecRoundStuQuery stu = condition.getCondition();
        Session session = SessionUtils.getCurrentSession();
        stu.setProjectId(session.getCurrentManageDptId());
        Page<Student4Elc> listPage =
            elecRoundStuDao.listPage(stu, stu.getRoundId());
        
        PageResult<Student4Elc> result = new PageResult<>(listPage);
        return result;
    }
    
    @Transactional
    @Override
    public String add(Long roundId, List<String> studentCodes,Integer mode)//判断mode,3,4都要校验学生来源(结业生表)
    {
        ElectionRounds rounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (rounds == null)
        {
            throw new ParameterValidateException("选课轮次不存在");
        }
        Session session = SessionUtils.getCurrentSession();
       
        List<String> listExistStu = elecRoundStuDao.listExistStu(studentCodes, session.getCurrentManageDptId());
        List<String> listAddedStu =
            elecRoundStuDao.listAddedStu(roundId, studentCodes);
        List<String> notExistStu = new ArrayList<>();
        for (String code : studentCodes)
        {
            if (listExistStu.contains(code) && !listAddedStu.contains(code))
            {
                Student studentByCode = studentDao.findStudentByCode(code);
                //studentByCode.getIsOverseas()是否留学生
                if(RoundMode.JieYe.eq(mode) && "0".equals(studentByCode.getIsOverseas())){//结业生
                    ElcNoGraduateStds student = noGraduateStdsDao.findStudentByCode(code);
                    if(student!=null){
                        elecRoundStuDao.add(roundId, code);
                    }else{
                        //添加学生不再结业表中
                        notExistStu.add(code);
                    }

                }else if(RoundMode.LiuXueJieYe.eq(mode) && "1".equals(studentByCode.getIsOverseas())){//留学结业生
                    ElcNoGraduateStds student = noGraduateStdsDao.findStudentByCode(code);
                    if(student!=null){
                        elecRoundStuDao.add(roundId, code);
                    }else{
                        //添加学生不再留学结业表中
                        notExistStu.add(code);
                    }

                }else if (RoundMode.NORMAL.eq(mode) || RoundMode.ShiJian.eq(mode)){
                    elecRoundStuDao.add(roundId, code);
                }else {
                    notExistStu.add(code);
                }
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
    	Session session = SessionUtils.getCurrentSession();
        stu.setProjectId(session.getCurrentManageDptId());
        // 1普通选课 2实践选课
        if(RoundMode.NORMAL.eq(stu.getMode()) || RoundMode.ShiJian.eq(stu.getMode())){ //来源学生

            List<Student4Elc> listStudent =
                    elecRoundStuDao.listNotExistStudent(stu);

            if (CollectionUtil.isNotEmpty(listStudent))
            {
                for (Student4Elc info : listStudent)
                {
                    elecRoundStuDao.add(stu.getRoundId(), info.getStudentId());
                }
            } else {
                throw new ParameterValidateException("没有匹配的学生");
            }
        }else{//选课学生来源与结业表
            List<String> stringList = elecRoundStuDao.notExistStudent(stu);
            if(CollectionUtil.isNotEmpty(stringList)){
                for (String s : stringList) {
                    elecRoundStuDao.add(stu.getRoundId(), s);
                }
            } else {
                throw new ParameterValidateException("没有匹配的学生");
            }
        }
    }
    
    @Transactional
    @Override
    public void delete(Long roundId, List<String> studentCodes)
    {
        if (CollectionUtil.isNotEmpty(studentCodes))
        {
            elecRoundStuDao.delete(roundId, studentCodes);
        }else{
            List<String> stu = elecRoundStuDao.findStuByRoundId(roundId);
            if (CollectionUtil.isEmpty(stu)) {
                throw new ParameterValidateException("无可移除名单");
            }
            elecRoundStuDao.deleteAll(roundId);
        }
    }
    
    @Transactional
    @Override
    public void deleteByCondition(ElecRoundStuQuery stu)
    {
    	Session session = SessionUtils.getCurrentSession();
        stu.setProjectId(session.getCurrentManageDptId());
        Page<Student4Elc> listStudent =
            elecRoundStuDao.listPage(stu, stu.getRoundId());
        
        List<String> studentCodes = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(listStudent))
        {
            for (Student4Elc info : listStudent)
            {
                studentCodes.add(info.getStudentId());
            }
            elecRoundStuDao.delete(stu.getRoundId(), studentCodes);
        }
    }
}
