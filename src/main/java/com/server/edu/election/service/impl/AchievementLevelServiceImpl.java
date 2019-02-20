package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.AchievementCourseDao;
import com.server.edu.election.dao.AchievementLevelDao;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dto.AchievementLevelDto;
import com.server.edu.election.entity.AchievementCourse;
import com.server.edu.election.entity.AchievementLevel;
import com.server.edu.election.entity.Course;
import com.server.edu.election.service.AchievementLevelService;
import com.server.edu.election.vo.AchievementLevelVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class AchievementLevelServiceImpl implements AchievementLevelService {
	@Autowired
	private AchievementLevelDao achievementLevelDao;
	@Autowired
	private AchievementCourseDao achievementCourseDao;
	@Autowired
	private CourseDao courseDao;
	@Override
	public PageInfo<AchievementLevelVo> list(PageCondition<AchievementLevelDto> condition){
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		AchievementLevelDto achievementLevelDto = new AchievementLevelDto();
		Example example = new Example(AchievementLevel.class);
		Example.Criteria criteria = example.createCriteria();
		if(achievementLevelDto.getGrade()!=null) {
			criteria.andEqualTo("grade",achievementLevelDto.getGrade());
		}
		if(StringUtils.isNotBlank(achievementLevelDto.getTrainingLevel())) {
			criteria.andEqualTo("trainingLevel",achievementLevelDto.getTrainingLevel());
		}
		if(StringUtils.isNotBlank(achievementLevelDto.getFormLearning())) {
			criteria.andEqualTo("formLearning",achievementLevelDto.getFormLearning());
		}
		if(StringUtils.isNotBlank(achievementLevelDto.getFaculty())) {
			criteria.andEqualTo("faculty",achievementLevelDto.getFaculty());
		}
		if(StringUtils.isNotBlank(achievementLevelDto.getProfession())) {
			criteria.andEqualTo("profession",achievementLevelDto.getProfession());
		}
		if(StringUtils.isNotBlank(achievementLevelDto.getSubject())) {
			criteria.andEqualTo("subject",achievementLevelDto.getSubject());
		}
		List<AchievementLevel> list = achievementLevelDao.selectByExample(example);
		List<AchievementLevelVo> voList = new ArrayList<>();
		if(CollectionUtil.isNotEmpty(list)) {
			list.forEach(temp->{
				AchievementLevelVo achievementLevelVo = new AchievementLevelVo();
				BeanUtils.copyProperties(temp, achievementLevelVo);
				Example courseExample = new Example(AchievementCourse.class);
				Example.Criteria cCriteria = courseExample.createCriteria();
				cCriteria.andEqualTo("achienvementId",achievementLevelVo.getId());
				List<AchievementCourse> aCourses = achievementCourseDao.selectByExample(courseExample);
				List<String> codes = new ArrayList<>();
				aCourses.forEach(aCourse->{
					codes.add(aCourse.getCourseCode());
				});
				Example course = new Example(Course.class);
				Example.Criteria courseCriteria = course.createCriteria();
				courseCriteria.andIn("code", codes);
				List<Course> courses = courseDao.selectByExample(courseCriteria);
				StringBuilder stringBuilder = new StringBuilder();
				courses.forEach(c->{
					stringBuilder.append(c.getName());
				});
				achievementLevelVo.setCourses(stringBuilder.toString());
			});
		}
		PageInfo<AchievementLevelVo> pageInfo =new PageInfo<>(voList);
		return pageInfo;
	}
	
	@Override
	public int copy(AchievementLevelDto dto) {
		if(dto.getId()==null||dto.getGrade()==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		AchievementLevel achievementLevel = achievementLevelDao.selectByPrimaryKey(dto.getId());
		if(achievementLevel==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",I18nUtil.getMsg("achievementLevel.level")));
		}
		AchievementLevel copyAchievementLevel = new AchievementLevel();
		BeanUtils.copyProperties(achievementLevel, copyAchievementLevel);
		copyAchievementLevel.setId(null);
		copyAchievementLevel.setGrade(dto.getGrade());
		int result = achievementLevelDao.insertSelective(copyAchievementLevel);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("achievementLevel.copyError",I18nUtil.getMsg("achievementLevel.level")));
		}
		Example courseExample = new Example(AchievementCourse.class);
		Example.Criteria cCriteria = courseExample.createCriteria();
		cCriteria.andEqualTo("achienvementId",copyAchievementLevel.getId());
		List<AchievementCourse> aCourses = achievementCourseDao.selectByExample(courseExample);
		List<AchievementCourse> copyAchievementCourses = new ArrayList<>();
		if(CollectionUtil.isEmpty(copyAchievementCourses)) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",I18nUtil.getMsg("achievementLevel.level")));
		}
		aCourses.forEach(c->{
			AchievementCourse achievementCourse = new AchievementCourse();
			BeanUtils.copyProperties(c, achievementCourse);
			achievementCourse.setAchienvementId(copyAchievementLevel.getId());
			copyAchievementCourses.add(achievementCourse);
		});
		result = achievementCourseDao.batchInsert(copyAchievementCourses);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("achievementLevel.copyError",I18nUtil.getMsg("achievementLevel.level")));
		}
		return result;
	}
	
	@Override
	public int add(AchievementLevelDto dto) {
		//1.保存分级等级
		AchievementLevel achievementLevel = new AchievementLevel();
		BeanUtils.copyProperties(dto, achievementLevel);
		int result = achievementLevelDao.insertSelective(achievementLevel);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("achievementLevel.level")));
		}
		//2.保存等级与课程关系表
		List<AchievementCourse> list = new ArrayList<>();
		List<String> courses = dto.getCourseCodes();
		courses.forEach(c->{
			AchievementCourse achievementCourse = new AchievementCourse();
			achievementCourse.setAchienvementId(achievementLevel.getId());
			achievementCourse.setCourseCode(c);
			list.add(achievementCourse);
		});
		result = achievementCourseDao.batchInsert(list);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.saveError",I18nUtil.getMsg("achievementLevel.level")));
		}
		return result;
		
	}
	
	@Override
	public AchievementLevelVo getLevel(Long id) {
		AchievementLevel achievementLevel = achievementLevelDao.selectByPrimaryKey(id);
		if(achievementLevel==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.dataError",I18nUtil.getMsg("achievementLevel.level")));
		}
		AchievementLevelVo achievementLevelVo = new AchievementLevelVo();
		BeanUtils.copyProperties(achievementLevel, achievementLevelVo);
		Example example = new Example(AchievementCourse.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("achienvementId",id);
		List<AchievementCourse> list = achievementCourseDao.selectByExample(example);
		List<String> codes = new ArrayList<>();
		list.forEach(temp->{
			codes.add(temp.getCourseCode());
		});
		Example course = new Example(Course.class);
		Example.Criteria courseCriteria = course.createCriteria();
		courseCriteria.andIn("code", codes);
		List<Course> courses = courseDao.selectByExample(courseCriteria);
		achievementLevelVo.setList(courses);
		return achievementLevelVo;
	}
	
	@Override
	public int update(AchievementLevelDto dto) {
		if(dto.getId()==null) {
			throw new ParameterValidateException(I18nUtil.getMsg("baseresservice.parameterError"));
		}
		//1.保存分级等级
		AchievementLevel achievementLevel = new AchievementLevel();
		BeanUtils.copyProperties(dto, achievementLevel);
		int result = achievementLevelDao.updateByPrimaryKeySelective(achievementLevel);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("achievementLevel.level")));
		}
		//2清空等级分级与课程关联关系
		Example example = new Example(AchievementCourse.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andEqualTo("achienvementId",dto.getId());
		result = achievementCourseDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("achievementLevel.level")));
		}
		//3.保存等级与课程关系表
		List<AchievementCourse> list = new ArrayList<>();
		List<String> courses = dto.getCourseCodes();
		courses.forEach(c->{
			AchievementCourse achievementCourse = new AchievementCourse();
			achievementCourse.setAchienvementId(achievementLevel.getId());
			achievementCourse.setCourseCode(c);
			list.add(achievementCourse);
		});
		result = achievementCourseDao.batchInsert(list);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.editError",I18nUtil.getMsg("achievementLevel.level")));
		}
		return result;
		
	}
	
	@Override
	public int delete(List<Long> ids) {
		//1.删除分级等级
		Example example = new Example(AchievementLevel.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("id", ids);
		int result = achievementLevelDao.deleteByExample(example);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("achievementLevel.level")));
		}
		Example refExample = new Example(AchievementCourse.class);
		Example.Criteria refCriteria = example.createCriteria();
		criteria.andIn("achienvementId", ids);
		result = achievementCourseDao.deleteByExample(refExample);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("common.failSuccess",I18nUtil.getMsg("achievementLevel.level")));
		}
		return result;
	}

}
