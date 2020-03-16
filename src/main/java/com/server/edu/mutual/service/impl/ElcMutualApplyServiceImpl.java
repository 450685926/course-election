package com.server.edu.mutual.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.server.edu.election.util.CommonConstant;
import com.server.edu.mutual.dao.ElcCrossStdsDao;
import com.server.edu.mutual.entity.ElcMutualApplyTurns;
import com.server.edu.mutual.vo.CulturePlanVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.entity.LabelCreditCount;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.RestResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.Enum.MutualApplyAuditStatus;
import com.server.edu.mutual.controller.ElcMutualApplyController;
import com.server.edu.mutual.dao.ElcMutualApplyDao;
import com.server.edu.mutual.dao.ElcMutualApplySwitchDao;
import com.server.edu.mutual.dao.ElcMutualListDao;
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
	private ElcCrossStdsDao elcCrossStdsDao;

	@Autowired
	private ElcMutualApplySwitchDao elcMutualApplySwitchDao;
	
	@Autowired
	private ElcMutualListDao elcMutualListDao;
	
	@Autowired
    private ElcCourseTakeDao elcCourseTakeDao;
	
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
			//复制一份数据，做处理
			List<ElcMutualApplyVo> copyList = new ArrayList<ElcMutualApplyVo>();
			copyList.addAll(coursesForStuList);

			if (CollectionUtil.isNotEmpty(coursesForStuList)) {
				//如果是研究生在审核俩表中有数据，排除培养中相同的数据
				if(Constants.SECOND.equals(dto.getMode())){
					List<Long> indexList = new ArrayList<>();
					for (int i = 0; i < list.size(); i++) {
						ElcMutualApplyVo listApply = list.get(i);
						for(int j = 0; j< coursesForStuList.size();j++){
							ElcMutualApplyVo courseApply = coursesForStuList.get(j);
							if(listApply.getCourseCode().equals(courseApply.getCourseCode())
									&& String.valueOf(listApply.getMutualCourseId()).equals(courseApply.getCourseId())
									&& listApply.getOpenCollege().equals(courseApply.getOpenCollege())){
								//将相同数据的主键放到list中
								indexList.add(courseApply.getId());
							}
						}
					}

					//去除相同的数据
					for (ElcMutualApplyVo elcVo : coursesForStuList) {
						if(indexList.contains(elcVo.getId())){
							copyList.remove(elcVo);
						}
					}
				}
				list.addAll(copyList);
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
		//String projectId = session.getCurrentManageDptId();
		//部门id,前端传入
		String projectId=dto.getProjectId();
		String studentId = session.realUid();

		int result = 0;
		// 校验学生是否可以申请互选课程
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
			//初始化帮助类
			MutualApplyJugeUtil mutualApplyJugeUtil = new MutualApplyJugeUtil(elcMutualApplySwitchDao,elcMutualApplyDao);
			//执行校验方法
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

			//如果主键不为空赋值主键
			if(dto.getIdSign() !=0){
				elcMutualApply.setId(Long.parseLong(String.valueOf(dto.getIdSign())));
				//利用通用mapper做删除
				elcMutualApplyDao.deleteByPrimaryKey(Long.parseLong(String.valueOf(dto.getIdSign())));
			}
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
		//获取相关参数
		ElcMutualApplyDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();
//		String projectId = "1";

		//校验选课时间开关
		elcMutualTimeCheck(dto, projectId);
		//校验跨学科或者本研互选名单
		studentNameListCheck(dto);
		//校验通过,查询学生本研/跨学科申请列表
		List<ElcMutualApplyVo> list = selectElcMutualApplyList(dto);
		//日志打印
		logCourseCodeFilter(list);
		//测试日志
		LOG.info("-----------dto.isBatch()---------:" + dto.isBatch());
		LOG.info("-----------dto---------:" + dto.toString());
		if(dto.isBatch()){
			//批量分支走批量逻辑  待开发
			list = listResultFilterByCultureBatch(dto, list, projectId);
		}else{
			//单个分支走单个逻辑
			list = listResultFilterByCulture(dto, list, projectId);
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

	/**
	 * 功能描述: 接口切换标志
	 * 备注：当前服务接口前端调用地方很多，根据前端是否上送 studentIdList 决定执行不同的业务逻辑。
	 *
	 * @params: [dto, stuDto]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/3/12 15:31
	 */
	private void dtoPotting(ElcMutualApplyDto dto,ElcMutualCrossStuDto stuDto){
		LOG.info("dtoPotting begin...");
        //增加批量学生 11685
        List<String> studentIdList = dto.getStudentIdList();
        if(studentIdList != null  && studentIdList.size() > 0){
            //studentIdList列表是标志，如果前端上送了studentIdList，则原来的studentId被设置为null
			dto.setStudentId(null);
			dto.setBatch(Boolean.TRUE);
			stuDto.setBatch(Boolean.TRUE);
            //增加批量学生 11685
            stuDto.setStudentIdList(studentIdList);
        }else{
			dto.setBatch(Boolean.FALSE);
			stuDto.setBatch(Boolean.FALSE);
		}
		stuDto.setCalendarId(dto.getCalendarId());
		stuDto.setStudentId(dto.getStudentId());

		LOG.info("studentIdList:" + dto.getStudentIdList());
		LOG.info("studentId:" + dto.getStudentId());
		LOG.info("dtoPotting end...");
    }


    /**
     * 功能描述: 查询选课开关，根据条件校验选课时间设置
     *
     * @params: [dto, projectId]
     * @return: void
     * @author: zhaoerhu
     * @date: 2020/3/12 15:17
     */
	private void elcMutualTimeCheck(ElcMutualApplyDto dto, String projectId) {
		boolean checkFlag = !CommonConstant.isEmptyStr(dto.getCourseSelectionMark()) && "true".equalsIgnoreCase(dto.getCourseSelectionMark());
		LOG.info("-------------------it is check add course election flag:{}", checkFlag);
		if (checkFlag) {
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
	}


	/**
	 * 功能描述: 学生名单校验
	 *
	 * 根据参数，分别校验当前学生是否存在于跨学科或者本研互选的学生名单中
	 *
	 * @params: [dto]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/3/12 15:24
	 */
	private void studentNameListCheck(ElcMutualApplyDto dto) {
		//是否是跨学科
		Boolean isBkCross = isBkCross(dto);
		LOG.info("... studentNameListCheck ...:" + isBkCross);
		//封装参数
		ElcMutualCrossStuDto stuDto = new ElcMutualCrossStuDto();
		dtoPotting(dto, stuDto);

		//返回单个po对象在切换学期时代码报错（切换未上送学生id所以返回多条记录），故统一使用list接收
		List<ElcMutualCrossStuVo> elcMutualCrossStuVos = null;
		if (isBkCross) {
			// 判断该学生是否在跨学科名单中
			elcMutualCrossStuVos = elcCrossStdsDao.isInElcMutualStdList(stuDto);
		} else {
			// 判断该学生是否在本研互选名单中
			elcMutualCrossStuVos = elcMutualStdsDao.isInElcMutualStdList(stuDto);
		}

		//名单校验
		if (elcMutualCrossStuVos == null || elcMutualCrossStuVos.isEmpty()) {
			if (isBkCross) {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notInCrossStuList"));
			} else {
				throw new ParameterValidateException(I18nUtil.getMsg("elcMutualStu.notInMutualStuList"));
			}
		}
	}

	/**
	 * 功能描述: 根据前端参数判断是否是跨学科
	 *
	 * @params: [dto]
	 * @return: boolean
	 * @author: zhaoerhu
	 * @date: 2020/3/12 15:43
	 */
	private boolean isBkCross(ElcMutualApplyDto dto) {
		//是否跨学科
		return (null != dto.getMode() && dto.getMode() == Constants.BK_CROSS);
	}

	/**
	 * 功能描述: 查询当前学生可申请的本研互选或者跨学科课程列表
	 *
	 * @params: [dto]
	 * @return: java.util.List<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/3/12 15:46
	 */
	private List<ElcMutualApplyVo> selectElcMutualApplyList(ElcMutualApplyDto dto){
		Boolean isBkCross = isBkCross(dto);
		LOG.info("... selectElcMutualApplyList ...:" + isBkCross);
		List<String> projectIds =new ArrayList<>();
		if (isBkCross) {
			projectIds.add(Constants.PROJ_UNGRADUATE);
			dto.setInType(Constants.FIRST);
		} else {
			projectIds = ProjectUtil.getProjectIds(dto.getProjectId());
			dto.setByType(Constants.FIRST);
		}
		dto.setProjectIds(projectIds);
		LOG.info("--------------projectIds--------------"+JSONArray.toJSONString(projectIds));
		//移动分页位置
		//由于对纯sql分页结果集做二次加减操作导致自动分页失效，故取消自动分页，采取手动分页。
		//PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		//本研互选课程列表数据查询
		List<ElcMutualApplyVo> list = elcMutualApplyDao.getElcMutualCoursesForStu(dto);
		//PageInfo<ElcMutualApplyVo> pageInfo = new PageInfo<ElcMutualApplyVo>(list);
		return list;
	}

	/**
	 * 功能描述: 过滤结果列表中课程code不为空的课程集合
	 *
	 * @params: [list]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/3/12 16:16
	 */
	private void logCourseCodeFilter(List<ElcMutualApplyVo> list){
		List<String> courseCode = list.stream()
				.filter(v->!v.getCourseCode().isEmpty()).map(ElcMutualApplyVo::getCourseCode)
				.collect(Collectors.toList());
		LOG.info("---------------可申请的课程代码--------------"+JSONArray.toJSONString(courseCode));
	}


	/**
	 * 功能描述: 根据培养rpc相关接口过滤结果集(单个分支)
	 *
	 * @params: [dto, list, projectId]
	 * @return: void
	 * @author: zhaoerhu
	 * @date: 2020/3/12 16:24
	 */
	private List<ElcMutualApplyVo> listResultFilterByCulture(ElcMutualApplyDto dto, List<ElcMutualApplyVo> list, String projectId) {
		String studentId = dto.getStudentId();
		LOG.info("... listResultFilterByCulture  ...:" + studentId);
		// 本科生可申请的跨院系课程不在培养计划内
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
			LOG.info("---------------dto.getMode()--------------" + dto.getMode());
			if (isBkCross(dto)) {
				list = bkCrossFilterByCulture(list, studentId);
			}
		} else {
			list = graduateFilterByCulture(list,studentId);
		}
		return list;
	}


	/**
	 * 功能描述: 根据培养rpc结果过滤跨学科结果集(单个分支)
	 *
	 * @params: [list, studentId]
	 * @return: java.util.List<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/3/12 17:05
	 */
	private List<ElcMutualApplyVo> bkCrossFilterByCulture(List<ElcMutualApplyVo> list, String studentId) {
		List<String> courseCodes = CultureSerivceInvokerToMutual.getCulturePlanCourseCodeByStudentId(studentId);
		LOG.info("---------------bkCrossFilterByCulture--------------:" + courseCodes.size());
		//过滤培养计划中的课程
		list = list.stream().filter(vo -> !courseCodes.contains(vo.getCourseCode())).collect(Collectors.toList());

		List<Long> ids = CultureSerivceInvokerToMutual.getStudentCultureScheme(studentId);
		list = listFilter(list, ids);
		Example example = new Example(ElcCourseTake.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("studentId", studentId);
		List<ElcCourseTake> elcs = elcCourseTakeDao.selectByExample(example);
		List<String> courseCodes2 = elcs.stream().map(vo -> vo.getCourseCode()).distinct().collect(Collectors.toList());
		//过滤已选课程中的课程
		list = list.stream().filter(vo -> !courseCodes2.contains(vo.getCourseCode())).collect(Collectors.toList());

		List<String> courseCodes3 = elcMutualApplyDao.getStuCourseCodesFromScore(studentId);
		//过滤已有成绩的课程
		list = list.stream().filter(vo -> !courseCodes3.contains(vo.getCourseCode())).collect(Collectors.toList());
		return list;
	}


	/**
	 * 功能描述: 根据培养rpc结果过滤研究生结果集(单个分支)
	 *
	 * @params: [list, studentId]
	 * @return: java.util.List<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/3/12 17:08
	 */
	private List<ElcMutualApplyVo> graduateFilterByCulture(List<ElcMutualApplyVo> list, String studentId) {
		LOG.info("... graduateFilterByCulture ...");
		// 研究生可申请的互选课程为: 研究生培养计划中“补修课”与本科生管理员维护的互选课程取交集
		List<LabelCreditCount> planBXK = new ArrayList<LabelCreditCount>();
		List<LabelCreditCount> planCount = CultureSerivceInvokerToMutual.studentPlanCountByStuId(studentId);
		if (CollectionUtil.isNotEmpty(planCount)) {
			planBXK = planCount.stream().filter(vo -> StringUtils.equals(vo.getLabelName(), "补修课")).collect(Collectors.toList());
		}

		if (CollectionUtil.isNotEmpty(planBXK)) {
			LabelCreditCount labelCreditCount = planBXK.get(0);
			long labelId = labelCreditCount.getLabelId().longValue();

			// 获取培养计划中的课程列表
			List<CulturePlanVo> listPlanVos = new ArrayList<CulturePlanVo>();
			RestResult restResult = CultureSerivceInvokerToMutual.getCulturePlanByStudentId(studentId, 0);
			String json = JSONObject.toJSON(restResult.getData()).toString();
			Map<String, Object> parse = (Map) JSON.parse(json);
			for (String key : parse.keySet()) {
				if (StringUtils.equals(key, "culturePlanList")) {
					String value = parse.get(key).toString();
					listPlanVos = JSONArray.parseArray(value, CulturePlanVo.class);
				}
			}

			// 获取培养计划中的补修课courseCode
			List<String> courseCodeBXK = listPlanVos.stream()
					.filter(vo -> vo.getLabelId().longValue() == labelId)
					.map(CulturePlanVo::getCourseCode)
					.collect(Collectors.toList());
			LOG.info("---------------courseCodeBXK:" + courseCodeBXK.toString() + "--------------");
			list = list.stream().filter(vo -> courseCodeBXK.contains(vo.getCourseCode())).collect(Collectors.toList());
		} else {
			list = new ArrayList<ElcMutualApplyVo>();
		}
		return list;

	}


	/**
	 * 功能描述: 根据培养rpc相关接口过滤结果集(批量分支)
	 *
	 * @params: [dto, list, projectId]
	 * @return: java.util.List<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/3/13 9:51
	 */
	private List<ElcMutualApplyVo> listResultFilterByCultureBatch(ElcMutualApplyDto dto, List<ElcMutualApplyVo> list, String projectId) {
		List<String> studentIdList = dto.getStudentIdList();
		LOG.info("... listResultFilterByCultureBatch  ...:" + studentIdList);
		// 本科生可申请的跨院系课程不在培养计划内
		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
			LOG.info("---------------dto.getMode()--------------" + dto.getMode());
			if (isBkCross(dto)) {
				list = bkCrossFilterByCultureBatch(list, studentIdList);
			}
		} else {
			//注意本研互选分支不改造为批量，所以该case不会使用
//			list = graduateFilterByCultureBatch(list, studentIdList);
		}
		return list;
	}


	/**
	 * 功能描述: 根据培养rpc结果过滤跨学科结果集(批量分支)
	 *
	 * @params: [list, studentId]
	 * @return: java.util.List<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/3/12 17:05
	 */
	private List<ElcMutualApplyVo> bkCrossFilterByCultureBatch(List<ElcMutualApplyVo> list, List<String> studentIdList) {
	    String studentIds = list2String(studentIdList);
		List<String> courseCodes = CultureSerivceInvokerToMutual.getCulturePlanCourseCodeByStudentIdForJD(studentIds);
		LOG.info("---------------bkCrossFilterByCultureBatch--------------:" + courseCodes.size());
		//过滤培养计划中的课程
		list = list.stream().filter(vo -> !courseCodes.contains(vo.getCourseCode())).collect(Collectors.toList());

		List<Long> ids = CultureSerivceInvokerToMutual.getStudentCultureSchemeForJD(studentIds);
		list = listFilter(list, ids);
		Example example = new Example(ElcCourseTake.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("studentId", studentIdList);
		List<ElcCourseTake> elcs = elcCourseTakeDao.selectByExample(example);
		List<String> courseCodes2 = elcs.stream().map(vo -> vo.getCourseCode()).distinct().collect(Collectors.toList());
		//过滤已选课程中的课程
		list = list.stream().filter(vo -> !courseCodes2.contains(vo.getCourseCode())).collect(Collectors.toList());

		List<String> courseCodes3 = elcMutualApplyDao.getStuCourseCodesFromScoreForJD(studentIdList);
		//过滤已有成绩的课程
		list = list.stream().filter(vo -> !courseCodes3.contains(vo.getCourseCode())).collect(Collectors.toList());
		return list;
	}


	/**
	 * 功能描述: 根据培养rpc结果过滤研究生结果集(批量分支)
	 *
	 * @params: [list, studentId]
	 * @return: java.util.List<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/3/12 17:08
	 */
	private List<ElcMutualApplyVo> graduateFilterByCultureBatch(List<ElcMutualApplyVo> list, List<String> studentIdList) {
		LOG.info("... graduateFilterByCultureBatch ...");

		// 研究生可申请的互选课程为: 研究生培养计划中“补修课”与本科生管理员维护的互选课程取交集
		List<LabelCreditCount> planBXK = new ArrayList<LabelCreditCount>();
		List<LabelCreditCount> planCount = null;
		if (CollectionUtil.isNotEmpty(planCount)) {
			planBXK = planCount.stream().filter(vo -> StringUtils.equals(vo.getLabelName(), "补修课")).collect(Collectors.toList());
		}

		if (CollectionUtil.isNotEmpty(planBXK)) {
			LabelCreditCount labelCreditCount = planBXK.get(0);
			long labelId = labelCreditCount.getLabelId().longValue();

			// 获取培养计划中的课程列表
			List<CulturePlanVo> listPlanVos = new ArrayList<CulturePlanVo>();
			RestResult restResult = null;
			String json = JSONObject.toJSON(restResult.getData()).toString();
			Map<String, Object> parse = (Map) JSON.parse(json);
			for (String key : parse.keySet()) {
				if (StringUtils.equals(key, "culturePlanList")) {
					String value = parse.get(key).toString();
					listPlanVos = JSONArray.parseArray(value, CulturePlanVo.class);
				}
			}

			// 获取培养计划中的补修课courseCode
			List<String> courseCodeBXK = listPlanVos.stream()
					.filter(vo -> vo.getLabelId().longValue() == labelId)
					.map(CulturePlanVo::getCourseCode)
					.collect(Collectors.toList());
			LOG.info("---------------courseCodeBXK:" + courseCodeBXK.toString() + "--------------");
			list = list.stream().filter(vo -> courseCodeBXK.contains(vo.getCourseCode())).collect(Collectors.toList());
		} else {
			list = new ArrayList<ElcMutualApplyVo>();
		}
		return list;

	}


	/**
	 * 功能描述: 根据ids过滤list
	 *
	 * @params: [list, ids]
	 * @return: java.util.List<com.server.edu.mutual.vo.ElcMutualApplyVo>
	 * @author: zhaoerhu
	 * @date: 2020/3/13 9:57
	 */
	private List<ElcMutualApplyVo> listFilter(List<ElcMutualApplyVo> list, List<Long> ids) {
		if (!CollectionUtils.isEmpty(ids)) {
			List<String> courseCodes1 = new ArrayList<>();
			for (Long id : ids) {
				List<String> courses = CultureSerivceInvokerToMutual.getStudentCultureSchemeCourseCode(id);
				if (!CollectionUtils.isEmpty(courses)) {
					courseCodes1.addAll(courses);
				}
			}
			//过滤培养方案中的课程
			list = list.stream().filter(vo -> !courseCodes1.contains(vo.getCourseCode())).collect(Collectors.toList());
		}
		return list;
	}

	/**
	 * 功能描述: list列表转换为以逗号分隔的大字符串
	 *
	 * @params: [studentIdList]
	 * @return: java.lang.String
	 * @author: zhaoerhu
	 * @date: 2020/3/13 18:03
	 */
	private String list2String(List<String> studentIdList){
        StringBuilder stringBuilder = new StringBuilder();
        for(String studentId : studentIdList){
            stringBuilder.append(studentId);
            stringBuilder.append(",");
        }
        String str = stringBuilder.substring(0, stringBuilder.length() - 1);
        return str;
    }
}
