package com.server.edu.mutual.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.LabelCreditCount;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.util.CommonConstant;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.Enum.MutualApplyAuditStatus;
import com.server.edu.mutual.controller.ElcMutualApplyController;
import com.server.edu.mutual.dao.ElcCrossStdsDao;
import com.server.edu.mutual.dao.ElcMutualApplyDao;
import com.server.edu.mutual.dao.ElcMutualApplySwitchDao;
import com.server.edu.mutual.dao.ElcMutualListDao;
import com.server.edu.mutual.dao.ElcMutualStdsDao;
import com.server.edu.mutual.dto.ElcMutualApplyDto;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcMutualApply;
import com.server.edu.mutual.entity.ElcMutualApplyTurns;
import com.server.edu.mutual.rpc.CultureSerivceInvokerToMutual;
import com.server.edu.mutual.service.ElcMutualApplyService;
import com.server.edu.mutual.util.MutualApplyJugeUtil;
import com.server.edu.mutual.util.ProjectUtil;
import com.server.edu.mutual.vo.CulturePlanVo;
import com.server.edu.mutual.vo.ElcMutualApplyVo;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class ElcMutualApplyServiceImpl implements ElcMutualApplyService {
	private static Logger LOG =
	        LoggerFactory.getLogger(ElcMutualApplyController.class);
	
	@Autowired
	private ElcMutualApplyDao elcMutualApplyDao;
	
	@Autowired
	private ElcMutualStdsDao elcMutualStdsDao;

	@Autowired
	private ElcCrossStdsDao elcCrossStdsDao;

	@Autowired
	private ElcMutualApplySwitchDao elcMutualApplySwitchDao;
	
	@Autowired
	private ElcMutualListDao elcMutualListDao;
	
	@Override
	public PageInfo<ElcMutualApplyVo> getElcMutualApplyList(PageCondition<ElcMutualApplyDto> condition) {
		//由于对sql结果集进行二次加减处理导致自动分页失效，因此取消自动分页，采用手动分页
//		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
//		Session session = SessionUtils.getCurrentSession();
//		if(session.realType() != UserTypeEnum.STUDENT.getValue()) {
//			throw new ParameterValidateException(I18nUtil.getMsg("elec.mustBeStu")); 
//		}
		
		List<String> projectIds = new ArrayList<>();
		if (Constants.BK_CROSS.equals(dto.getMode())) {
			projectIds.add(Constants.PROJ_UNGRADUATE);
			dto.setInType(Constants.FIRST);
		} else {
			projectIds = ProjectUtil.getProjectIds(dto.getProjectId());
			dto.setByType(Constants.FIRST);
		}

		dto.setProjectIds(projectIds);
		
		List<ElcMutualApplyVo> list = elcMutualApplyDao.getElcMutualApplyList(condition.getCondition());
		
		if (StringUtils.equals(dto.getProjectId(),Constants.PROJ_UNGRADUATE)) {

		}else {
//			PageInfo<ElcMutualApplyVo> coursesForStu = getElcMutualCoursesForStu(condition);
			PageInfo<ElcMutualApplyVo> coursesForStu = getElcMutualCoursesForStuNotLimit(condition);
			List<ElcMutualApplyVo> coursesForStuList = coursesForStu.getList();
			if (CollectionUtil.isNotEmpty(coursesForStuList)) {
				list.addAll(coursesForStuList);
			}
		}
		
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<>(list);
		//对pageInfo容器手动分页
		pageInfoLimit(list,pageInfo,condition.getPageNum_(),condition.getPageSize_());
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
			int count = elcMutualListDao.countElectionCourse(String.valueOf(mutualCourseId), studentId);
			if(count>0) {
				elcMutualApply.setCourseTakeType(2);
			}else {
				elcMutualApply.setCourseTakeType(1);
			}
			LOG.info("elcMutualApplycount: "+count);
			LOG.info("elcMutualApplygetCourseTakeType: "+elcMutualApply.getCourseTakeType());
			elcMutualApplys.add(elcMutualApply);
		}
		result = elcMutualApplyDao.insertList(elcMutualApplys);
		
		return result;
	}

	/**
	 * 功能描述: 全量获取，手动逻辑分页
	 *
	 * @params: [condition]
	 * @return: com.github.pagehelper.PageInfo<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/2/25 10:53
	 */
	@Override
	public PageInfo<ElcMutualApplyVo> getElcMutualCoursesForStu(PageCondition<ElcMutualApplyDto> condition){
		LOG.info("*******getElcMutualCoursesForStu********");
////		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
//		ElcMutualApplyDto dto = condition.getCondition();
//		Session session = SessionUtils.getCurrentSession();
//		String projectId = session.getCurrentManageDptId();
//		//本地调试部门id
//		//String projectId="1";
//		String studentId = dto.getStudentId();
//
//		ElcMutualCrossStuDto stuDto = new ElcMutualCrossStuDto();
//		stuDto.setCalendarId(dto.getCalendarId());
//		stuDto.setStudentId(studentId);
//		//返回单个po对象在切换学期时代码报错（切换未上送学生id所以返回多条记录），故统一使用list接收
//		List<ElcMutualCrossStuVo> elcMutualCrossStuVos = null;
//		// 判断该学生是否在本研互选名单中
//		/*if (null != dto.getMode() && dto.getMode() == Constants.BK_MUTUAL) {
//			elcMutualCrossStuVo = elcMutualStdsDao.isInElcMutualStdList(stuDto);
//		}*/
//		boolean checkFlag = !CommonConstant.isEmptyStr(dto.getCourseSelectionMark()) && "true".equalsIgnoreCase(dto.getCourseSelectionMark());
//		LOG.info("-------------------it is check add course election flag:{}",checkFlag);
//		if(checkFlag) {
//			//判断校验是否开启选课、选课时间是否符合
//			Example example = new Example(ElcMutualApplyTurns.class);
//			Example.Criteria criteria = example.createCriteria();
//			criteria.andEqualTo("calendarId", dto.getCalendarId());
//			criteria.andEqualTo("projectId", projectId);
//			criteria.andEqualTo("category", dto.getCategory());
//			criteria.andEqualTo("open", Constants.DELETE_TRUE);
//			//查询选课时间开关
//			ElcMutualApplyTurns elcMutualApplyTurns = elcMutualApplySwitchDao.selectOneByExample(example);
//			Date date = new Date();
//			//当前时间如果早于开始时间或者晚于结束时间,则抛出异常、无法添加选课课程
//			if (elcMutualApplyTurns != null) {
//				if (date.before(elcMutualApplyTurns.getBeginAt()) || date.after(elcMutualApplyTurns.getEndAt())) {
//					throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notDateInCrossElection"));
//				}
//			} else {
//				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notSearchCrossOpen"));
//			}
//		}
//		// 判断该学生是否在跨院系互选名单中
//		if (null != dto.getMode() && dto.getMode() == Constants.BK_CROSS) {
//			elcMutualCrossStuVos = elcCrossStdsDao.isInElcMutualStdList(stuDto);
//		} else {
//			elcMutualCrossStuVos = elcMutualStdsDao.isInElcMutualStdList(stuDto);
//		}
//
//		if (elcMutualCrossStuVos == null || elcMutualCrossStuVos.isEmpty()) {
//			if (null != dto.getMode() && dto.getMode() == Constants.BK_CROSS) {
//				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notInCrossStuList"));
//			} else {
//				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notInMutualStuList"));
//			}
//		}
//		List<String> projectIds =new ArrayList<>();
//		if (null != dto.getMode() && dto.getMode() == Constants.BK_CROSS) {
//			projectIds.add(Constants.PROJ_UNGRADUATE);
//		} else {
//			projectIds = ProjectUtil.getProjectIds(dto.getProjectId());
//		}
//
//		dto.setProjectIds(projectIds);
//
//		dto.setStudentId(studentId);
//		if(Constants.BK_CROSS.equals(dto.getMode())) {
//			dto.setInType(Constants.FIRST);
//		}else {
//			dto.setByType(Constants.FIRST);
//		}
//		LOG.info("--------------projectIds--------------"+JSONArray.toJSONString(projectIds));
//		//移动分页位置
//		//由于对纯sql分页结果集做二次加减操作导致自动分页失效，故取消自动分页，采取手动分页。
////		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
//		List<ElcMutualApplyVo> list = elcMutualApplyDao.getElcMutualCoursesForStu(dto);
////		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<ElcMutualApplyVo>(list);
//		List<String> courseCode = list.stream()
//                .filter(v->!v.getCourseCode().isEmpty()).map(ElcMutualApplyVo::getCourseCode)
//                .collect(Collectors.toList());
//		LOG.info("---------------可申请的课程代码--------------"+JSONArray.toJSONString(courseCode));
//
//		// 本科生可申请的跨院系课程不在培养计划内
//		if (StringUtils.equals(projectId,Constants.PROJ_UNGRADUATE)) {
//			LOG.info("---------------dto.getMode()--------------"+dto.getMode());
//
////			List<ElcMutualApplyVo> list2 =pageInfo.getList();
//
//			if(Constants.BK_CROSS.equals(dto.getMode())) {
//				List<String> courseCodes = CultureSerivceInvokerToMutual.getCulturePlanCourseCodeByStudentId(studentId);
//				LOG.info("---------------getCulturePlanCourseCodeByStudentId--------------"+courseCodes.size());
////				list2 = list2.stream().filter(vo->!courseCodes.contains(vo.getCourseCode())).collect(Collectors.toList());
//				list = list.stream().filter(vo->!courseCodes.contains(vo.getCourseCode())).collect(Collectors.toList());
//			}
////			pageInfo=new PageInfo<ElcMutualApplyVo>(list2);
////			pageInfo.setList(list2);
//
//		}else {
//			// 研究生可申请的互选课程为: 研究生培养计划中“补修课”与本科生管理员维护的互选课程取交集
//			List<LabelCreditCount> planBXK = new ArrayList<LabelCreditCount>();
//			List<LabelCreditCount> planCount = CultureSerivceInvokerToMutual.studentPlanCountByStuId(studentId);
//			if (CollectionUtil.isNotEmpty(planCount)) {
//				planBXK = planCount.stream().filter(vo->StringUtils.equals(vo.getLabelName(), "补修课")).collect(Collectors.toList());
//			}
//
//			if (CollectionUtil.isNotEmpty(planBXK)) {
//				LabelCreditCount labelCreditCount = planBXK.get(0);
//				long labelId = labelCreditCount.getLabelId().longValue();
//
//				// 获取培养计划中的课程列表
//				List<CulturePlanVo> listPlanVos = new ArrayList<CulturePlanVo>();
//				RestResult restResult = CultureSerivceInvokerToMutual.getCulturePlanByStudentId(studentId, 0);
//				String json = JSONObject.toJSON(restResult.getData()).toString();
//				Map<String, Object> parse = (Map)JSON.parse(json);
//				for (String key : parse.keySet()) {
//					if (StringUtils.equals(key, "culturePlanList")) {
//						String value = parse.get(key).toString();
//						listPlanVos = JSONArray.parseArray(value, CulturePlanVo.class);
//					}
//				}
//
//				// 获取培养计划中的补修课courseCode
//				List<String> courseCodeBXK = listPlanVos.stream()
//						                          .filter(vo->vo.getLabelId().longValue()==labelId)
//						                          .map(CulturePlanVo::getCourseCode)
//						                          .collect(Collectors.toList());
//				LOG.info("---------------courseCodeBXK:" + courseCodeBXK.toString() + "--------------");
////				List<ElcMutualApplyVo> list2 =pageInfo.getList();
//
//				// 补修课与互选维护课程取交集
////				list2 = list2.stream().filter(vo->courseCodeBXK.contains(vo.getCourseCode())).collect(Collectors.toList());
////				pageInfo = new PageInfo<ElcMutualApplyVo>(list2);
////				pageInfo.setList(list2);
//				list = list.stream().filter(vo->courseCodeBXK.contains(vo.getCourseCode())).collect(Collectors.toList());
//			}else {
//				list = new ArrayList<ElcMutualApplyVo>();
////				pageInfo = new PageInfo<ElcMutualApplyVo>(list);
////				pageInfo.setList(list);
//			}
//		}
		List<ElcMutualApplyVo> list = getElcMutualCoursesForStudent(condition);
		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<ElcMutualApplyVo>(list);
		pageInfoLimit(list, pageInfo, condition.getPageNum_(), condition.getPageSize_());
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






	/**
	 * 功能描述: 复用方法，抽取封装
	 *
	 * @params: [condition]
	 * @return: java.util.List
	 * @author: zhaoerhu
	 * @date: 2020/2/24 22:13
	 */
	private List<ElcMutualApplyVo> getElcMutualCoursesForStudent(PageCondition<ElcMutualApplyDto> condition){
		//		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();
		//本地调试部门id
//		String projectId="2";
		String studentId = dto.getStudentId();

		ElcMutualCrossStuDto stuDto = new ElcMutualCrossStuDto();
		stuDto.setCalendarId(dto.getCalendarId());
		stuDto.setStudentId(studentId);
		//返回单个po对象在切换学期时代码报错（切换未上送学生id所以返回多条记录），故统一使用list接收
		List<ElcMutualCrossStuVo> elcMutualCrossStuVos = null;
		// 判断该学生是否在本研互选名单中
		/*if (null != dto.getMode() && dto.getMode() == Constants.BK_MUTUAL) {
			elcMutualCrossStuVo = elcMutualStdsDao.isInElcMutualStdList(stuDto);
		}*/
		boolean checkFlag = !CommonConstant.isEmptyStr(dto.getCourseSelectionMark()) && "true".equalsIgnoreCase(dto.getCourseSelectionMark());
		LOG.info("-------------------it is check add course election flag:{}",checkFlag);
		if(checkFlag) {
			//判断校验是否开启选课、选课时间是否符合
			Example example = new Example(ElcMutualApplyTurns.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("calendarId", dto.getCalendarId());
			criteria.andEqualTo("projectId", projectId);
			criteria.andEqualTo("category", dto.getCategory());
			criteria.andEqualTo("open", Constants.DELETE_TRUE);
			//查询选课时间开关
			ElcMutualApplyTurns elcMutualApplyTurns = elcMutualApplySwitchDao.selectOneByExample(example);
			Date date = new Date();
			//当前时间如果早于开始时间或者晚于结束时间,则抛出异常、无法添加选课课程
			if (elcMutualApplyTurns != null) {
				if (date.before(elcMutualApplyTurns.getBeginAt()) || date.after(elcMutualApplyTurns.getEndAt())) {
					throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notDateInCrossElection"));
				}
			} else {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notSearchCrossOpen"));
			}
		}
		// 查询跨院系互选名单中
		if (null != dto.getMode() && dto.getMode() == Constants.BK_CROSS) {
			elcMutualCrossStuVos = elcCrossStdsDao.isInElcMutualStdList(stuDto);
		} else {
			elcMutualCrossStuVos = elcMutualStdsDao.isInElcMutualStdList(stuDto);
		}
        //判断该学生是否在跨院系互选名单中
		if (elcMutualCrossStuVos == null || elcMutualCrossStuVos.isEmpty()) {
			if (null != dto.getMode() && dto.getMode() == Constants.BK_CROSS) {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notInCrossStuList"));
			} else {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notInMutualStuList"));
			}
		}
		//校验通过,查询学生本研申请列表
		List<String> projectIds =new ArrayList<>();
		if (null != dto.getMode() && dto.getMode() == Constants.BK_CROSS) {
			projectIds.add(Constants.PROJ_UNGRADUATE);
		} else {
			projectIds = ProjectUtil.getProjectIds(projectId);
		}

		dto.setProjectIds(projectIds);

		dto.setStudentId(studentId);
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			dto.setInType(Constants.FIRST);
		}else {
			dto.setByType(Constants.FIRST);
		}
		LOG.info("--------------projectIds--------------"+JSONArray.toJSONString(projectIds));
		//移动分页位置
		//由于对纯sql分页结果集做二次加减操作导致自动分页失效，故取消自动分页，采取手动分页。
//		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElcMutualApplyVo> list = elcMutualApplyDao.getElcMutualCoursesForStu(dto);
//		PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<ElcMutualApplyVo>(list);
		List<String> courseCode = list.stream()
				.filter(v->!v.getCourseCode().isEmpty()).map(ElcMutualApplyVo::getCourseCode)
				.collect(Collectors.toList());
		LOG.info("---------------可申请的课程代码--------------"+JSONArray.toJSONString(courseCode));

		// 本科生可申请的跨院系课程不在培养计划内
		if (StringUtils.equals(projectId,Constants.PROJ_UNGRADUATE)) {
			LOG.info("---------------dto.getMode()--------------"+dto.getMode());

//			List<ElcMutualApplyVo> list2 =pageInfo.getList();

			if(Constants.BK_CROSS.equals(dto.getMode())) {
				List<String> courseCodes = CultureSerivceInvokerToMutual.getCulturePlanCourseCodeByStudentId(studentId);
				LOG.info("---------------getCulturePlanCourseCodeByStudentId--------------"+courseCodes.size());
//				list2 = list2.stream().filter(vo->!courseCodes.contains(vo.getCourseCode())).collect(Collectors.toList());
				list = list.stream().filter(vo->!courseCodes.contains(vo.getCourseCode())).collect(Collectors.toList());
			}
//			pageInfo=new PageInfo<ElcMutualApplyVo>(list2);
//			pageInfo.setList(list2);

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
				List<CulturePlanVo> listPlanVos = new ArrayList<CulturePlanVo>();
				RestResult restResult = CultureSerivceInvokerToMutual.getCulturePlanByStudentId(studentId, 0);
				String json = JSONObject.toJSON(restResult.getData()).toString();
				Map<String, Object> parse = (Map)JSON.parse(json);
				for (String key : parse.keySet()) {
					if (StringUtils.equals(key, "culturePlanList")) {
						String value = parse.get(key).toString();
						listPlanVos = JSONArray.parseArray(value, CulturePlanVo.class);
					}
				}

				// 获取培养计划中的补修课courseCode
				List<String> courseCodeBXK = listPlanVos.stream()
						.filter(vo->vo.getLabelId().longValue()==labelId)
						.map(CulturePlanVo::getCourseCode)
						.collect(Collectors.toList());
				LOG.info("---------------courseCodeBXK:" + courseCodeBXK.toString() + "--------------");
//				List<ElcMutualApplyVo> list2 =pageInfo.getList();

				// 补修课与互选维护课程取交集
//				list2 = list2.stream().filter(vo->courseCodeBXK.contains(vo.getCourseCode())).collect(Collectors.toList());
//				pageInfo = new PageInfo<ElcMutualApplyVo>(list2);
//				pageInfo.setList(list2);
				list = list.stream().filter(vo->courseCodeBXK.contains(vo.getCourseCode())).collect(Collectors.toList());
			}else {
				list = new ArrayList<ElcMutualApplyVo>();
//				pageInfo = new PageInfo<ElcMutualApplyVo>(list);
//				pageInfo.setList(list);
			}
		}
		return list;
	}


    /**
     * 功能描述: 全量获取，不做分页
     *
     * @params: [condition]
     * @return: com.github.pagehelper.PageInfo<com.server.edu.mutual.vo.ElcMutualApplyVo>
     * @author: zhaoerhu
     * @date: 2020/2/24 22:18
     */
    public PageInfo<ElcMutualApplyVo> getElcMutualCoursesForStuNotLimit(PageCondition<ElcMutualApplyDto> condition){
        LOG.info("*******getElcMutualCoursesForStuNotLimit********");
        List<ElcMutualApplyVo> list = getElcMutualCoursesForStudent(condition);
        PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<ElcMutualApplyVo>(list);
        return pageInfo;
    }

	/**
	 * 功能描述: 对于数据来源不是单纯的分页sql时，对结果集合进行处理，手动分页设置pageInfo参数（取消自动分页）。
	 *
	 * @params: [list, pageInfo, pageNumParam, pageSizeParam]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/2/24 21:55
	 */
	private void pageInfoLimit(List list, PageInfo pageInfo, int pageNumParam, int pageSizeParam) {
		//手动设置PageInfo参数，进行手动分页
		int start = 0;
		int end = 0;
		int totalPages = 0;
		int totalRecord = 0;
		int pageSize = 0;
		int size = 0;
		int number = 0;
		size = pageSizeParam;
		number = pageNumParam;
		pageSize = pageSizeParam;
		totalRecord = list.size();
		pageInfo.setTotal(totalRecord);
		pageInfo.setPageSize(size);
		pageInfo.setPageNum(number);
		pageInfo.setSize(totalRecord);
		//计算获取对应的要显示的数据
		if (totalRecord % pageSize == 0) {
			totalPages = totalRecord / pageSize;
		} else {
			totalPages = totalRecord / pageSize + 1;
		}
		pageInfo.setPages(totalPages);
		//初始边界值计算
		if (number == 1) {
			start = 0;
			pageInfo.setHasPreviousPage(false);
			pageInfo.setPrePage(0);
			pageInfo.setIsFirstPage(true);
		} else {
			start = pageInfo.getPageSize() * (pageInfo.getPageNum() - 1);
			pageInfo.setHasPreviousPage(true);
			pageInfo.setPrePage(number - 1);
			pageInfo.setIsFirstPage(false);
		}
		pageInfo.setStartRow((number - 1) * pageSize);
		//结束边界值计算
		if ((start + pageInfo.getPageSize() > pageInfo.getTotal())) {
			end = totalRecord;
			pageInfo.setHasNextPage(false);
			pageInfo.setIsLastPage(true);
			pageInfo.setEndRow(totalRecord);
		} else {
			end = start + pageInfo.getPageSize();
			pageInfo.setHasNextPage(true);
			pageInfo.setNextPage(number + 1);
			pageInfo.setIsLastPage(false);
			pageInfo.setEndRow((number) * pageSize);
		}
		if (start < end && end <= totalRecord) {
			pageInfo.setList(list.subList(start, end));
		}
		if (pageInfo.getSize() == 0) {
			pageInfo.setStartRow(0);
			pageInfo.setEndRow(0);
		} else {
			pageInfo.setStartRow(pageInfo.getStartRow() + 1);
			pageInfo.setEndRow(pageInfo.getStartRow() - 1 + pageInfo.getSize());
		}
		pageInfo.setPages(totalPages);
		pageInfo.setNavigateLastPage(totalPages > number ? number + 1 : totalPages);
	}

}
