package com.server.edu.mutual.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.CulturePlan;
import com.server.edu.common.entity.LabelCreditCount;
import com.server.edu.common.enums.UserTypeEnum;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.Enum.MutualApplyAuditStatus;
import com.server.edu.mutual.controller.ElcMutualApplyController;
import com.server.edu.mutual.dao.ElcMutualApplyDao;
import com.server.edu.mutual.dao.ElcMutualApplySwitchDao;
import com.server.edu.mutual.dao.ElcMutualStdsDao;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcMutualApply;
import com.server.edu.mutual.rpc.CultureSerivceInvokerToMutual;
import com.server.edu.mutual.service.ElcMutualApplyService;
import com.server.edu.mutual.util.MutualApplyJugeUtil;
import com.server.edu.mutual.util.ProjectUtil;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMutualApplyServiceImpl implements ElcMutualApplyService {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualApplyController.class);
	
	@Autowired
	private ElcMutualApplyDao elcMutualApplyDao;
	
	@Autowired
	private ElcMutualStdsDao elcMutualStdsDao;
	
	@Autowired
	private ElcMutualApplySwitchDao elcMutualApplySwitchDao;
	
	@Override
	public PageInfo<ElcMutualApplyVo> getElcMutualApplyList(PageCondition<ElcMutualApplyDto> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		if(session.realType() != UserTypeEnum.STUDENT.getValue()) {
			throw new ParameterValidateException(I18nUtil.getMsg("elec.mustBeStu")); 
		}
		
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			dto.setInType(Constants.FIRST);
		}else {
			dto.setByType(Constants.FIRST);
		}
		
		List<String> projectIds = ProjectUtil.getProjectIds(dto.getProjectId());
		dto.setProjectIds(projectIds);
		
		List<ElcMutualApplyVo> list = elcMutualApplyDao.getElcMutualApplyList(condition.getCondition());
		
		if (StringUtils.equals(dto.getProjectId(),Constants.PROJ_UNGRADUATE)) {
			
		}else {
			PageInfo<ElcMutualApplyVo> coursesForStu = getElcMutualCoursesForStu(condition);
			List<ElcMutualApplyVo> coursesForStuList = coursesForStu.getList();
			if (CollectionUtil.isNotEmpty(coursesForStuList)) {
				list.addAll(coursesForStuList);
			}
		}
		
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
	
	@Transactional
	@Override
	public int apply(ElcMutualApplyDto dto) {
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();
		String studentId = session.realUid();
		
		int result = 0;
		// 校验学生是否可以申请互选课程
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
			MutualApplyJugeUtil mutualApplyJugeUtil = new MutualApplyJugeUtil(elcMutualApplySwitchDao,elcMutualApplyDao);
			Boolean isApplyMutualCourseFlag = mutualApplyJugeUtil.jugeApplyMutualCourses(dto, projectId, studentId);
			LOG.info("=========isApplyMutualCourseFlag:"+isApplyMutualCourseFlag+"=========");
			if (!isApplyMutualCourseFlag.booleanValue()) {
				return result;
			}
		}
		
		List<Long> mutualCourseIds = dto.getMutualCourseIds();
		List<ElcMutualApply>  elcMutualApplys= new ArrayList<ElcMutualApply>();
		for(Long mutualCourseId: mutualCourseIds) {
			ElcMutualApply elcMutualApply = new ElcMutualApply();
			BeanUtils.copyProperties(dto, elcMutualApply);
			elcMutualApply.setMutualCourseId(mutualCourseId);
			elcMutualApply.setStatus(Integer.parseInt(String.valueOf(MutualApplyAuditStatus.UN_AUDITED.status())));
			elcMutualApply.setStudentId(studentId);
			elcMutualApply.setUserId(studentId);
			elcMutualApply.setApplyAt(new Date());
			elcMutualApplys.add(elcMutualApply);
		}
		result = elcMutualApplyDao.insertList(elcMutualApplys);
		
		return result;
	}
	
	@Override
	public PageInfo<ElcMutualApplyVo> getElcMutualCoursesForStu(PageCondition<ElcMutualApplyDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();
		String studentId = dto.getStudentId();
		
		// 判断该学生是否在本研互选名单中
		ElcMutualCrossStuDto stuDto = new ElcMutualCrossStuDto();
		stuDto.setCalendarId(dto.getCalendarId());
		stuDto.setStudentId(studentId);
		ElcMutualCrossStuVo elcMutualCrossStuVo = elcMutualStdsDao.isInElcMutualStdList(stuDto);
		if (elcMutualCrossStuVo == null) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notInMutualStuList")); 
		}
		
		List<String> projectIds = ProjectUtil.getProjectIds(projectId);
		dto.setProjectIds(projectIds);
		
		dto.setStudentId(studentId);
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			dto.setInType(Constants.FIRST);
		}else {
			dto.setByType(Constants.FIRST);
		}
		List<ElcMutualApplyVo> list = elcMutualApplyDao.getElcMutualCoursesForStu(dto);
		
		if (StringUtils.equals(projectId,Constants.PROJ_UNGRADUATE)) {
			// 本科生可申请的互选课程为: 研究生管理员维护的互选课程
			
		}else {
			// 研究生可申请的互选课程为: 研究生培养计划中“补修课”与本科生管理员维护的互选课程取交集
			List<LabelCreditCount> planBXK = new ArrayList<LabelCreditCount>();
			List<LabelCreditCount> planCount = CultureSerivceInvokerToMutual.studentPlanCountByStuId(studentId);
			if (CollectionUtil.isNotEmpty(planCount)) {
				planBXK = planCount.stream().filter(vo->StringUtils.equals(vo.getLabelName(), "补修课")).collect(Collectors.toList());
			}

			if (CollectionUtil.isNotEmpty(planBXK)) {
				LabelCreditCount labelCreditCount = planBXK.get(0);
				long labelId = labelCreditCount.getLabelId().longValue();
				
				// 获取培养计划中的课程列表
				List<CulturePlan> listPlanVos = new ArrayList<CulturePlan>();
				RestResult restResult = CultureSerivceInvokerToMutual.getCulturePlanByStudentId(studentId, 0);
				String json = JSONObject.toJSON(restResult.getData()).toString();
				Map<String, Object> parse = (Map)JSON.parse(json);
				for (String key : parse.keySet()) {
					if (StringUtils.equals(key, "culturePlanList")) {
						String value = parse.get(key).toString();
						listPlanVos = JSONArray.parseArray(value, CulturePlan.class);
					}
				}
				
				// 获取培养计划中的补修课courseCode
				List<String> courseCodeBXK = listPlanVos.stream()
						                          .filter(vo->vo.getLabelId().longValue()==labelId)
						                          .map(CulturePlan::getCourseCode)
						                          .collect(Collectors.toList());
				LOG.info("---------------courseCodeBXK:" + courseCodeBXK.toString() + "--------------");
				
				// 补修课与互选维护课程取交集
				list = list.stream().filter(vo->courseCodeBXK.contains(vo.getCourseCode())).collect(Collectors.toList());
			}else {
				list = new ArrayList<ElcMutualApplyVo>();
			}
		}
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<ElcMutualApplyVo>(list);
		return pageInfo;
	}
	
	@Transactional
	@Override
	public int cancel(Long id) {
		ElcMutualApply elcMutualApply = elcMutualApplyDao.selectByPrimaryKey(id);
		if(elcMutualApply==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",I18nUtil.getMsg(""))); 
		}
		int result  = elcMutualApplyDao.deleteByPrimaryKey(id);
		return result;
	}
	
	@Transactional
	@Override
	public int delete(List<Long> ids) {
		Example example =new Example(ElcMutualApply.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id",ids);
		List<ElcMutualApply> list = elcMutualApplyDao.selectByExample(example);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",I18nUtil.getMsg(""))); 
		}
		int result  = elcMutualApplyDao.deleteByExample(example);
		return result;
	}

	@Override
	public ElcMutualApply getElcMutualApplyById(Long id) {
		return elcMutualApplyDao.selectByPrimaryKey(id);
	}

}
