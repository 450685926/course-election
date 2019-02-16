package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
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
    public String add(ElcCourseTakeAddDto add)
    {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        Long calendarId = add.getCalendarId();
        List<String> studentIds = add.getStudentIds();
        List<Long> teachingClassIds = add.getTeachingClassIds();
        for (String studentId : studentIds)
        {
            for (int i = 0; i < teachingClassIds.size(); i++)
            {
                Long teachingClassId = teachingClassIds.get(i);
                ElcCourseTake record = new ElcCourseTake();
                record.setStudentId(studentId);
                record.setTeachingClassId(teachingClassId);
                int selectCount = courseTakeDao.selectCount(record);
                if (selectCount == 0)
                {
                    ElcCourseTakeVo vo = courseTakeDao
                        .getTeachingClassInfo(teachingClassId, null);
                    if (null != vo && vo.getCourseId() != null)
                    {
                        ElcCourseTake take = new ElcCourseTake();
                        take.setCalendarId(calendarId);
                        take.setChooseObj(ChooseObj.ADMIN.type());
                        take.setCourseId(vo.getCourseId());
                        take.setCourseTakeType(CourseTakeType.NORMAL.type());
                        take.setCreatedAt(date);
                        take.setStudentId(studentId);
                        take.setTeachingClassId(teachingClassId);
                        take.setTurn(0);
                        courseTakeDao.insertSelective(take);
                    }
                    else
                    {
                        String code = teachingClassId.toString();
                        if (vo != null)
                        {
                            code = vo.getTeachingClassCode();
                        }
                        sb.append("教学班[" + code + "]对应的课程不存在,");
                    }
                }
            }
        }
        
        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }
    
    @Override
    public String addByExcel(Long calendarId, List<ElcCourseTakeAddDto> datas)
    {
        StringBuilder sb = new StringBuilder();
        Date date = new Date();
        for (ElcCourseTakeAddDto data : datas)
        {
            String studentId = StringUtils.trim(data.getStudentId());
            String teachingClassCode =
                StringUtils.trim(data.getTeachingClassCode());
            
            if (StringUtils.isNotBlank(studentId)
                && StringUtils.isNotBlank(teachingClassCode))
            {
                ElcCourseTakeVo vo = this.courseTakeDao
                    .getTeachingClassInfo(null, teachingClassCode);
                
                if (null != vo && vo.getCourseId() != null)
                {
                    Long courseId = vo.getCourseId();
                    Long teachingClassId = vo.getTeachingClassId();
                    ElcCourseTake record = new ElcCourseTake();
                    record.setStudentId(studentId);
                    record.setTeachingClassId(teachingClassId);
                    int selectCount = courseTakeDao.selectCount(record);
                    if (selectCount == 0)
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
                }
                else
                {
                    sb.append("教学班[" + teachingClassCode + "]对应的课程不存在,");
                }
                
            }
        }
        if (sb.length() > 0)
        {
            return sb.substring(0, sb.length() - 1);
        }
        return StringUtils.EMPTY;
    }
    
    @Override
    public void withdraw(List<ElcCourseTake> value)
    {
        for (ElcCourseTake take : value)
        {
            Example example = new Example(ElcCourseTake.class);
            example.createCriteria()
                .andEqualTo("studentId", take.getStudentId())
                .andEqualTo("teachingClassId", take.getTeachingClassId());
            courseTakeDao.deleteByExample(example);
        }
    }
    
}
