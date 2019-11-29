package com.server.edu.mutual.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.mutual.dao.ElcMutualCoursesDao;
import com.server.edu.mutual.dto.ElcMutualCoursesDto;
import com.server.edu.mutual.entity.ElcMutualCourses;
import com.server.edu.mutual.service.ElcMutualCoursesService;
import com.server.edu.mutual.vo.ElcMutualCoursesVo;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcMutualCoursesServiceImpl implements ElcMutualCoursesService {
	@Autowired
	private ElcMutualCoursesDao elcMutualCoursesDao;
	
	@Autowired
	private ElecRoundCourseDao elecRoundCourseDao;
	
	@Override
	public PageInfo<ElcMutualCoursesVo> getElcMutualCourseList(PageCondition<ElcMutualCoursesDto> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		ElcMutualCoursesDto dto = condition.getCondition();
		Session session = SessionUtils.getCurrentSession();
		dto.setProjectId(session.getCurrentManageDptId());
		if(Constants.BK_CROSS.equals(dto.getMode())) {
			dto.setInType(Constants.FIRST);
		}else {
			dto.setByType(Constants.FIRST);
		}
		List<ElcMutualCoursesVo> list = elcMutualCoursesDao.getElcMutualCourseList(condition.getCondition());
		PageInfo<ElcMutualCoursesVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}

	@Transactional
	@Override
	public int add(Long calendarId, String courseList, Integer mode) {
		String courseListStr = "";
		if (StringUtils.isNotBlank(courseList)) {
			courseListStr = courseList.replaceAll("，", ",");
		}
		List<String> courseCodes = Arrays.asList(courseListStr.split(","));
		
		Session session = SessionUtils.getCurrentSession();
		String projectId = session.getCurrentManageDptId();
		
		// 校验用户添加的课程是否是本科生（或普通研究生、在职研究生）当前学年学期已开课的所有课程
		List<CourseOpenDto> courses = getOpenedCourses(calendarId,projectId);
		Set<String> courseCodeSet = courses.stream().map(CourseOpenDto->CourseOpenDto.getCourseCode()).collect(Collectors.toSet());
		if(CollectionUtil.isEmpty(courseCodeSet)|| !courseCodeSet.containsAll(courseCodes)) {
			throw new ParameterValidateException(I18nUtil.getMsg("elcMutualCourses.addCourseError")); 
		}
		
		List<CourseOpenDto> addCourses = new ArrayList<CourseOpenDto>();
		for (CourseOpenDto courseOpenDto : courses) {
			for (String courseCode : courseCodes) {
				if (StringUtils.equals(courseOpenDto.getCourseCode(), courseCode)) {
					addCourses.add(courseOpenDto);
				}
			}
		}
		
		//保存
		int result = sveElcMutualCourses(calendarId, mode, addCourses);
		return result;
	}

	private int sveElcMutualCourses(Long calendarId, Integer mode, List<CourseOpenDto> courses) {
		Set<Long> courseIds = courses.stream().map(CourseOpenDto::getId).collect(Collectors.toSet());
		Example example = new Example(ElcMutualCourses.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", calendarId);
		if(Constants.BK_CROSS.equals(mode)) {
			criteria.andEqualTo("inType", Constants.FIRST);
		}else {
			criteria.andEqualTo("byType", Constants.FIRST);
		}
		criteria.andIn("courseId", courseIds);
		List<ElcMutualCourses> elcMutualCourses = elcMutualCoursesDao.selectByExample(example);
		if(CollectionUtil.isNotEmpty(elcMutualCourses)) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg("election.elcMutualCourses"))); 
		}
		List<ElcMutualCourses> mutualCourses = new ArrayList<ElcMutualCourses>();
		for(Long courseId:courseIds) {
			ElcMutualCourses temp = new ElcMutualCourses();
			temp.setCalendarId(calendarId);
			if(Constants.BK_CROSS.equals(mode)) {
				temp.setInType(Constants.FIRST);
			}else {
				temp.setByType(Constants.FIRST);
			}
			temp.setCourseId(courseId);
			mutualCourses.add(temp);
		}
		int result = elcMutualCoursesDao.insertList(mutualCourses);
		return result;
	}

	@Transactional
	@Override
	public int batchAdd(Long calendarId, String college, Integer mode) {
		Session session = SessionUtils.getCurrentSession();
		List<CourseOpenDto> courses = getOpenedCourses(calendarId,session.getCurrentManageDptId());

		if(CollectionUtil.isEmpty(courses)) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",I18nUtil.getMsg("elcMutualCourses.course"))); 
		}
		
		if (StringUtils.isNotBlank(college)) {
			courses = courses.stream().filter(CourseOpenDto->StringUtils.equals(CourseOpenDto.getFaculty(), college)).collect(Collectors.toList());
		}

		//保存数据
		int result = sveElcMutualCourses(calendarId, mode, courses);
		return result;
	}
	
	@Transactional
	@Override
	public int addAll(Long calendarId, Integer mode) {
		int result =batchAdd(calendarId,"",mode);
		return result;
	}

	@Transactional
	@Override
	public int delete(List<Long> ids) {
		Example example = new Example(ElcMutualCourses.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		int result = elcMutualCoursesDao.deleteByExample(example);
		return result;
	}

	@Transactional
	@Override
	public int deleteAll(Long calendarId, Integer mode) {
		Session session = SessionUtils.getCurrentSession();
		String projecetId = session.getCurrentManageDptId();
		// 获取全部排课课程ID
		List<CourseOpenDto> courses = getOpenedCourses(calendarId,projecetId);
		Set<Long> courseIds = courses.stream().map(CourseOpenDto::getId).collect(Collectors.toSet());
		
		Example example = new Example(ElcMutualCourses.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("calendarId", calendarId);
		criteria.andIn("courseId", courseIds);
		if(Constants.BK_CROSS.equals(mode)) {
			criteria.andEqualTo("inType", Constants.FIRST);
		}else {
			criteria.andEqualTo("byType", Constants.FIRST);
		}
		int result = elcMutualCoursesDao.deleteByExample(example);
		return result;
	}
	
	@Override
	public int getElcMutualCourseCount(Long calendarId,Integer mode) {
		ElcMutualCoursesDto dto = new ElcMutualCoursesDto();
		Session session = SessionUtils.getCurrentSession();
		dto.setProjectId(session.getCurrentManageDptId());
		dto.setCalendarId(calendarId);
		if(Constants.BK_CROSS.equals(mode)) {
			dto.setInType(Constants.FIRST);
		}else {
			dto.setByType(Constants.FIRST);
		}
		int count = elcMutualCoursesDao.getElcMutualCourseCount(dto);
		return count;
	}
	
	/**
	 * 获取本科生（或者研究生）已经排课的课程
	 * @param calendarId 学年学期
	 * @param projectId 用户管理部门ID
	 * @return List<CourseOpenDto>
	 */
	private List<CourseOpenDto> getOpenedCourses(Long calendarId, String projectId){
//		List<CourseOpenDto> courses = new ArrayList<CourseOpenDto>();
//		if (StringUtils.equals(projectId, Constants.PROJ_UNGRADUATE)) {
//			courses = elecRoundCourseDao.selectCourseByCalendarId(calendarId);
//		}else {
//			courses = elecRoundCourseDao.selectCorseGraduteByCalendarId(calendarId, projectId);
//		}
		
		List<CourseOpenDto> courses = elecRoundCourseDao.selectCourseByCalendarIdForMutual(calendarId,projectId);
		return courses;
	}

}
