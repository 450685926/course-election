package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionApplyCoursesDao;
import com.server.edu.election.dao.ElectionApplyDao;
import com.server.edu.election.dto.ElectionApplyDto;
import com.server.edu.election.entity.ElectionApply;
import com.server.edu.election.entity.ElectionApplyCourses;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.service.ElectionApplyService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.vo.ElectionApplyVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

/**
 * 选课申请
 * 
 * 
 * @version  [版本号, 2019年6月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
@Service
public class ElectionApplyServiceImpl implements ElectionApplyService
{
    @Autowired
    private ElectionApplyDao electionApplyDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;
    
    @Autowired
    private ElectionApplyCoursesDao electionApplyCoursesDao;
    
    @Override
    public PageInfo<ElectionApplyVo> applyList(
        PageCondition<ElectionApplyDto> condition)
    {
        ElectionApplyDto dto = condition.getCondition();
        List<ElectionApplyVo> applylist = electionApplyDao.selectApplys(dto);
        Session session = SessionUtils.getCurrentSession();
        if (CollectionUtil.isNotEmpty(applylist))
        {
            Example roundExample = new Example(ElectionRounds.class);
            roundExample.setOrderByClause("BEGIN_TIME_ ASC");
            Example.Criteria roundCriteria = roundExample.createCriteria();
            roundCriteria.andEqualTo("calendarId", dto.getCalendarId());
            roundCriteria.andEqualTo("projectId", dto.getProjectId());
            roundCriteria.andEqualTo("electionObj", Constants.DEPART_ADMIN);
            Date date = new Date();
            roundCriteria.andGreaterThanOrEqualTo("beginTime", date);
            roundCriteria.andLessThanOrEqualTo("endTime", date);
            roundCriteria.andEqualTo("openFlag", Constants.ONE);
            ElectionRounds electionRounds =
                elecRoundsDao.selectOneByExample(roundExample);
            for (ElectionApplyVo electionApplyVo : applylist)
            {
                electionApplyVo.setStatus(Constants.ZERO);
                if (session.isAcdemicDean() || electionRounds != null)
                {
                    electionApplyVo.setStatus(Constants.ONE);
                }
            }
        }
        PageInfo<ElectionApplyVo> pageInfo = new PageInfo<>(applylist);
        return pageInfo;
    }
    
    @Override
    @Transactional
    public int reply(ElectionApply electionApply)
    {
        int result =
            electionApplyDao.updateByPrimaryKeySelective(electionApply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionApply.reply")));
        }
        return result;
    }
    
    @Override
    public int delete(Long calendarId)
    {
        Example example = new Example(ElectionApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("apply", Constants.ONE);
        int result = electionApplyDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("electionApply.application")));
        }
        return result;
    }
    
    @Override
    @Transactional
    public int agree(Long id)
    {
        Session session = SessionUtils.getCurrentSession();
        ElectionApply electionApply = new ElectionApply();
        electionApply.setId(id);
        electionApply.setApply(Constants.ONE);
        electionApply.setApplyBy(session.realUid());
        int result =
            electionApplyDao.updateByPrimaryKeySelective(electionApply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("electionApply.agree")));
        }
        return result;
    }
    
    @Override
    @Transactional
    public int apply(String studentId, Long roundId, String courseCode)
    {
        ElectionRounds elecRounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (elecRounds == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(ElectionApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", studentId);
        Long calendarId = elecRounds.getCalendarId();
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("courseCode", courseCode);
        ElectionApply apply = electionApplyDao.selectOneByExample(example);
        if (apply != null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("election.electionApply")));
        }
        Example cExample = new Example(ElectionApplyCourses.class);
        Example.Criteria cCriteria = cExample.createCriteria();
        cCriteria.andEqualTo("calendarId", calendarId);
        cCriteria.andEqualTo("courseCode", courseCode);
        ElectionApplyCourses electionApplyCourses =
            electionApplyCoursesDao.selectOneByExample(cExample);
        ElectionApply electionApply = new ElectionApply();
        electionApply.setStudentId(studentId);
        electionApply.setCalendarId(calendarId);
        electionApply.setCourseCode(courseCode);
        if (electionApplyCourses != null)
        {
            electionApply.setMode(electionApplyCourses.getMode());
        }
        electionApply.setApply(Constants.ZERO);
        electionApply.setCreatedAt(new Date());
        int result = electionApplyDao.insertSelective(electionApply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("electionApply.applyError"));
        }
        this.updateCache(studentId, calendarId);
        return result;
    }
    
    @Override
    @Transactional
    public void update(String studentId, Long calendarId, String courseCode)
    {
        Example example = new Example(ElectionApply.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", studentId);
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("courseCode", courseCode);
        ElectionApply apply = electionApplyDao.selectOneByExample(example);
        if (apply != null)
        {
            apply.setApply(Constants.ZERO);
            electionApplyDao.updateByPrimaryKeySelective(apply);
        }
        
        this.updateCache(studentId, calendarId);
    }
    
    @Override
    @Transactional
    public int cancelApply(String studentId, Long roundId, String courseCode)
    {
        ElectionRounds elecRounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (elecRounds == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Example example = new Example(ElectionApply.class);
        
        Long calendarId = elecRounds.getCalendarId();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", studentId);
        criteria.andEqualTo("calendarId", calendarId);
        criteria.andEqualTo("courseCode", courseCode);
        ElectionApply apply = electionApplyDao.selectOneByExample(example);
        if (apply == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        int result = electionApplyDao.delete(apply);
        if (result <= 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("election.electionApply")));
        }
        
        this.updateCache(studentId, calendarId);
        return result;
    }
    
    /**更新学生选课缓存数据*/
    private void updateCache(String studentId, Long calendarId)
    {
        Example aExample = new Example(ElectionApply.class);
        Example.Criteria aCriteria = aExample.createCriteria();
        aCriteria.andEqualTo("studentId", studentId);
        aCriteria.andEqualTo("calendarId", calendarId);
        List<ElectionApply> electionApplys =
            electionApplyDao.selectByExample(aExample);
        
        ElecContextUtil
            .setElecApplyCourse(studentId, electionApplys);
    }
}
