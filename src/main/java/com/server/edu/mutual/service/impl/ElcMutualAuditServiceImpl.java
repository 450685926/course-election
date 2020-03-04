package com.server.edu.mutual.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.server.edu.common.rest.RestResult;
import com.server.edu.common.rest.ResultStatus;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.entity.Student;
import com.server.edu.mutual.entity.ElcMutualApplyCopyVo;
import com.server.edu.mutual.rpc.BaseresServiceExamInvoker;
import com.server.edu.mutual.rpc.CultureSerivceInvokerToMutual;
import com.server.edu.mutual.service.ElcMutualCommonService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.Enum.MutualApplyAuditStatus;
import com.server.edu.mutual.Enum.MutualApplyAuditType;
import com.server.edu.mutual.dao.ElcMutualApplyAuditLogsDao;
import com.server.edu.mutual.dao.ElcMutualApplyDao;
import com.server.edu.mutual.dao.ElcMutualApplySwitchDao;
import com.server.edu.mutual.dao.ElcMutualListDao;
import com.server.edu.mutual.dto.AgentApplyDto;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.entity.ElcMutualApply;
import com.server.edu.mutual.entity.ElcMutualApplyAuditLogs;
import com.server.edu.mutual.service.ElcMutualApplyService;
import com.server.edu.mutual.service.ElcMutualAuditService;
import com.server.edu.mutual.util.MutualApplyJugeUtil;
import com.server.edu.mutual.util.ProjectUtil;
import com.server.edu.mutual.vo.ElcMutualApplyAuditLogsVo;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.BeanUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMutualAuditServiceImpl implements ElcMutualAuditService {
	private static Logger LOG = LoggerFactory.getLogger(ElcMutualAuditServiceImpl.class);
	
	@Autowired
	private ElcMutualApplyDao elcMutualApplyDao;
	
	@Autowired
	private ElcMutualApplyAuditLogsDao elcMutualApplyAuditLogsDao;
	
	@Autowired
	private ElcMutualApplySwitchDao elcMutualApplySwitchDao;
	
	@Autowired
	private ElcMutualApplyService elcMutualApplyService;

	@Autowired
	private ElcMutualCommonService elcMutualCommonService;
	
	@Autowired
	private ElcMutualListDao elcMutualListDao;

	@Autowired
	private StudentDao studentDao;

	@Override
	public PageInfo<ElcMutualApplyVo> collegeApplyCourseList(PageCondition<ElcMutualApplyDto> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
    	if (!isAcdemicDean) {
    		throw new ParameterValidateException(I18nUtil.getMsg("elec.mustAcdemicDean"));
    	}

		List<String> projectIds = new ArrayList<>();
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			projectIds.add(Constants.PROJ_UNGRADUATE);
			dto.setInType(Constants.FIRST);
		}else {
			projectIds = ProjectUtil.getProjectIds(session.getCurrentManageDptId());
			dto.setByType(Constants.FIRST);
		}

		// 取消根据学院过滤
//		dto.setCollege(session.getFaculty());
		//封装学院集合
		packageCollegeList(session,dto);
		//封装部门数据
		dto.setProjectIds(projectIds);
		LOG.info("dto collegeList:" + dto.getCollegeList().toString());
		LOG.info("dto==condition.getCondition()?--->" + (dto == condition.getCondition()));
		List<ElcMutualApplyVo> list = elcMutualApplyDao.collegeApplyCourseList(condition.getCondition());
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<ElcMutualApplyVo> collegeApplyStuList(PageCondition<ElcMutualApplyDto> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
    	if (!isAcdemicDean) {
    		throw new ParameterValidateException(I18nUtil.getMsg("elec.mustAcdemicDean"));
    	}
        List<String> projectIds = new ArrayList<>();
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			dto.setInType(Constants.FIRST);
			projectIds.add(Constants.PROJ_UNGRADUATE);
		}else {
			dto.setByType(Constants.FIRST);
            projectIds = ProjectUtil.getProjectIds(session.getCurrentManageDptId());
		}
		/**
		 * 解决本研互选bug10674 行政学院教务员要查询本学院或本学院管理的学生 2020-2-20
		 */
		packageCollegeList(session,dto);
		/**
		 * 功能描述: 解决本研互选bug 10896
		 * @author: zhaoerhu
		 * @date: 2020/2/21 14:29
		 */
//		dto.setProjectId(session.getCurrentManageDptId());
		dto.setProjectIds(projectIds);
		LOG.info("dto collegeList:" + dto.getCollegeList().toString());
		List<ElcMutualApplyVo> list = elcMutualApplyDao.collegeApplyStuList(condition.getCondition());
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@Override
	public PageInfo<ElcMutualApplyVo> openCollegeApplyCourseList(PageCondition<ElcMutualApplyDto> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();

		boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
    	if (!isAcdemicDean) {
    		throw new ParameterValidateException(I18nUtil.getMsg("elec.mustAcdemicDean")); 
    	}

		/**
		 * 功能描述: 解决本研互选bug 10896
		 * @author: zhaoerhu
		 * @date: 2020/2/21 14:29
		 */
//		List<String> projectIds = new ArrayList<>();
		if(Constants.BK_CROSS.equals(dto.getMode())) {
//			projectIds.add(Constants.PROJ_UNGRADUATE);
			dto.setInType(Constants.FIRST);
		}else {
//			projectIds = ProjectUtil.getProjectIds(session.getCurrentManageDptId());
			dto.setByType(Constants.FIRST);
		}
//		dto.setProjectIds(projectIds);
		dto.setProjectId(projectId);
//		dto.setOpenCollege(session.getFaculty());
		dto.setCollegeList(elcMutualCommonService.getCollegeList(session));

		// 本科生只有行政学院审核通过，才能进行开课学院审核
//		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
		if (StringUtils.equals(projectId, Constants.PROJ_GRADUATE) || StringUtils.equals(projectId,Constants.PROJ_LINE_GRADUATE)) {
			dto.setStatusArray(Arrays.asList(MutualApplyAuditStatus.DEPART_AUDITED_APPROVED.status(),
					MutualApplyAuditStatus.AUDITED_APPROVED.status(),MutualApplyAuditStatus.AUDITED_UN_APPROVED.status()));
		}
		
		//跨学科选课只有当行政学院审核通过，才能进行开课学院审核
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			if (dto.getStatus() == null) {
				dto.setStatusArray(Arrays.asList(MutualApplyAuditStatus.DEPART_AUDITED_APPROVED.status(),
						MutualApplyAuditStatus.AUDITED_APPROVED.status(), MutualApplyAuditStatus.AUDITED_UN_APPROVED.status()));
			}
		}
		
		List<ElcMutualApplyVo> list = elcMutualApplyDao.openCollegeApplyCourseList(condition.getCondition());
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<ElcMutualApplyVo> openCollegeApplyStuList(PageCondition<ElcMutualApplyDto> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();

		boolean isAcdemicDean = StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE)) && !session.isAdmin() && session.isAcdemicDean();
    	if (!isAcdemicDean) {
    		throw new ParameterValidateException(I18nUtil.getMsg("elec.mustAcdemicDean"));
    	}

		/**
		 * 功能描述: 解决本研互选bug 10896
		 * @author: zhaoerhu
		 * @date: 2020/2/21 14:29
		 */
