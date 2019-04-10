package com.server.edu.election.service.impl;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dao.ElcLogDao;
import com.server.edu.election.dao.ElcMedWithdrawApplyDao;
import com.server.edu.election.dao.ElcMedWithdrawApplyLogDao;
import com.server.edu.election.dao.ElcMedWithdrawRuleRefCourDao;
import com.server.edu.election.dao.ElcMedWithdrawRulesDao;
import com.server.edu.election.dao.ElcRoundsCourDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dao.TeachingClassDao;
import com.server.edu.election.dto.ElcMedWithdrawApplyDto;
import com.server.edu.election.dto.ElcMedWithdrawRuleRefCourDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.ElcLog;
import com.server.edu.election.entity.ElcMedWithdrawApply;
import com.server.edu.election.entity.ElcMedWithdrawApplyLog;
import com.server.edu.election.entity.ElcMedWithdrawRules;
import com.server.edu.election.entity.ElcRoundsCour;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.service.ElcMedWithdrawApplyService;
import com.server.edu.election.vo.ElcCourseTakeVo;
import com.server.edu.election.vo.ElcMedWithdrawApplyVo;
import com.server.edu.election.vo.ElcMedWithdrawRuleRefCourVo;
import com.server.edu.election.vo.TeachingClassVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMedWithdrawApplyServiceImpl implements ElcMedWithdrawApplyService {
	@Autowired
	private ElcMedWithdrawApplyDao elcMedWithdrawApplyDao;
	@Autowired
	private ElcMedWithdrawApplyLogDao elcMedWithdrawApplyLogDao;
	@Autowired
	private ElcCourseTakeDao elcCourseTakeDao;
	@Autowired
	private ElcMedWithdrawRuleRefCourDao elcMedWithdrawRuleRefCourDao;
	@Autowired
	private TeachingClassDao teachingClassDao;
	@Autowired
	private ElcMedWithdrawRulesDao elcMedWithdrawRulesDao;
	@Autowired
	private ElectionConstantsDao electionConstantsDao;
	@Autowired
	private ElecRoundsDao elecRoundsDao;
	@Autowired
	private ElcRoundsCourDao elcRoundsCourDao;
	@Autowired
	private ElcLogDao elcLogDao;
	
	@Override
	public PageInfo<ElcCourseTakeVo> applyList(PageCondition<ElcMedWithdrawApplyDto> condition){
		List<ElcCourseTakeVo> elcCourseTakes= new ArrayList<>();
		ElcMedWithdrawApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		String uid = session.realUid();
		int userType = session.realType();
		if(UserTypeEnum.STUDENT.is(userType)) {
			PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
			elcCourseTakes = elcCourseTakeDao.findUnApplyCourses(uid,dto.getCalendarId());
		}
		PageInfo<ElcCourseTakeVo> pageInfo =new PageInfo<>(elcCourseTakes);
		return pageInfo;
	}
	
	@Override
	public PageInfo<ElcMedWithdrawApplyVo> applyLogs(PageCondition<ElcMedWithdrawApplyDto> condition){
		List<ElcMedWithdrawApplyVo> list = new ArrayList<>();
		ElcMedWithdrawApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		String uid = session.realUid();
		int userType = session.realType();
		if(UserTypeEnum.STUDENT.is(userType)) {
			dto.setStudentId(uid);
			PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
			list = elcMedWithdrawApplyDao.selectApplyLogs(condition.getCondition());
		}
		PageInfo<ElcMedWithdrawApplyVo> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<ElcMedWithdrawApplyVo> list(PageCondition<ElcMedWithdrawApplyDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElcMedWithdrawApplyVo> list = elcMedWithdrawApplyDao.selectMedApplyList(condition.getCondition());
		PageInfo<ElcMedWithdrawApplyVo> pageInfo =new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	@Transactional
	public int apply(Long id,Integer projectId) {
		//选择退课课程
		Example example = new Example(ElcCourseTake.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("id", id);
		ElcCourseTake elcCourseTake = elcCourseTakeDao.selectOneByExample(example);
		if(elcCourseTake==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.courseTakeError"));
		}
		//保存申请和日志
		ElcMedWithdrawApply elcMedWithdrawApply = new ElcMedWithdrawApply();
		BeanUtils.copyProperties(elcCourseTake, elcMedWithdrawApply);
		elcMedWithdrawApply.setId(null);
		elcMedWithdrawApply.setWithdrawFlag(false);
		int result = elcMedWithdrawApplyDao.insertSelective(elcMedWithdrawApply);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.retakeCourse"));
		}
		ElcMedWithdrawApplyLog log = new ElcMedWithdrawApplyLog();
		log.setStudentId(elcCourseTake.getStudentId());
		log.setTeachingClassId(elcCourseTake.getTeachingClassId());
		//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
		try {
			InetAddress address = InetAddress.getLocalHost();
			String hostAddress = address.getHostAddress();
			log.setOprationClientIp(hostAddress);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userId =SessionUtils.getCurrentSession().realUid();
		String userName = SessionUtils.getCurrentSession().realName();
		log.setOprationObjCode(userId);
		log.setOprationObjName(userName);
		log.setTargetObjCode(userId);
		log.setTargetObjName(userName);
		log.setOprationType(Constants.EN_AUDIT);
		log.setCreatedAt(new Date());

		//查询选择学期的退课规则和课程
		ElcMedWithdrawRuleRefCourDto ruleDto = new ElcMedWithdrawRuleRefCourDto();
		ruleDto.setCalendarId(elcCourseTake.getCalendarId());
		List<ElcMedWithdrawRuleRefCourVo> list = elcMedWithdrawRuleRefCourDao.selectMedRuleRefCours(ruleDto);
		List<ElcMedWithdrawRuleRefCourVo> refList = list.stream().filter(c->elcCourseTake.getTeachingClassId().equals(c.getTeachingClassId())).collect(Collectors.toList());
		if(CollectionUtil.isEmpty(refList)) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.courseExist"));
		}
		//获取期中退课规则
		Example elcExample = new Example(ElcMedWithdrawRules.class);
		Example.Criteria elcCriteria = elcExample.createCriteria();
		elcCriteria.andEqualTo("projectId",projectId);
		elcCriteria.andEqualTo("calendarId",elcCourseTake.getCalendarId());
		ElcMedWithdrawRules elcMedWithdrawRules = elcMedWithdrawRulesDao.selectOneByExample(elcExample);
		//规则启用
		if(elcMedWithdrawRules.getOpenFlag()) {
		    //查询教学班信息得到周数
			ElcResultQuery elcResultQuery = new ElcResultQuery();
			elcResultQuery.setTeachingClassId(elcCourseTake.getTeachingClassId());
			List<TeachingClassVo> teachingClassList = teachingClassDao.findTeachingClass(elcResultQuery);
			if(CollectionUtil.isEmpty(teachingClassList)) {
				log.setContent(I18nUtil.getMsg("elcMedWithdraw.teachingClassError"));
				result = elcMedWithdrawApplyLogDao.insertSelective(log);
				throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.teachingClassError"));
			}
			//课程结束周小于等于不得退课
			TeachingClassVo teachingClassVo = teachingClassList.get(0);
			int week = new BigDecimal(teachingClassVo.getPeriod()).divide(new BigDecimal(teachingClassVo.getWeekHour()),BigDecimal.
				    ROUND_HALF_UP).intValue();
			if(elcMedWithdrawRules.getCourseEndWeek()>week) {
				log.setContent(I18nUtil.getMsg("elcMedWithdraw.courseEndWeek"));
				result = elcMedWithdrawApplyLogDao.insertSelective(log);
				throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.courseEndWeek"));
			}
			//外语强化班不得退课
			if(elcMedWithdrawRules.getEnglishCourse()) {
				Example constant = new Example(ElectionConstants.class);
				Example.Criteria constantCriteria = constant.createCriteria();
				List<String> keys=new ArrayList<>();
				keys.add("LANGUAGECOURSEFORGERMANY");//德强
				keys.add("LANGUAGECOURSEFORFRENCE");//法强
				constantCriteria.andEqualTo("managerDeptId",projectId.toString());
				constantCriteria.andIn("key",keys);
				List<ElectionConstants> electionConstants = electionConstantsDao.selectByExample(constant);
				StringBuilder stringBuilder = new StringBuilder();
				for(ElectionConstants elc:electionConstants) {
					stringBuilder.append(elc.getValue());
					stringBuilder.append(",");
				}
				String courses = stringBuilder.deleteCharAt(stringBuilder.length()-1).toString();
				if(courses.contains(elcCourseTake.getCourseCode())) {
					log.setContent(I18nUtil.getMsg("elcMedWithdraw.englishCourse"));
					result = elcMedWithdrawApplyLogDao.insertSelective(log);
					throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.englishCourse"));
				}
				
			}
			//体育课不得退课
			if(elcMedWithdrawRules.getPeCourse()) {
				Example constant = new Example(ElectionConstants.class);
				Example.Criteria constantCriteria = constant.createCriteria();
				constantCriteria.andEqualTo("managerDeptId",projectId.toString());
				constantCriteria.andEqualTo("key","PE_COURSE_CODES");
			    ElectionConstants electionConstants = electionConstantsDao.selectOneByExample(constant);
				String courses = electionConstants.getValue();
				if(courses.contains(elcCourseTake.getCourseCode())) {
					log.setContent(I18nUtil.getMsg("elcMedWithdraw.pcCourse"));
					result = elcMedWithdrawApplyLogDao.insertSelective(log);
					throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.pcCourse"));
				}
			}
			//实践课不得退课
			if(elcMedWithdrawRules.getPracticeCourse()) {
				Example rounds = new Example(ElectionRounds.class); 
				Example.Criteria roundsCriteria = rounds.createCriteria();
				roundsCriteria.andEqualTo("projectId",projectId.toString());
				roundsCriteria.andEqualTo("calendarId",elcCourseTake.getCalendarId());
				roundsCriteria.andEqualTo("mode", Constants.PRACTICE_COURSE_);
				roundsCriteria.andEqualTo("openFlag", Constants.IS_OPEN);
				List<ElectionRounds> roundsList = elecRoundsDao.selectByExample(rounds);
				if(CollectionUtil.isNotEmpty(roundsList)) {
					List<Long> roundIds = roundsList.stream().map(ElectionRounds::getId).collect(Collectors.toList());
					Example couRounds = new Example(ElcRoundsCour.class);
					Example.Criteria couCriteria = couRounds.createCriteria();
					couCriteria.andIn("roundsId", roundIds);
					List<ElcRoundsCour> elcRoundsCours = elcRoundsCourDao.selectByExample(couRounds);
					if(CollectionUtil.isNotEmpty(elcRoundsCours)) {
						List<String> courseCodes = elcRoundsCours.stream().map(ElcRoundsCour::getCourseCode).collect(Collectors.toList());
						if(courseCodes.contains(elcCourseTake.getCourseCode())) {
							log.setContent(I18nUtil.getMsg("elcMedWithdraw.practiceCourse"));
							result = elcMedWithdrawApplyLogDao.insertSelective(log);
							throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.practiceCourse"));
						}
					}
					
				}

			}
			//重修课程不得退课
			if(elcMedWithdrawRules.getRetakeCourse()) {
				if(Constants.REBUILD_CALSS.equals(elcCourseTake.getCourseTakeType().toString())) {
					log.setContent(I18nUtil.getMsg("elcMedWithdraw.retakeCourse"));
					result = elcMedWithdrawApplyLogDao.insertSelective(log);
					throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.retakeCourse"));
				}
			}
			
		}
		result = elcMedWithdrawApplyLogDao.insertSelective(log);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.retakeCourse"));
		}
		return result;
	}

	
	@Override
	@Transactional
	public int approval(List<Long> ids) {
		List<ElcMedWithdrawApplyLog> logs =new ArrayList<>();
		Example example = new Example(ElcMedWithdrawApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		List<ElcMedWithdrawApply> list = elcMedWithdrawApplyDao.selectByExample(example);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.applyDataError"));
		}
		List<Long> takeIds = new ArrayList<>();
		List<ElcLog> elcLogs = new ArrayList<>();
		list.forEach(temp->{
			Example takeExample = new Example(ElcCourseTake.class);
			Example.Criteria takeCriteria = takeExample.createCriteria();
			takeCriteria.andEqualTo("studentId",temp.getStudentId());
			takeCriteria.andEqualTo("teachingClassId",temp.getTeachingClassId());
			ElcCourseTake elcCourseTake = elcCourseTakeDao.selectOneByExample(takeExample);
			if(elcCourseTake==null) {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.elcCourseTakeError"));
			}
			takeIds.add(elcCourseTake.getId());
			ElcLog elcLog = new ElcLog();
			BeanUtils.copyProperties(elcCourseTake, elcLog);
			elcLog.setId(null);
			ElcMedWithdrawApplyLog log = new ElcMedWithdrawApplyLog();
			temp.setWithdrawFlag(ElcMedWithdrawApplyVo.WITHDRAW_FLAG_);
			log.setStudentId(temp.getStudentId());
			log.setTeachingClassId(temp.getTeachingClassId());
			ElcResultQuery elcResultQuery = new ElcResultQuery();
			elcResultQuery.setTeachingClassId(elcCourseTake.getTeachingClassId());
			List<TeachingClassVo> teachingClassList = teachingClassDao.findTeachingClass(elcResultQuery);
			if(CollectionUtil.isEmpty(teachingClassList)) {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.teachingClassError"));
			}
			TeachingClassVo teachingClassVo = teachingClassList.get(0);
			elcLog.setCourseCode(teachingClassVo.getCourseCode());
			elcLog.setCourseCode(teachingClassVo.getCourseName());
			elcLog.setTeachingClassCode(teachingClassVo.getCode());
			//获取的是本地的IP地址 //PC-20140317PXKX/192.168.0.121
			try {
				InetAddress address = InetAddress.getLocalHost();
				String hostAddress = address.getHostAddress();
				elcLog.setCreateIp(hostAddress);
				log.setOprationClientIp(hostAddress);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String userId =SessionUtils.getCurrentSession().realUid();
			String userName = SessionUtils.getCurrentSession().realName();
			elcLog.setCreateBy(userId);
			elcLog.setCreateName(userName);
			elcLog.setCreatedAt(new Date());
			elcLogs.add(elcLog);
			log.setOprationObjCode(userId);
			log.setOprationObjName(userName);
			log.setTargetObjCode(userId);
			log.setTargetObjName(userName);
			log.setOprationType(Constants.EN_AUDIT);
			log.setCreatedAt(new Date());
			logs.add(log);
		});
		//审核
		int result = elcMedWithdrawApplyDao.batchUpdate(list);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.approvalError"));
		}
		result = elcMedWithdrawApplyLogDao.insertList(logs);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.approvalError"));
		}
		//批量删除选课数据
		Example takeExample = new Example(ElcCourseTake.class);
		Example.Criteria takeCriteria = takeExample.createCriteria();
		takeCriteria.andIn("id", takeIds);
		result = elcCourseTakeDao.deleteByExample(takeExample);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.approvalError"));
		}
		//批量保存退课日志数据
		result = elcLogDao.insertList(elcLogs);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMedWithdraw.approvalError"));
		}
		return result;
	}

}
