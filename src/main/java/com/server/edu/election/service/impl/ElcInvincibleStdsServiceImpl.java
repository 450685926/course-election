package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcInvincibleStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.ElcInvincibleStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcInvincibleStdsService;
import com.server.edu.exception.ParameterValidateException;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcInvincibleStdsServiceImpl implements ElcInvincibleStdsService {
	@Autowired
	private StudentDao studentDao;
	@Autowired
	private ElcInvincibleStdsDao elcInvincibleStdsDao;
	@Override
	public PageInfo<Student> list(PageCondition<Student> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<Student> list = studentDao.selectElcInvincibleStds(condition.getCondition());
		PageInfo<Student> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<Student> getStudents(PageCondition<Student> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<Student> list = studentDao.selectUnElcStudents(condition.getCondition());
		PageInfo<Student> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public int delete(List<String> ids) {
		Example example =new Example(ElcInvincibleStds.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("studentId", ids);
		int result=elcInvincibleStdsDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("elcAffinity.InvincibleStds")));
		}
		return result;
	}
	
	@Override
	public int add(List<String> ids) {
		List<ElcInvincibleStds> invincibleStds =new ArrayList<>();
		ids.forEach(c->{
			ElcInvincibleStds elcInvincibleStds = new ElcInvincibleStds();
			elcInvincibleStds.setStudentId(c);
			invincibleStds.add(elcInvincibleStds);
		});
		int result = elcInvincibleStdsDao.batchInsert(invincibleStds);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("elcAffinity.InvincibleStds")));
		}
		return result; 
		
	}	

}
