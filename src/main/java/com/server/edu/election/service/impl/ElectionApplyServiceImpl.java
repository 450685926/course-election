package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
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
@Service
public class ElectionApplyServiceImpl implements ElectionApplyService {
	@Autowired
	private ElectionApplyDao electionApplyDao;
	@Autowired
	private ElecRoundsDao elecRoundsDao;
	@Autowired
	private ElcCourseTakeDao elcCourseTakeDao;
	@Autowired
	private ElectionApplyCoursesDao electionApplyCoursesDao;
	@Autowired
	private ElecContextUtil elecContextUtil;
	@Override
	public PageInfo<ElectionApplyVo> applyList(PageCondition<ElectionApplyDto> condition) {
		ElectionApplyDto dto = condition.getCondition();
		List<ElectionApplyVo> applylist = electionApplyDao.selectApplys(dto);
		Session session = SessionUtils.getCurrentSession();
		if(CollectionUtil.isNotEmpty(applylist)) {
			Example roundExample = new Example(ElectionRounds.class);
			roundExample.setOrderByClause("beginTime asc");
			Example.Criteria roundCriteria = roundExample.createCriteria();
			roundCriteria.andEqualTo("calendarId", dto.getCalendarId());
			roundCriteria.andEqualTo("projectId", dto.getProjectId());
			roundCriteria.andEqualTo("electionObj",Constants.DEPART_ADMIN);
			Date date = new Date();
			roundCriteria.andGreaterThanOrEqualTo("beginTime", date);
			roundCriteria.andLessThanOrEqualTo("endTime", date);
			roundCriteria.andEqualTo("openFlag", Constants.ONE);
			ElectionRounds electionRounds = elecRoundsDao.selectOneByExample(roundExample);
			for(ElectionApplyVo electionApplyVo:applylist) {
				electionApplyVo.setStatus(Constants.ZERO);
				if(session.isAcdemicDean()||electionRounds!=null) {
					electionApplyVo.setStatus(Constants.ONE);
				}
			}
		}
		PageInfo<ElectionApplyVo> pageInfo = new PageInfo<>(applylist);
		return pageInfo;
	}

	@Override
	public int reply(ElectionApply electionApply) {
		int result = electionApplyDao.updateByPrimaryKeySelective(electionApply);
		if(result<=0) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("electionApply.reply")));
		}
		return result;
	}

	@Override
	public int delete(Long calendarId) {
		Example example = new Example(ElectionApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", calendarId);
		criteria.andEqualTo("apply", Constants.ONE);
		int result = electionApplyDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("electionApply.application")));
		}
		return result;
	}

	@Override
	public int agree(Long id) {
		Session session = SessionUtils.getCurrentSession();
		ElectionApply electionApply = new ElectionApply();
		electionApply.setId(id);
		electionApply.setApply(Constants.ONE);
		electionApply.setApplyBy(session.realUid());
		int result = electionApplyDao.updateByPrimaryKeySelective(electionApply);
		if(result<=0) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("electionApply.agree")));
		}
		return result;
	}
	
	@Override
	public int apply(String studentId,Long calendarId,String courseCode) {
		Example example =new Example(ElectionApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("studentId", studentId);
		criteria.andEqualTo("calendarId", calendarId);
		criteria.andEqualTo("courseCode", courseCode);
		ElectionApply apply = electionApplyDao.selectOneByExample(example);
		if(apply!=null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.electionApply")));
		}
		Example cExample =new Example(ElectionApplyCourses.class);
		Example.Criteria cCriteria = cExample.createCriteria();
		cCriteria.andEqualTo("calendarId", calendarId);
		cCriteria.andEqualTo("courseCode", courseCode);
		ElectionApplyCourses electionApplyCourses = electionApplyCoursesDao.selectOneByExample(cExample);
		ElectionApply electionApply = new ElectionApply();
		electionApply.setStudentId(studentId);
		electionApply.setCalendarId(calendarId);
		electionApply.setCourseCode(courseCode);
		if(electionApplyCourses!=null) {
			electionApply.setMode(electionApplyCourses.getMode());
		}
		electionApply.setApply(Constants.ZERO);
		electionApply.setCreatedAt(new Date());
		int result = electionApplyDao.insertSelective(electionApply);
		if(result<=0) {
			throw new ParameterValidateException(I18nUtil.getMsg("electionApply.applyError"));
		}
		elecContextUtil.save("elecApplyCourses", electionApply);
		return result;
	}

}