//		List<String> projectIds = new ArrayList<>();
		if(Constants.BK_CROSS.equals(dto.getMode())) {
//			projectIds.add(Constants.PROJ_UNGRADUATE);
			dto.setInType(Constants.FIRST);
		}else {
//			projectIds = ProjectUtil.getProjectIds(projectId);
			dto.setByType(Constants.FIRST);
		}
//		dto.setOpenCollege(session.getFaculty());
		dto.setCollegeList(elcMutualCommonService.getCollegeList(session));
//		dto.setProjectIds(projectIds);
		dto.setProjectId(projectId);

		// 只有本科生发起的申请流程才走该逻辑：本科生申请，只有当本科生行政学院审核通过，才能进行开课学院审核
		// 研究生发起的申请流程，本科生教务员直接进行开课学院审核
//		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
		if (StringUtils.equals(projectId, Constants.PROJ_GRADUATE) || StringUtils.equals(projectId,Constants.PROJ_LINE_GRADUATE)) {
			if (dto.getStatus() == null) {
				dto.setStatusArray(Arrays.asList(MutualApplyAuditStatus.DEPART_AUDITED_APPROVED.status(),
						MutualApplyAuditStatus.AUDITED_APPROVED.status(), MutualApplyAuditStatus.AUDITED_UN_APPROVED.status()));
			}
		}
		
		//跨学科选课只有当行政学院审核通过，才能进行开课学院审核
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			if (dto.getStatus() == null) {
				dto.setStatusArray(Arrays.asList(MutualApplyAuditStatus.DEPART_AUDITED_APPROVED.status(),
						MutualApplyAuditStatus.AUDITED_APPROVED.status(), MutualApplyAuditStatus.AUDITED_UN_APPROVED.status()));
			}
		}

		dto.setMode(ProjectUtil.convertMode(dto.getMode()));
		List<ElcMutualApplyVo> list = elcMutualApplyDao.openCollegeApplyStuList(condition.getCondition());
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@Transactional
	@Override
	public int aduit(ElcMutualApplyDto dto) {
		Session session = SessionUtils.getCurrentSession();
		updateMutualApply(dto, session.getCurrentManageDptId());
		int result = saveElcMutualAuditLog(dto, session.realUid());
		return result;
	}
	
	private void updateMutualApply(ElcMutualApplyDto dto, String projectId) {
		ElcMutualApply elcMutualApply = elcMutualApplyDao.selectByPrimaryKey(dto.getId());
		if(elcMutualApply==null) {
    		throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",I18nUtil.getMsg("election.elcMutualApply"))); 
		}

		Integer status = elcMutualApply.getStatus();
		if (MutualApplyAuditType.DEPARTMENT.type() == dto.getAuditType().intValue()) { // 行政学院审核
			if(!MutualApplyAuditStatus.UN_AUDITED.eq(status.intValue()) && !MutualApplyAuditStatus.DEPART_AUDITED_UN_APPROVED.eq(status.intValue())) {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.auditStatusError")); 
			}
			
			if (dto.getAuditFlag() == Constants.ZERO && StringUtils.isBlank(dto.getAuditReason())) {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApplyAudit.auditReson")); 
			}
			
			elcMutualApply.setAuditReason(dto.getAuditReason());
			if (dto.getAuditFlag().intValue() == Constants.ONE) {
				elcMutualApply.setStatus(Integer.parseInt(String.valueOf(MutualApplyAuditStatus.DEPART_AUDITED_APPROVED.status())));
			}else if (dto.getAuditFlag().intValue() == Constants.ZERO) {
				elcMutualApply.setStatus(Integer.parseInt(String.valueOf(MutualApplyAuditStatus.DEPART_AUDITED_UN_APPROVED.status())));
			}
		}else if (MutualApplyAuditType.CULTURE.type() == dto.getAuditType().intValue()) {// 开课学院审核
			if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) { // 本科生院审核研究生
				if(MutualApplyAuditStatus.UN_AUDITED.eq(status.intValue()) || MutualApplyAuditStatus.DEPART_AUDITED_UN_APPROVED.eq(status.intValue())) {
//					throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.auditStatusError")); 
				}
			}else {   // 研究生院审核本科生
				if(!MutualApplyAuditStatus.DEPART_AUDITED_APPROVED.eq(status.intValue()) && !MutualApplyAuditStatus.AUDITED_UN_APPROVED.eq(status.intValue())) {
					throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApply.auditStatusError")); 
				}
			}
			
			if (dto.getAuditFlag() == Constants.ZERO && StringUtils.isBlank(dto.getAuditReason())) {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApplyAudit.auditReson")); 
			}
			
			elcMutualApply.setAuditReason(dto.getAuditReason());
			if (dto.getAuditFlag().intValue() == Constants.ONE) {
				elcMutualApply.setStatus(Integer.parseInt(String.valueOf(MutualApplyAuditStatus.AUDITED_APPROVED.status())));
			}else if (dto.getAuditFlag().intValue() == Constants.ZERO) {
				elcMutualApply.setStatus(Integer.parseInt(String.valueOf(MutualApplyAuditStatus.AUDITED_UN_APPROVED.status())));
			}
		}
		elcMutualApplyDao.updateByPrimaryKeySelective(elcMutualApply);
		Integer mode = elcMutualApply.getMode();
		if(Constants.BK_CROSS.equals(mode)) {//只有跨学科选课才更新到培养计划中
			LOG.info("method updateCultureStuPlanRpc start...");
			packageElcMutualApplyDto(dto, elcMutualApply);
			updateCultureStuPlanRpc(dto);
			LOG.info("method updateCultureStuPlanRpc end...");
		}
	}
	
	/**保存互选审核日志
	 * @param dto
	 * @param userId
	 * @return
	 */
	private int saveElcMutualAuditLog(ElcMutualApplyDto dto, String userId) {
		ElcMutualApplyAuditLogs elcMutualApplyAuditLogs = new ElcMutualApplyAuditLogs();
		elcMutualApplyAuditLogs.setAuditUserId(userId);
		elcMutualApplyAuditLogs.setMuApplyId(dto.getId());
		elcMutualApplyAuditLogs.setAuditType(dto.getAuditType());
		elcMutualApplyAuditLogs.setApproved(dto.getAuditFlag());
		elcMutualApplyAuditLogs.setReason(dto.getAuditReason());
		elcMutualApplyAuditLogs.setAuditAt(new Date());
		return elcMutualApplyAuditLogsDao.insertSelective(elcMutualApplyAuditLogs);
	}

	@Override
	@Transactional
	public int agentApply(AgentApplyDto dto) {
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();

		int result = Constants.ZERO;
		
		// 校验学生是否可以申请互选课程
        if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
            ElcMutualApplyDto dto2 = new ElcMutualApplyDto();
            dto2.setCalendarId(dto.getCalendarId());
            dto2.setCategory(dto.getCategory());
            dto2.setMutualCourseIds(Arrays.asList(dto.getMutualCourseId()));
            dto2.setMode(dto.getMode());
            //初始化工具类
            MutualApplyJugeUtil mutualApplyJugeUtil = new MutualApplyJugeUtil(elcMutualApplySwitchDao,elcMutualApplyDao);
            //Boolean isApplyMutualCourseFlag = mutualApplyJugeUtil.jugeApplyMutualCourses(dto2, projectId, dto.getStudentId());
            LOG.info("===========it is current user is or not Academic officer:{}",StringUtils.equals(session.getCurrentRole(), String.valueOf(Constants.ONE))
                    && !session.isAdmin() && session.isAcdemicDean());
            //跨学科课程添加申请校验
            Boolean judgmentAcademicApplyMutualCourses = mutualApplyJugeUtil.judgmentAcademicApplyMutualCourses(dto2, projectId, dto.getStudentId(), session);
            LOG.info("=========isApplyMutualCourseFlag:"+judgmentAcademicApplyMutualCourses+"=========");
            if (!judgmentAcademicApplyMutualCourses.booleanValue()) {
                return result;
            }
        }
		
		Example example = new Example(ElcMutualApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", dto.getCalendarId());
		criteria.andEqualTo("studentId", dto.getStudentId());
		criteria.andEqualTo("mutualCourseId", dto.getMutualCourseId());
		ElcMutualApply elcMutualApply = elcMutualApplyDao.selectOneByExample(example);
		if(elcMutualApply!=null) {
    		throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.elcMutualApply"))); 
		}
		
		/** 本科生行政学院教务员代理申请同时审核；研究生行政学院教务员只代理申请不审核  */
		ElcMutualApply apply = new ElcMutualApply();
		apply.setCalendarId(dto.getCalendarId());
		apply.setStudentId(dto.getStudentId());
		apply.setMutualCourseId(dto.getMutualCourseId());
		apply.setUserId(session.realUid());
        apply.setMode(dto.getMode());
		apply.setApplyAt(new Date());
		int count = elcMutualListDao.countElectionCourse(String.valueOf(dto.getMutualCourseId()), dto.getStudentId());
		if(count>0) {
			apply.setCourseTakeType(CourseTakeType.RETAKE.type());
		}else {
			apply.setCourseTakeType(CourseTakeType.NORMAL.type());
		}
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
			apply.setStatus(MutualApplyAuditStatus.DEPART_AUDITED_APPROVED.status());
		}else {
			apply.setStatus(MutualApplyAuditStatus.UN_AUDITED.status());
		}
		result = elcMutualApplyDao.insertSelective(apply);
		
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
			ElcMutualApplyDto elcMutualApplyDto = new ElcMutualApplyDto();
			elcMutualApplyDto.setId(apply.getId());
			elcMutualApplyDto.setAuditType(MutualApplyAuditType.DEPARTMENT.type());
			elcMutualApplyDto.setAuditFlag(Constants.FIRST);
			result = saveElcMutualAuditLog(elcMutualApplyDto,session.realUid());
		}
		return result;
	}

	@Override
	public List<ElcMutualApplyAuditLogsVo> queryAuditLogList(ElcMutualApplyAuditLogsVo vo) throws IllegalAccessException, InvocationTargetException {
		ElcMutualApplyAuditLogsVo elcMutualApplyAuditLogsVo = new ElcMutualApplyAuditLogsVo();
		//ElcMutualApply elcMutualApply = elcMutualApplyService.getElcMutualApplyById(vo.getMuApplyId());
		//查询学生本研互选申请记录
		ElcMutualApplyCopyVo elcMutualApply = elcMutualApplyAuditLogsDao.getElcMutualApplyById(vo.getMuApplyId());
		BeanUtil.copyProperties(elcMutualApplyAuditLogsVo, elcMutualApply);

		//List<ElcMutualApplyAuditLogsVo> list = elcMutualApplyAuditLogsDao.queryAuditLogList(vo);
		//查询本研互选审核记录
		List<ElcMutualApplyAuditLogsVo> list = elcMutualApplyAuditLogsDao.queryAuditLogLists(vo);
		list.add(0, elcMutualApplyAuditLogsVo);
		return list;
	}

	@Override
	public List<ElcMutualApplyVo> getOpenCollegeAuditList(ElcMutualApplyDto dto) {
		return elcMutualApplyDao.getOpenCollegeAuditList(dto);
	}

	/**
	 * 功能描述:封装当前的学院数据（当前学院+管理的学院）
	 *
	 * 备注：教务员不仅仅存在当前学院，还可以管理多个学院
	 *
	 * @params: [session, dto]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/2/4 17:04
	 */
	private void packageCollegeList(Session session, ElcMutualApplyDto dto) {
		//封装学院数据
//		dto.setCollegeList(elcMutualCommonService.getCollegeList(session));
		LOG.info("进入   packageCollegeList   。。。");
		dto.setCollegeList(elcMutualCommonService.getCollegeList());
	}

	/**
	 * 功能描述: 如果开课学院审核通过，则远程调用培养接口，更新学生培养计划选课状态
	 *
	 * @params: [elcMutualApply]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/2/18 11:07
	 */
	private void updateCultureStuPlanRpc(ElcMutualApplyDto elcMutualApply) {
		if (elcMutualApply.getStatus().equals(Integer.parseInt(String.valueOf(MutualApplyAuditStatus.AUDITED_APPROVED.status())))) {
			LOG.info("updateCultureStuPlanRpc rpc begin");
			RestResult result = CultureSerivceInvokerToMutual.updateCulturePlan4Stu(elcMutualApply);
			if (null == result || result.getCode() != ResultStatus.SUCCESS.code()) {//更新培养计划异常，则手动抛出运行时异常，事务回滚
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApplyAudit.planFail"));
			}
			LOG.info("code --- > " + result.getCode());
			LOG.info("updateCultureStuPlanRpc rpc end");
		}
	}

	/**
	 * 功能描述: 封装DTO
	 *
	 * @params: [dto]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/2/25 22:29
	 */
	private void packageElcMutualApplyDto(ElcMutualApplyDto dto, ElcMutualApply elcMutualApply) {
		Example stuExample = new Example(Student.class);
		Example.Criteria criteria = stuExample.createCriteria();
		criteria.andEqualTo("studentCode", dto.getStudentId());
		Student student = studentDao.selectOneByExample(stuExample);
		Integer currentGrade = student.getCurrentGrade();
		LOG.info("student current grade:" + currentGrade);
		//查询本科生当前学期
		Long stuCalendarId = BaseresServiceExamInvoker.getCalendarId(currentGrade, 1);
		LOG.info("student current calendarId:" + stuCalendarId);
		if (stuCalendarId == null) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualApplyAudit.getCalendarFail"));
		}
		//获取学生所选课程的当前学期
		Long courseCalendarId = dto.getCalendarId();
		BigDecimal semester = new BigDecimal(courseCalendarId).subtract(new BigDecimal(stuCalendarId));
		LOG.info("semester:" + semester.longValue());
		// 经与宁娟、吕鹏沟通，学期差值+1，和选课计算方式保持一致
		dto.setSemester(semester.longValue() + 1);
		//更新培养计划
		dto.setStatus(elcMutualApply.getStatus());
	}
}
