package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCouSubsDao;
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.service.ElcCouSubsService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.vo.ElcCouSubsVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcCouSubsServiceImpl implements ElcCouSubsService
{
    @Autowired
    private ElcCouSubsDao elcCouSubsDao;
    
    @Override
    public PageInfo<ElcCouSubsVo> page(
        PageCondition<ElcCouSubsDto> condition)
    {
        ElcCouSubsDto dto = condition.getCondition();
        dto.setProjectId(dto.getProjectId());
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<ElcCouSubsVo> list =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        if (CollectionUtil.isNotEmpty(list))
        {
            for (ElcCouSubsVo vo : list)
            {
                vo.setOrigsCourseInfo(vo.getOrigsCourseCode() + "(" + vo.getOrigsCourseName() + ")");
                vo.setSubCourseInfo(vo.getSubCourseCode() + "(" + vo.getSubCourseName() + ")");
            }
        }
        PageInfo<ElcCouSubsVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    @Transactional
    public int add(ElcCouSubs elcCouSubs)
    {
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", elcCouSubs.getStudentId());
        criteria.andEqualTo("projectId", elcCouSubs.getProjectId());
        criteria.andEqualTo("origsCourseId",
            elcCouSubs.getOrigsCourseId());
        criteria.andEqualTo("subCourseId", elcCouSubs.getSubCourseId());
        ElcCouSubs subs = elcCouSubsDao.selectOneByExample(example);
        if (subs != null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        elcCouSubs.setCreatedAt(new Date());
        int result = elcCouSubsDao.insertSelective(elcCouSubs);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        ElcCouSubsDto dto = new ElcCouSubsDto();
        dto.setStudentId(elcCouSubs.getStudentId());
        List<ElcCouSubsVo> list =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        ElecContextUtil.setReplaceCourses(elcCouSubs.getStudentId(),
            list);
        return result;
    }
    
    @Override
    @Transactional
    public int update(ElcCouSubs elcCouSubs)
    {
        if (elcCouSubs.getId() == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        List<Long> ids = new ArrayList<>();
        ids.add(elcCouSubs.getId());
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotIn("id", ids);
        criteria.andEqualTo("studentId", elcCouSubs.getStudentId());
        criteria.andEqualTo("origsCourseId",
            elcCouSubs.getOrigsCourseId());
        criteria.andEqualTo("subCourseId", elcCouSubs.getSubCourseId());
        ElcCouSubs subs = elcCouSubsDao.selectOneByExample(example);
        elcCouSubs.setUpdatedAt(new Date());
        if (subs != null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        int result =
            elcCouSubsDao.updateByPrimaryKeySelective(elcCouSubs);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        ElcCouSubsDto dto = new ElcCouSubsDto();
        dto.setStudentId(elcCouSubs.getStudentId());
        List<ElcCouSubsVo> list =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        ElecContextUtil.setReplaceCourses(elcCouSubs.getStudentId(),
            list);
        return result;
    }
    
    @Override
    @Transactional
    public int delete(List<Long> ids)
    {
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        List<ElcCouSubs> list =
            elcCouSubsDao.selectByExample(example);
        if (CollectionUtil.isEmpty(list))
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        List<String> studentIds = list.stream()
            .map(ElcCouSubs::getStudentId)
            .collect(Collectors.toList());
        int result = elcCouSubsDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        ElcCouSubsDto dto = new ElcCouSubsDto();
        dto.setStudentIds(studentIds);
        List<ElcCouSubsVo> elcNoGradCouSubsList =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        if (CollectionUtil.isNotEmpty(elcNoGradCouSubsList))
        {
            for (String studentId : studentIds)
            {
                List<ElcCouSubsVo> stuCous = elcNoGradCouSubsList.stream()
                    .filter(c -> studentId.equals(c.getStudentId()))
                    .collect(Collectors.toList());
                ElecContextUtil.setReplaceCourses(studentId, stuCous);
            }
        }
        return result;
    }
    
    @Override
    public List<ElcCouSubs> getElcNoGradCouSubs(List<String> studentIds)
    {
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("studentId", studentIds);
        List<ElcCouSubs> list =
            elcCouSubsDao.selectByExample(example);
        return list;
    }
    
}
