package com.server.edu.election.service;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.server.edu.common.PageCondition;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.dto.ElcCouSubsDto;
import com.server.edu.election.entity.Course;
import com.server.edu.election.entity.ElcCouSubs;
import com.server.edu.election.entity.Student;
import com.server.edu.election.vo.ElcCouSubsVo;

public interface ElcCouSubsService
{
    PageResult<ElcCouSubsVo> page(
        PageCondition<ElcCouSubsDto> condition);
    
    int add(ElcCouSubs elcCouSubs);
    
    int update(ElcCouSubs elcCouSubs);
    
    int delete(List<Long> ids);
    
    List<ElcCouSubs> getElcNoGradCouSubs(List<String> studentIds);

    PageResult<Student> findStuInfoList(PageCondition<ElcCouSubsDto> condition);

    //查找替代课程中的原课程信息
    PageResult<Course> findOriginCourse(PageCondition<ElcCouSubsDto> condition);

    //查找替代课程中的新课程信息
    PageResult<Course> findNewCourse(PageCondition<ElcCouSubsDto> condition);
}
