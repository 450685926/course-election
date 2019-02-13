package com.server.edu.election.dao;

import com.github.pagehelper.Page;
import com.server.edu.election.entity.ElcCourseTake;
import com.server.edu.election.entity.RebuildCourseNoChargeList;
import com.server.edu.election.entity.RebuildCourseNoChargeType;
import com.server.edu.election.vo.StudentVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface ElcCourseTakeDao extends Mapper<ElcCourseTake> {
    //判断申请免修免考课程是否已经选课
    int findIsEletionCourse(@Param("studentCode") String studentCode,
                            @Param("calendarId") Long calendarId,@Param("courseCode") String courseCode);


    //查询重修未缴费课程名单
    Page<RebuildCourseNoChargeList> findCourseNoChargeList(RebuildCourseNoChargeType condition);

    //查询学生重修未缴费总门数
    Page<StudentVo> findCourseNoChargeStudentList(RebuildCourseNoChargeType condition);
}