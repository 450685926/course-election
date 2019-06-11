package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcMedWithdrawRuleRefCourDao;
import com.server.edu.election.dto.ElcMedWithdrawRuleRefCourDto;
import com.server.edu.election.entity.ElcMedWithdrawRuleRefCour;
import com.server.edu.election.service.MedRuleRefCourService;
import com.server.edu.election.vo.ElcMedWithdrawRuleRefCourVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class MedRuleRefCourServiceImpl implements MedRuleRefCourService {
	@Autowired
	private ElcMedWithdrawRuleRefCourDao elcMedWithdrawRuleRefCourDao;
	@Override
	public PageInfo<ElcMedWithdrawRuleRefCourVo> list(PageCondition<ElcMedWithdrawRuleRefCourDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMedWithdrawRuleRefCourDto elcMedWithdrawRuleRefCourDto = condition.getCondition();
		if(elcMedWithdrawRuleRefCourDto.getCalendarId()==null||elcMedWithdrawRuleRefCourDto.getMedWithdrawRuleId()==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		List<ElcMedWithdrawRuleRefCourVo> list = new ArrayList<>();
		if(elcMedWithdrawRuleRefCourDto.getIsOpen() == ElcMedWithdrawRuleRefCourDto.ISOPEN) {
			list =elcMedWithdrawRuleRefCourDao.selectMedRuleRefCours(condition.getCondition());
		}else {
			list = elcMedWithdrawRuleRefCourDao.selectUnMedRuleRefCours(condition.getCondition());
		}
		PageInfo<ElcMedWithdrawRuleRefCourVo> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	@Transactional
	public int add(ElcMedWithdrawRuleRefCourDto elcMedWithdrawRuleRefCourDto) {
		List<Long> list = elcMedWithdrawRuleRefCourDto.getTeachingClassIds();
		List<ElcMedWithdrawRuleRefCour> refList = new ArrayList<>();
		list.forEach(temp->{
			ElcMedWithdrawRuleRefCour elcMedWithdrawRuleRefCour = new ElcMedWithdrawRuleRefCour();
			elcMedWithdrawRuleRefCour.setMedWithdrawRuleId(elcMedWithdrawRuleRefCourDto.getMedWithdrawRuleId());
			elcMedWithdrawRuleRefCour.setTeachingClassId(temp);
			refList.add(elcMedWithdrawRuleRefCour);
		});
		int result= elcMedWithdrawRuleRefCourDao.batchInsert(refList);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("elcMedWithdraw.rule")));
		}
		return result;
		
	}
	
	@Override
	@Transactional
	public int addAll(ElcMedWithdrawRuleRefCourDto dto) {
		int result = Constants.ZERO;
		List<ElcMedWithdrawRuleRefCourVo> list= elcMedWithdrawRuleRefCourDao.selectUnMedRuleRefCours(dto);
		if(CollectionUtil.isEmpty(list)) {
			return result;
		}
		List<ElcMedWithdrawRuleRefCour> refList = new ArrayList<>();
		list.forEach(temp->{
			ElcMedWithdrawRuleRefCour elcMedWithdrawRuleRefCour = new ElcMedWithdrawRuleRefCour();
			BeanUtils.copyProperties(temp, elcMedWithdrawRuleRefCour);
			elcMedWithdrawRuleRefCour.setMedWithdrawRuleId(dto.getMedWithdrawRuleId());
			refList.add(elcMedWithdrawRuleRefCour);
		});
		result= elcMedWithdrawRuleRefCourDao.batchInsert(refList);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("elcMedWithdraw.rule")));
		}
		return result;
		
	}
	
	@Override
	@Transactional
	public int removeAll(ElcMedWithdrawRuleRefCourDto dto) {
		int result = Constants.ZERO;
		List<ElcMedWithdrawRuleRefCourVo> list =elcMedWithdrawRuleRefCourDao.selectMedRuleRefCours(dto);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		List<Long> teachingClassIds = list.stream().map(ElcMedWithdrawRuleRefCourVo::getTeachingClassId).collect(Collectors.toList());
		Example example = new Example(ElcMedWithdrawRuleRefCour.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("teachingClassId", teachingClassIds);
		result = elcMedWithdrawRuleRefCourDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("elcMedWithdraw.ruleRefCour")));
		}
		return result;
	}
	
	@Override
	@Transactional
	public int remove(ElcMedWithdrawRuleRefCourDto dto) {
	    List<Long> teachingClassIds = dto.getTeachingClassIds();
		int result = Constants.ZERO;
		Example example = new Example(ElcMedWithdrawRuleRefCour.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("teachingClassId", teachingClassIds);
		List<ElcMedWithdrawRuleRefCour> list  =elcMedWithdrawRuleRefCourDao.selectByExample(example);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		result = elcMedWithdrawRuleRefCourDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("elcMedWithdraw.ruleRefCour")));
		}
		return result;
	}
}
