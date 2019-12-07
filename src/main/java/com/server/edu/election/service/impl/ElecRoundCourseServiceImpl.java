package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.server.edu.common.entity.Teacher;
import com.server.edu.dictionary.utils.TeacherCacheUtil;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.TimeAndRoom;
import com.server.edu.election.studentelec.service.cache.TeachClassCacheService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.entity.ElectionRoundsCour;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.service.ElecRoundCourseService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElecRoundCourseServiceImpl implements ElecRoundCourseService
{
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;

    @Autowired
    private ElectionConstantsDao constantsDao;

    @Autowired
    private TeachClassCacheService teachClassCacheService;

    @Override
    public PageResult<CourseOpenDto> listPage(
        PageCondition<ElecRoundCourseQuery> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElecRoundCourseQuery query = condition.getCondition();
        Page<CourseOpenDto> listPage;
        if (Constants.PROJ_UNGRADUATE.equals(query.getProjectId())) {
        	listPage = roundCourseDao.listPage(query);
            if (CollectionUtil.isNotEmpty(listPage)){
                for (CourseOpenDto courseOpenDto: listPage) {
                    getTeacgerName(courseOpenDto);
                }
            }
        }else {
        	listPage = roundCourseDao.listPageGraduate(query);
		}
        
        PageResult<CourseOpenDto> result = new PageResult<>(listPage);
        
        return result;
    }
    
    @Override
    public PageResult<CourseOpenDto> listUnAddPage(
        PageCondition<ElecRoundCourseQuery> condition)
    {
        List<String> practicalCourse=new ArrayList<>();
        if(condition.getCondition().getMode()==2){//实践课
             practicalCourse = CultureSerivceInvoker.findPracticalCourse();
        }
        Page<CourseOpenDto> listPage;
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElecRoundCourseQuery query = condition.getCondition();
        if (Constants.PROJ_UNGRADUATE.equals(query.getProjectId())) {
        	listPage = roundCourseDao.listUnAddPage(query,practicalCourse);
        	if (CollectionUtil.isNotEmpty(listPage)){
                for (CourseOpenDto courseOpenDto: listPage) {
                    getTeacgerName(courseOpenDto);
                }
            }
		}else {
			listPage = roundCourseDao.listUnAddPageGraduate(query);
		}
        PageResult<CourseOpenDto> result = new PageResult<>(listPage);
        
        return result;
    }
    private void getTeacgerName(CourseOpenDto vo) {
        if(StringUtils.isNotBlank(vo.getTeacherCode())) {
            String[] codes = vo.getTeacherCode().split(",");
            List<Teacher> teachers = new ArrayList<Teacher>();
            for (String code : codes) {
                teachers.addAll(TeacherCacheUtil.getTeachers(code));
            }
            if(CollectionUtil.isNotEmpty(teachers)) {
                StringBuilder stringBuilder = new StringBuilder();
                for(Teacher teacher:teachers) {
                    if(teacher!=null) {
                        stringBuilder.append(teacher.getName());
                        stringBuilder.append("(");
                        stringBuilder.append(teacher.getCode());
                        stringBuilder.append(")");
                        stringBuilder.append(",");
                    }
                }
                if(stringBuilder.length()>Constants.ZERO) {
                    vo.setTeacherCode(stringBuilder.deleteCharAt(stringBuilder.length()-1).toString());
                }
            }
        }
    }
    @Override
    public PageResult<CourseOpenDto> listTeachingClassPage(
        PageCondition<ElecRoundCourseQuery> condition)
    {
        ElecRoundCourseQuery query = condition.getCondition();
        List<String> includeCodes = new ArrayList<>();
        // 1体育课
        if (Objects.equals(query.getCourseType(), 1))
        {
            String findPECourses = constantsDao.findPECourses();
            if (StringUtils.isNotBlank(findPECourses))
            {
                includeCodes.addAll(Arrays.asList(findPECourses.split(",")));
            }
        }
        else if (Objects.equals(query.getCourseType(), 2))
        {// 2英语课
            String findEnglishCourses = constantsDao.findEnglishCourses();
            if (StringUtils.isNotBlank(findEnglishCourses))
            {
                includeCodes
                        .addAll(Arrays.asList(findEnglishCourses.split(",")));
            }
        }
        query.setIncludeCourseCodes(includeCodes);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<CourseOpenDto> listPage =
            roundCourseDao.listTeachingClassPage(query);
        for (CourseOpenDto courseOpenDto : listPage) {
            TeachingClassCache teachingClassCache =
                    teachClassCacheService.getTeachClassByTeachClassId(courseOpenDto.getTeachingClassId());
            if (teachingClassCache != null) {
                List<TimeAndRoom> timeTableList = teachingClassCache.getTimeTableList();
                if (CollectionUtil.isNotEmpty(timeTableList)) {
                    List<String> list = timeTableList.stream().map(TimeAndRoom::getTimeAndRoom).collect(Collectors.toList());
                    courseOpenDto.setTimeAndRoom(String.join(",", list));
                }
            }
        }
        PageResult<CourseOpenDto> result = new PageResult<>(listPage);
        
        return result;
    }
    
    @Override
    public void add(Long roundId, List<Long> teachingClassIds)
    {
        ElectionRounds rounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (rounds == null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("elec.roundCourseExistTip"));
        }
        //过滤已经添加的课程
        List<Long> listAddedCourse =
            roundCourseDao.listAddedCourse(roundId, teachingClassIds);
        List<ElectionRoundsCour> list = new ArrayList<>();
        for(Long teachingClassId:teachingClassIds) {
        	ElectionRoundsCour electionRoundsCour = new ElectionRoundsCour();
        	if(!listAddedCourse.contains(teachingClassId)) {
		    	electionRoundsCour.setRoundsId(roundId);
		    	electionRoundsCour.setTeachingClassId(teachingClassId);
		    	list.add(electionRoundsCour);
        	}
        }
        roundCourseDao.batchInsert(list);
    }
    
    @Override
    public void addAll(ElecRoundCourseQuery condition)
    {
        List<String> practicalCourse=new ArrayList<>();
        if(condition.getMode()==2){//实践课
            practicalCourse = CultureSerivceInvoker.findPracticalCourse();
        }
        
        Page<CourseOpenDto> listPage = null;
        if (StringUtils.equals(condition.getProjectId(), "1")) {
        	listPage = roundCourseDao.listUnAddPage(condition,practicalCourse);
		}else {
			listPage = roundCourseDao.listUnAddPageGraduate(condition);
		}
        if(CollectionUtil.isNotEmpty(listPage)) {
            List<ElectionRoundsCour> list = new ArrayList<>();
            for (CourseOpenDto courseOpenDto : listPage)
            {
                ElectionRoundsCour electionRoundsCour = new ElectionRoundsCour();
                electionRoundsCour.setRoundsId(condition.getRoundId());
                electionRoundsCour.setTeachingClassId(courseOpenDto.getTeachingClassId());
                list.add(electionRoundsCour);
            }
            roundCourseDao.batchInsert(list);
        }
    }
    
    @Override
    public void delete(Long roundId, List<Long> teachingClassIds)
    {
        if (CollectionUtil.isNotEmpty(teachingClassIds))
        {
        	Example example = new Example(ElectionRoundsCour.class);
        	Example.Criteria criteria =example.createCriteria();
        	criteria.andEqualTo("roundsId", roundId);
        	criteria.andIn("teachingClassId", teachingClassIds);
            roundCourseDao.deleteByExample(example);
        }
    }
    
    @Override
    public void deleteAll(Long roundId)
    {
        roundCourseDao.deleteByRoundId(roundId);
    }
    
    @Override
	public List<ElectionRoundsCour> getTeachingClassIds(Long roundId){
    	Example example = new Example(ElectionRoundsCour.class);
    	Example.Criteria criteria = example.createCriteria();
    	criteria.andEqualTo("roundsId", roundId);
    	List<ElectionRoundsCour> list = roundCourseDao.selectByExample(example);
    	return list;
    }
    
}
