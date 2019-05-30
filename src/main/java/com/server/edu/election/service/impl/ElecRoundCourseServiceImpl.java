package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dao.ElecRoundCourseDao;
import com.server.edu.election.dao.ElecRoundsDao;
import com.server.edu.election.dto.CourseOpenDto;
import com.server.edu.election.entity.ElectionRounds;
import com.server.edu.election.query.ElecRoundCourseQuery;
import com.server.edu.election.service.ElecRoundCourseService;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

@Service
public class ElecRoundCourseServiceImpl implements ElecRoundCourseService
{
    @Autowired
    private ElecRoundCourseDao roundCourseDao;
    
    @Autowired
    private ElecRoundsDao elecRoundsDao;

    @Autowired
    private ElectionConstantsDao constantsDao;
    
    @Override
    public PageResult<CourseOpenDto> listPage(
        PageCondition<ElecRoundCourseQuery> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElecRoundCourseQuery query = condition.getCondition();
        Page<CourseOpenDto> listPage = roundCourseDao.listPage(query);
        
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
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        ElecRoundCourseQuery query = condition.getCondition();
        Page<CourseOpenDto> listPage = roundCourseDao.listUnAddPage(query,practicalCourse);

        PageResult<CourseOpenDto> result = new PageResult<>(listPage);
        
        return result;
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
        
        PageResult<CourseOpenDto> result = new PageResult<>(listPage);
        
        return result;
    }
    
    @Override
    public void add(Long roundId, List<String> courseCodes)
    {
        ElectionRounds rounds = elecRoundsDao.selectByPrimaryKey(roundId);
        if (rounds == null)
        {
            throw new ParameterValidateException("选课轮次不存在");
        }
        //过滤已经添加的课程
        List<String> listAddedCourse =
            roundCourseDao.listAddedCourse(roundId, courseCodes);
        for (String courseCode : courseCodes)
        {
            if (!listAddedCourse.contains(courseCode))
            {
                roundCourseDao.add(roundId, courseCode);
            }
        }
    }
    
    @Override
    public void addAll(ElecRoundCourseQuery condition)
    {
        List<String> practicalCourse=new ArrayList<>();
        if(condition.getMode()==2){//实践课
            practicalCourse = CultureSerivceInvoker.findPracticalCourse();
        }
        Page<CourseOpenDto> listPage = roundCourseDao.listUnAddPage(condition,practicalCourse);
        for (CourseOpenDto courseOpenDto : listPage)
        {
            roundCourseDao.add(condition.getRoundId(),
                courseOpenDto.getCourseCode());
        }
    }
    
    @Override
    public void delete(Long roundId, List<String> courseCodes)
    {
        if (CollectionUtil.isNotEmpty(courseCodes))
        {
            roundCourseDao.delete(roundId, courseCodes);
        }
    }
    
    @Override
    public void deleteAll(Long roundId)
    {
        roundCourseDao.deleteByRoundId(roundId);
    }
    
}
