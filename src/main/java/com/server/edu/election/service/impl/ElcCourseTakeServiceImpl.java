package com.server.edu.election.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.ChooseObj;
import com.server.edu.election.constants.CourseTakeType;
import com.server.edu.election.dao.ElcCourseTakeDao;
import com.server.edu.election.dto.ElcCourseTakeAddDto;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.query.ElcCourseTakeQuery;
import com.server.edu.election.service.ElcCourseTakeService;
import com.server.edu.election.vo.ElcCourseTakeVo;

import tk.mybatis.mapper.entity.Example;

@Service
public class ElcCourseTakeServiceImpl implements ElcCourseTakeService
{
    @Autowired
    private ElcCourseTakeDao courseTakeDao;
    
    @Override
    public PageResult<ElcCourseTakeVo> listPage(
        PageCondition<ElcCourseTakeQuery> page)
    {
        PageHelper.startPage(page.getPageNum_(), page.getPageSize_());
        Page<ElcCourseTakeVo> listPage =
            courseTakeDao.listPage(page.getCondition());
        
        PageResult<ElcCourseTakeVo> result = new PageResult<>(listPage);
        return result;
    }
    
    @Override
    public void add(ElcCourseTakeAddDto add)
    {
        Date date = new Date();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        for (String studentId : studentIds)
        {
            List<Long> courseIds = new ArrayList<>();
            for (Long teachingClassId : teachingClassIds)
            {
                ElcCourseTake record = new ElcCourseTake();
                record.setStudentId(studentId);
                record.setTeachingClassId(teachingClassId);
                int selectCount = courseTakeDao.selectCount(record);
                if (selectCount == 0)
                {
                    Long courseId =
                        courseTakeDao.getCourseIdByClassId(teachingClassId);
                    if (null != courseId && !courseIds.contains(courseId))
                    {
                        ElcCourseTake take = new ElcCourseTake();
                        take.setCalendarId(calendarId);
                        take.setChooseObj(ChooseObj.ADMIN.type());
                        take.setCourseId(courseId);
                        take.setCourseTakeType(CourseTakeType.NORMAL.type());
                        take.setCreatedAt(date);
                        take.setStudentId(studentId);
                        take.setTeachingClassId(teachingClassId);
                        take.setTurn(0);
                        courseTakeDao.insertSelective(take);
                    }
                    courseIds.add(courseId);
                }
            }
        }
    }
    
    @Override
    public void withdraw(List<ElcCourseTakeAddDto> value)
    {
        for (ElcCourseTakeAddDto add : value)
        {
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria()
                .andEqualTo("studentId", add.getStudentId())
                .andIn("teachingClassId", add.getTeachingClassIds());
            courseTakeDao.deleteByExample(example);
        }
    }
    
}
