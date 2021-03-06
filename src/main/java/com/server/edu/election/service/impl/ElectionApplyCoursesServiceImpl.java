package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElectionApplyCoursesDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dto.CourseDto;
import com.server.edu.election.dto.ElectionApplyCoursesDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElectionApplyCourses;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElectionApplyCoursesService;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.vo.ElectionApplyCoursesVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElectionApplyCoursesServiceImpl implements ElectionApplyCoursesService {
	@Autowired
	private CourseDao courseDao;
	@Autowired
	private  ElectionApplyCoursesDao electionApplyCoursesDao;
	
	@Autowired
	private ElectionConstantsDao constantsDao;
	
	@Override
	public PageInfo<ElectionApplyCoursesVo> applyCourseList(PageCondition<ElectionApplyCoursesDto> condition){
		ElectionApplyCoursesDto dto = condition.getCondition();
		Integer mode = dto.getMode();
		//添加课程是处理好体育和英语课，这里不需要特殊判断

//		if (Constants.ENGLISH_MODEL.equals(mode)) {
//			dto.setCollege("000268");
//		} else if (Constants.PE_MODEL.equals(mode)) {
//			dto.setCollege("000293");
//		}
		//模式1 查所有
		if(Constants.NORMAL_MODEL.equals(mode)){
			dto.setMode(null);
		}
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<ElectionApplyCoursesVo> list = electionApplyCoursesDao.selectApplyCourse(dto);
		PageInfo<ElectionApplyCoursesVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public PageInfo<Course> courseList(PageCondition<CourseDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		CourseDto dto = condition.getCondition();
		Integer model = dto.getMode();
		List<Course> list = new ArrayList<>();
		Example example = new Example(Course.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("status", Constants.THREE);
		if(StringUtils.isNotBlank(dto.getKeyword())) {
		    example.and().andLike("code", dto.getKeyword()+"%").orLike("name", dto.getKeyword()+"%");
		}
		
		if (Constants.ENGLISH_MODEL.equals(model)) {
			String englishCourses = constantsDao.findEnglishCourses();
			if(StringUtils.isBlank(englishCourses)) {
			    return new PageInfo<>();
			}
			String[] split = englishCourses.split(",");
			criteria.andIn("code", Arrays.asList(split));
		}else if (Constants.PE_MODEL.equals(model)){
			String PECourses = constantsDao.findPECourses();
			if(StringUtils.isBlank(PECourses)) {
                return new PageInfo<>();
            }
			String[] split = PECourses.split(",");
            criteria.andIn("code", Arrays.asList(split));
		}else{
			//查找申请列表中已经存在的课程，并排除掉
			ElectionApplyCoursesDto electionApplyCoursesDto = new ElectionApplyCoursesDto();
			electionApplyCoursesDto.setCalendarId(dto.getCalendarId());
			List<ElectionApplyCoursesVo> selectApplyCourse = electionApplyCoursesDao.selectApplyCourse(electionApplyCoursesDto);
			List<String> code = selectApplyCourse.stream().map(ElectionApplyCoursesVo::getCode).collect(Collectors.toList());
			criteria.andNotIn("code",code);
		}
		list = courseDao.selectByExample(example);
		PageInfo<Course> pageInfo = new PageInfo<>(list);
		return pageInfo;
		
	}

	@Override
	public PageResult<Course> courseList1(PageCondition<CourseDto> condition) {
		CourseDto dto = condition.getCondition();
		Integer model = dto.getMode();
		dto.setStatus(Constants.THREE+"");
		dto.setKeyword(dto.getKeyword());
		Page<Course> page = null;
		if (Constants.ENGLISH_MODEL.equals(model)) {
//			String englishCourses = constantsDao.findEnglishCourses();
//			if(StringUtils.isBlank(englishCourses)) {
//			    return new PageResult<>();
//			}
//			String[] split = englishCourses.split(",");
//			course.setCodes( Arrays.asList(split));
//			PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
//			page = electionApplyCoursesDao.getApplyCourse2Add(course);
			dto.setFaculty("000268");
			PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
			page = electionApplyCoursesDao.getApplyCourse3Add(dto);
		}else if (Constants.PE_MODEL.equals(model)){
//			String PECourses = constantsDao.findPECourses();
//			if(StringUtils.isBlank(PECourses)) {
//                return new PageResult<>();
//            }
//			String[] split = PECourses.split(",");
//			course.setCodes( Arrays.asList(split));
			dto.setFaculty("000293");
			PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
			page = electionApplyCoursesDao.getApplyCourse3Add(dto);
		}else{
			//查找申请列表中已经存在的课程，并排除掉
//			ElectionApplyCoursesDto electionApplyCoursesDto = new ElectionApplyCoursesDto();
//			electionApplyCoursesDto.setCalendarId(dto.getCalendarId());
//			List<ElectionApplyCoursesVo> selectApplyCourse = electionApplyCoursesDao.selectApplyCourse(electionApplyCoursesDto);
//			List<String> code = selectApplyCourse.stream().map(ElectionApplyCoursesVo::getCode).collect(Collectors.toList());
//			course.setCodes(code);
			PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
			page = electionApplyCoursesDao.getApplyCourse3Add(dto);
		}
		PageResult<Course> pageInfo = new PageResult<>(page);
		return pageInfo;
	}
	
	@Override
	@Transactional
	public int addCourses(ElectionApplyCoursesDto dto) {
		Example courseExample = new Example(Course.class);
		Example.Criteria courdseCriteria = courseExample.createCriteria();
		List<String> courseCodes = dto.getCourses();
		courdseCriteria.andIn("code",courseCodes);
		List<Course> courses = courseDao.selectByExample(courseExample);
		if(CollectionUtil.isEmpty(courses)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		//分别过滤英语课和体育课 （英语课mode 3  体育课 2 ）
		List<String> enlishCourse = courses.stream().filter(vo -> vo.getCollege().equals("000268")).map(Course::getCode).collect(Collectors.toList());
		List<String> peCourse = courses.stream().filter(vo -> vo.getCollege().equals("000293")).map(Course::getCode).collect(Collectors.toList());

		List<ElectionApplyCourses> list = new ArrayList<>();
//		List<String> courseCodes = new ArrayList<>();
		Long calendarId = dto.getCalendarId();
		Integer mode = dto.getMode();
		for(String course: courseCodes) {
			ElectionApplyCourses electionApplyCourses = new ElectionApplyCourses();
			if(Constants.NORMAL_MODEL.equals(mode)){
				if(CollectionUtil.isNotEmpty(enlishCourse) && enlishCourse.contains(course)){
					electionApplyCourses.setMode(Constants.ENGLISH_MODEL);
				}else if(CollectionUtil.isNotEmpty(peCourse) && peCourse.contains(course)){
					electionApplyCourses.setMode(Constants.PE_MODEL);
				}else{
					electionApplyCourses.setMode(mode);
				}
			}else{
				electionApplyCourses.setMode(mode);
			}
			electionApplyCourses.setCourseCode(course);
			electionApplyCourses.setCalendarId(calendarId);
            list.add(electionApplyCourses);
//            courseCodes.add(course);
        }
		Example example = new Example(ElectionApplyCourses.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("courseCode",courseCodes);
		criteria.andEqualTo("calendarId", calendarId);
		//criteria.andEqualTo("mode", mode);
		List<ElectionApplyCourses> electionApplyCourses = electionApplyCoursesDao.selectByExample(example);
		if (CollectionUtil.isNotEmpty(electionApplyCourses)) {
			Set<String> collect = electionApplyCourses.stream().map(ElectionApplyCourses::getCourseCode).collect(Collectors.toSet());
			throw new ParameterValidateException(I18nUtil.getMsg("common.exist",I18nUtil.getMsg(String.join(",", collect))));
		}
		int result = electionApplyCoursesDao.insertList(list);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("electionApply.electionApplyCourses")));
		}
		
		setToCache(calendarId);
		return result;
	}

	public void setToCache(Long calendarId) {
		Example applyExample = new Example(ElectionApplyCourses.class);
		Example.Criteria applyCriteria = applyExample.createCriteria();
		applyCriteria.andEqualTo("calendarId", calendarId);
		List<ElectionApplyCourses> electionApplyCourseList = electionApplyCoursesDao.selectByExample(applyExample);
		Set<String> applyCourses = new HashSet<>();
		if(CollectionUtil.isNotEmpty(electionApplyCourseList)) {
			applyCourses = electionApplyCourseList.stream()
					.map(ElectionApplyCourses::getCourseCode)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());
		}
		//存入redis
		ElecContextUtil.setApplyCourse(calendarId, applyCourses);
	}
	
	@Override
	@Transactional
	public int deleteCourses(List<Long> ids) {
		Example example = new Example(ElectionApplyCourses.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id",ids);
		List<ElectionApplyCourses> list = electionApplyCoursesDao.selectByExample(example);
		if(CollectionUtil.isEmpty(list)) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		Long calendarId= list.get(0).getCalendarId();
		int result = electionApplyCoursesDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("electionApply.electionApplyCourses")));
		}
		setToCache(calendarId);
		return result;
	}

}
