package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.github.pagehelper.Page;
import com.server.edu.common.enums.GroupDataEnum;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.entity.Student;
import com.server.edu.election.rpc.BaseresServiceInvoker;
import com.server.edu.session.util.SessionUtils;
import com.server.edu.session.util.entity.Session;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseDao;
import com.server.edu.election.dao.ElcCouSubsDao;
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.service.ElcCouSubsService;
import com.server.edu.election.studentelec.event.ElectLoadEvent;
import com.server.edu.election.studentelec.utils.ElecContextUtil;
import com.server.edu.election.studentelec.utils.ElecStatus;
import com.server.edu.election.vo.ElcCouSubsVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcCouSubsServiceImpl implements ElcCouSubsService
{
    @Autowired
    private ElcCouSubsDao elcCouSubsDao;
    
    @Autowired
    private CourseDao courseDao;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Override
    public PageResult<ElcCouSubsVo> page(
        PageCondition<ElcCouSubsDto> condition)
    {
        ElcCouSubsDto dto = condition.getCondition();
        dto.setProjectId(dto.getProjectId());
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<ElcCouSubsVo> page =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        if (CollectionUtil.isNotEmpty(page))
        {

            for (ElcCouSubsVo vo : page.getResult())
            {
                vo.setOrigsCourseInfo(vo.getOrigsCourseCode() + "(" + vo.getOrigsCourseName() + ")");
                vo.setSubCourseInfo(vo.getSubCourseCode() + "(" + vo.getSubCourseName() + ")");
            }
        }
        return new PageResult<>(page);
    }
    
    @Override
    @Transactional
    public int add(ElcCouSubs elcCouSubs)
    {
    	if (elcCouSubs.getOrigsCourseId().longValue() == elcCouSubs.getSubCourseId().longValue()) {
         	throw new ParameterValidateException(
                     I18nUtil.getMsg("election.subCourseError"));
 		}
        Course origsCourse = courseDao.selectByPrimaryKey(elcCouSubs.getOrigsCourseId().longValue());
        Course subCourse = courseDao.selectByPrimaryKey(elcCouSubs.getSubCourseId().longValue());
        if (origsCourse == null || subCourse == null || origsCourse.getCode() == null || subCourse.getCode() == null) {
        	throw new ParameterValidateException(
        			I18nUtil.getMsg("baseresservice.parameterError"));
		}
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("studentId", elcCouSubs.getStudentId());
        criteria.andEqualTo("projectId", elcCouSubs.getProjectId());
        criteria.andEqualTo("origsCourseId",
            elcCouSubs.getOrigsCourseId());
        criteria.andEqualTo("subCourseId", elcCouSubs.getSubCourseId());
        ElcCouSubs subs = elcCouSubsDao.selectOneByExample(example);
        if (subs != null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        elcCouSubs.setCreatedAt(new Date());
        int result = elcCouSubsDao.insertSelective(elcCouSubs);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        ElcCouSubsDto dto = new ElcCouSubsDto();
        dto.setStudentId(elcCouSubs.getStudentId());
        List<ElcCouSubsVo> list =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        ElecContextUtil.setReplaceCourses(elcCouSubs.getStudentId(),
            list);
        ElecContextUtil.initCurrentAndNextCalendarStu(elcCouSubs.getStudentId());
        return result;
    }
    
    @Override
    @Transactional
    public int update(ElcCouSubs elcCouSubs)
    {
        if (elcCouSubs.getId() == null)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        if (elcCouSubs.getOrigsCourseId().longValue() == elcCouSubs.getSubCourseId().longValue()) {
        	throw new ParameterValidateException(
                    I18nUtil.getMsg("election.subCourseError"));
		}
        Course origsCourse = courseDao.selectByPrimaryKey(elcCouSubs.getOrigsCourseId().longValue());
        Course subCourse = courseDao.selectByPrimaryKey(elcCouSubs.getSubCourseId().longValue());
        if (origsCourse == null || subCourse == null || origsCourse.getCode() == null || subCourse.getCode() == null) {
        	throw new ParameterValidateException(
        			I18nUtil.getMsg("baseresservice.parameterError"));
		}
        List<Long> ids = new ArrayList<>();
        ids.add(elcCouSubs.getId());
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotIn("id", ids);
        criteria.andEqualTo("studentId", elcCouSubs.getStudentId());
        criteria.andEqualTo("origsCourseId",
            elcCouSubs.getOrigsCourseId());
        criteria.andEqualTo("subCourseId", elcCouSubs.getSubCourseId());
        ElcCouSubs subs = elcCouSubsDao.selectOneByExample(example);
        elcCouSubs.setUpdatedAt(new Date());
        if (subs != null)
        {
            throw new ParameterValidateException(I18nUtil.getMsg("common.exist",
                I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        int result =
            elcCouSubsDao.updateByPrimaryKeySelective(elcCouSubs);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.editError",
                    I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        ElcCouSubsDto dto = new ElcCouSubsDto();
        dto.setStudentId(elcCouSubs.getStudentId());
        List<ElcCouSubsVo> list =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        ElecContextUtil.setReplaceCourses(elcCouSubs.getStudentId(),
            list);
        ElecContextUtil.initCurrentAndNextCalendarStu(elcCouSubs.getStudentId());
        return result;
    }
    
    @Override
    @Transactional
    public int delete(List<Long> ids)
    {
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", ids);
        List<ElcCouSubs> list =
            elcCouSubsDao.selectByExample(example);
        if (CollectionUtil.isEmpty(list))
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("baseresservice.parameterError"));
        }
        List<String> studentIds = list.stream()
            .map(ElcCouSubs::getStudentId)
            .collect(Collectors.toList());
        int result = elcCouSubsDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("election.elcNoGradCouSubs")));
        }
        ElcCouSubsDto dto = new ElcCouSubsDto();
        dto.setStudentIds(studentIds);
        List<ElcCouSubsVo> elcNoGradCouSubsList =
            elcCouSubsDao.selectElcNoGradCouSubs(dto);
        if (CollectionUtil.isNotEmpty(elcNoGradCouSubsList))
        {
            for (String studentId : studentIds)
            {
                List<ElcCouSubsVo> stuCous = elcNoGradCouSubsList.stream()
                    .filter(c -> studentId.equals(c.getStudentId()))
                    .collect(Collectors.toList());
                ElecContextUtil.setReplaceCourses(studentId, stuCous);
                ElecContextUtil.initCurrentAndNextCalendarStu(studentId);
            }
        }
        return result;
    }
    
    @Override
    public List<ElcCouSubs> getElcNoGradCouSubs(List<String> studentIds)
    {
        Example example = new Example(ElcCouSubs.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("studentId", studentIds);
        List<ElcCouSubs> list =
            elcCouSubsDao.selectByExample(example);
        return list;
    }

    @Override
    public PageResult<Student> findStuInfoList(PageCondition<ElcCouSubsDto> condition) {
        ElcCouSubsDto elcCouSubsDto = condition.getCondition();

        Integer mode = elcCouSubsDto.getMode();
        if(mode == null){
            throw new ParameterValidateException(
                    I18nUtil.getMsg("baseresservice.parameterError"));
        }
        Session session = SessionUtils.getCurrentSession();
        String dptId = session.getCurrentManageDptId();
        List<String> list = session.getGroupData().get(GroupDataEnum.department.getValue());
        elcCouSubsDto.setMode(mode);
        elcCouSubsDto.setProjectId(dptId);
        elcCouSubsDto.setFacultys(list);
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<Student> studentPage =  elcCouSubsDao.findStuInfoList(elcCouSubsDto);
        return new PageResult<>(studentPage);
    }

    @Override
    public PageResult<Course> findOriginCourse(PageCondition<ElcCouSubsDto> condition) {
        ElcCouSubsDto elcCouSubsDto = condition.getCondition();
        if(StringUtils.isEmpty(elcCouSubsDto.getStudentId())){
            return null;
        }

        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        Page<Course> pageCourse = elcCouSubsDao.findOriginCourse(elcCouSubsDto);
        return new PageResult<>(pageCourse);

    }

}
