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
import com.server.edu.election.dao.ElcNoGradCouSubsDao;
import com.server.edu.election.dto.ElcNoGradCouSubsDto;
import com.server.edu.election.entity.ElcNoGradCouSubs;
import com.server.edu.election.service.ElcNoGradCouSubsService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.vo.ElcNoGradCouSubsVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcNoGradCouSubsServiceImpl implements ElcNoGradCouSubsService {
	@Autowired
	private ElcNoGradCouSubsDao elcNoGradCouSubsDao;
	@Override
	public PageInfo<ElcNoGradCouSubsVo> page(PageCondition<ElcNoGradCouSubsDto> condition) {
		ElcNoGradCouSubsDto dto = condition.getCondition();
		dto.setProjectId(dto.getProjectId());
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElcNoGradCouSubsVo> list = elcNoGradCouSubsDao.selectElcNoGradCouSubs(dto);
		if(CollectionUtil.isNotEmpty(list)) {
			for(ElcNoGradCouSubsVo vo:list) {
				vo.setOrigsCourseInfo(vo.getOrigsCourseId()+"("+vo.getOrigsCourseName()+")");
				vo.setSubCourseInfo(vo.getSubCourseId()+"("+vo.getSubCourseName()+")");
			}
		}
		PageInfo<ElcNoGradCouSubsVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@Override
	@Transactional
	public int add(ElcNoGradCouSubs elcNoGradCouSubs) {
		Example example = new Example(ElcNoGradCouSubs.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("studentId", elcNoGradCouSubs.getStudentId());
		criteria.andEqualTo("projectId", elcNoGradCouSubs.getProjectId());
		criteria.andEqualTo("origsCourseId", elcNoGradCouSubs.getOrigsCourseId());
		criteria.andEqualTo("subCourseId", elcNoGradCouSubs.getSubCourseId());
		ElcNoGradCouSubs subs = elcNoGradCouSubsDao.selectOneByExample(example);
		if(subs!=null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		elcNoGradCouSubs.setCreatedAt(new Date());
		int result = elcNoGradCouSubsDao.insertSelective(elcNoGradCouSubs);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		ElcNoGradCouSubsDto dto = new ElcNoGradCouSubsDto();
		dto.setStudentId(elcNoGradCouSubs.getStudentId());
		List<ElcNoGradCouSubsVo> list = elcNoGradCouSubsDao.selectElcNoGradCouSubs(dto);
		ElecContextUtil.setNoGradCouSubs(elcNoGradCouSubs.getStudentId(), list);
		return result;
	}

	@Override
	@Transactional
	public int update(ElcNoGradCouSubs elcNoGradCouSubs) {
		if(elcNoGradCouSubs.getId()==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		List<Long> ids = new ArrayList<>();
		ids.add(elcNoGradCouSubs.getId());
		Example example = new Example(ElcNoGradCouSubs.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andNotIn("id", ids);
		criteria.andEqualTo("studentId", elcNoGradCouSubs.getStudentId());
		criteria.andEqualTo("origsCourseId", elcNoGradCouSubs.getOrigsCourseId());
		criteria.andEqualTo("subCourseId", elcNoGradCouSubs.getSubCourseId());
		ElcNoGradCouSubs subs = elcNoGradCouSubsDao.selectOneByExample(example);
		elcNoGradCouSubs.setUpdatedAt(new Date());
		if(subs!=null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		int result = elcNoGradCouSubsDao.updateByPrimaryKeySelective(elcNoGradCouSubs);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		ElcNoGradCouSubsDto dto = new ElcNoGradCouSubsDto();
		dto.setStudentId(elcNoGradCouSubs.getStudentId());
		List<ElcNoGradCouSubsVo> list = elcNoGradCouSubsDao.selectElcNoGradCouSubs(dto);
		ElecContextUtil.setNoGradCouSubs(elcNoGradCouSubs.getStudentId(), list);
		return result;
	}
	
	@Override
	@Transactional
	public int delete(List<Long> ids) {
		Example example = new Example(ElcNoGradCouSubs.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		List<ElcNoGradCouSubs> list = elcNoGradCouSubsDao.selectByExample(example);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		List<String> studentIds = list.stream().map(ElcNoGradCouSubs ::getStudentId).collect(Collectors.toList());
		int result = elcNoGradCouSubsDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("election.elcNoGradCouSubs")));
		}
		ElcNoGradCouSubsDto dto = new ElcNoGradCouSubsDto();
		dto.setStudentIds(studentIds);
		List<ElcNoGradCouSubsVo> elcNoGradCouSubsList = elcNoGradCouSubsDao.selectElcNoGradCouSubs(dto);
		if(CollectionUtil.isNotEmpty(elcNoGradCouSubsList)) {
			for(String studentId:studentIds) {
				List<ElcNoGradCouSubsVo> stuCous = elcNoGradCouSubsList.stream().filter(c->studentId.equals(c.getStudentId())).collect(Collectors.toList());
				ElecContextUtil.setNoGradCouSubs(studentId, stuCous);
			}
		}
		return result;
	}
	
	@Override
	public List<ElcNoGradCouSubs> getElcNoGradCouSubs(List<String> studentIds){
		Example example = new Example(ElcNoGradCouSubs.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("studentId", studentIds);
		List<ElcNoGradCouSubs> list = elcNoGradCouSubsDao.selectByExample(example);
		return list;
	}

}
