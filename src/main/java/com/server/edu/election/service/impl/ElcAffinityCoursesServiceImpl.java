package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.CourseOpenDao;
import com.server.edu.election.dao.ElcAffinityCoursesDao;
import com.server.edu.election.dao.ElcAffinityCoursesStdsDao;
import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dto.ElcAffinityCoursesDto;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.CourseOpen;
import com.server.edu.election.entity.ElcAffinityCourses;
import com.server.edu.election.entity.ElcAffinityCoursesStds;
import com.server.edu.election.entity.Student;
import com.server.edu.election.service.ElcAffinityCoursesService;
import com.server.edu.election.vo.CourseOpenVo;
import com.server.edu.election.vo.ElcAffinityCoursesVo;
import com.server.edu.exception.ParameterValidateException;
import com.server.edu.util.CollectionUtil;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcAffinityCoursesServiceImpl implements ElcAffinityCoursesService
{
    @Autowired
    private ElcAffinityCoursesDao elcAffinityCoursesDao;
    
    @Autowired
    private ElcAffinityCoursesStdsDao elcAffinityCoursesStdsDao;
    
    @Autowired
    private CourseOpenDao courseOpenDao;
    
    @Autowired
    private StudentDao studentDao;
    
    @Override
    public PageInfo<ElcAffinityCoursesVo> list(
        PageCondition<ElcAffinityCoursesDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<ElcAffinityCoursesVo> list = elcAffinityCoursesDao
            .selectElcAffinityCourses(condition.getCondition());
        PageInfo<ElcAffinityCoursesVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public int delete(List<Long> courseIds)
    {
        Example example = new Example(ElcAffinityCourses.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("courseId", courseIds);
        int result = elcAffinityCoursesDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        Example refExample = new Example(ElcAffinityCoursesStds.class);
        Example.Criteria refCriteria = refExample.createCriteria();
        refCriteria.andIn("courseId", courseIds);
        List<ElcAffinityCoursesStds> list =
            elcAffinityCoursesStdsDao.selectByExample(refExample);
        if (CollectionUtil.isNotEmpty(list))
        {
            result = elcAffinityCoursesStdsDao.deleteByExample(refExample);
            if (result <= Constants.ZERO)
            {
                throw new ParameterValidateException(
                    I18nUtil.getMsg("common.failSuccess",
                        I18nUtil.getMsg("elcAffinity.courses")));
            }
        }
        return result;
    }
    
    @Override
    public PageInfo<CourseOpenVo> courseList(PageCondition<CourseOpen> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        CourseOpen courseOpen = condition.getCondition();
        List<CourseOpenVo> list = courseOpenDao.selectCourseList(courseOpen);
        PageInfo<CourseOpenVo> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public int addCourse(List<Long> ids)
    {
        List<ElcAffinityCourses> list = new ArrayList<>();
        ids.forEach(temp -> {
            ElcAffinityCourses elcAffinityCourses = new ElcAffinityCourses();
            elcAffinityCourses.setCourseId(temp);
            list.add(elcAffinityCourses);
        });
        int result = elcAffinityCoursesDao.insertList(list);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
    @Override
    public PageInfo<Student> studentList(PageCondition<StudentDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<Student> list =
            studentDao.selectElcStudents(condition.getCondition());
        PageInfo<Student> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public PageInfo<Student> getStudents(PageCondition<StudentDto> condition)
    {
        PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
        List<Student> list =
            studentDao.selectUnElcStudents(condition.getCondition());
        PageInfo<Student> pageInfo = new PageInfo<>(list);
        return pageInfo;
    }
    
    @Override
    public int addStudent(ElcAffinityCoursesVo elcAffinityCoursesVo)
    {
        List<ElcAffinityCoursesStds> stuList = new ArrayList<>();
        elcAffinityCoursesVo.getStudentIds().forEach(temp -> {
            ElcAffinityCoursesStds elcAffinityCoursesStds =
                new ElcAffinityCoursesStds();
            elcAffinityCoursesStds
                .setCourseId(elcAffinityCoursesVo.getCourseId());
            elcAffinityCoursesStds.setStudentId(temp);
            stuList.add(elcAffinityCoursesStds);
        });
        int result = elcAffinityCoursesStdsDao.batchInsert(stuList);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
    @Override
    public int batchAddStudent(StudentDto studentDto)
    {
        List<Student> list = studentDao.selectUnElcStudents(studentDto);
        List<ElcAffinityCoursesStds> stuList = new ArrayList<>();
        list.forEach(temp -> {
            ElcAffinityCoursesStds elcAffinityCoursesStds =
                new ElcAffinityCoursesStds();
            elcAffinityCoursesStds.setCourseId(studentDto.getCourseId());
            elcAffinityCoursesStds.setStudentId(temp.getStudentCode());
            stuList.add(elcAffinityCoursesStds);
        });
        int result = elcAffinityCoursesStdsDao.batchInsert(stuList);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.saveError",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
    @Override
    public int deleteStudent(ElcAffinityCoursesVo vo)
    {
        Example example = new Example(ElcAffinityCoursesStds.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("courseId", vo.getCourseId());
        criteria.andIn("studentId", vo.getStudentIds());
        int result = elcAffinityCoursesStdsDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
    @Override
    public int batchDeleteStudent(Long courseId)
    {
        Example example = new Example(ElcAffinityCoursesStds.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("courseId", courseId);
        int result = elcAffinityCoursesStdsDao.deleteByExample(example);
        if (result <= Constants.ZERO)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("common.failSuccess",
                    I18nUtil.getMsg("elcAffinity.courses")));
        }
        return result;
    }
    
}
