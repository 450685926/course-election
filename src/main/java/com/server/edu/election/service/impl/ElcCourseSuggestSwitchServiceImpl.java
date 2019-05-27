package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ElcCourseSuggestSwitchDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElcCourseSuggestSwitch;
import com.server.edu.election.service.ElcCourseSuggestSwitchService;
import com.server.edu.election.vo.CourseOpenVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;
@Service
public class ElcCourseSuggestSwitchServiceImpl implements ElcCourseSuggestSwitchService {
	@Autowired
	private CourseOpenDao courseOpenDao;
	@Autowired
	private ElcCourseSuggestSwitchDao elcCourseSuggestSwitchDao;
	@Override
	public PageInfo<CourseOpenVo>  page(PageCondition<CourseOpenDto> condition){
		CourseOpenDto dto = condition.getCondition();
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		List<CourseOpenVo> list = courseOpenDao.selectCourseSuggestSwitch(dto);
		PageInfo<CourseOpenVo> pageInfo = new PageInfo<>(list);
		return pageInfo;
	}
	
	@Override
	public int start(List<String> courses) {
		List<ElcCourseSuggestSwitch> list = elcCourseSuggestSwitchDao.selectAll();
		int result = 0;
		if(CollectionUtil.isNotEmpty(list)) {
			List<String> courseCodes = list.stream().map(ElcCourseSuggestSwitch::getCourseCode).collect(Collectors.toList());
			Iterator<String> iterator = courses.iterator();
			while(iterator.hasNext()) {
				String coure = iterator.next(); 
				if(courseCodes.contains(coure)) {
					courses.remove(coure);
				}
			}
			if(CollectionUtil.isEmpty(courses)) {
				return result;
			}
		}
		List<ElcCourseSuggestSwitch> suggestCodes = new ArrayList<>();
		for(String courseCode:courses) {
			ElcCourseSuggestSwitch elcCourseSuggestSwitch = new ElcCourseSuggestSwitch();
			elcCourseSuggestSwitch.setCourseCode(courseCode);
			suggestCodes.add(elcCourseSuggestSwitch);
		}
		result = elcCourseSuggestSwitchDao.insertList(suggestCodes);
		if(result<=Constants.ZERO) {
			throw new ParameterValidateException(I18nUtil.getMsg("courseSuggestSwitch.startError",I18nUtil.getMsg("election.courseSuggestSwitch")));
		}
		return result;
	}
	
	@Override
	public int stop(List<String> courses) {
		Example example = new Example(ElcCourseSuggestSwitch.class);
		Example.Criteria criteria = example.createCriteria();
		criteria.andIn("courseCode", courses);
		List<ElcCourseSuggestSwitch> list = elcCourseSuggestSwitchDao.selectByExample(example);
		int result = 0;
		if(CollectionUtil.isNotEmpty(list)) {
			List<Long> ids = list.stream().map(ElcCourseSuggestSwitch::getId).collect(Collectors.toList());
			Example deleExample = new Example(ElcCourseSuggestSwitch.class);
			Example.Criteria deleCriteria = deleExample.createCriteria();
			deleCriteria.andIn("id", ids);
			result = elcCourseSuggestSwitchDao.deleteByExample(deleExample);
			if(result<=Constants.ZERO) {
				throw new ParameterValidateException(I18nUtil.getMsg("courseSuggestSwitch.stopError",I18nUtil.getMsg("election.courseSuggestSwitch")));
			}
		}
		return result;
	}
}
