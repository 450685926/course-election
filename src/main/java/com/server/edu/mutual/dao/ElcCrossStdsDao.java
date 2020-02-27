package com.server.edu.mutual.dao;

import java.util.List;

import com.server.edu.common.entity.Department;
import com.server.edu.mutual.dto.ElcMutualCrossStuDto;
import com.server.edu.mutual.entity.ElcCrossStdVo;
import com.server.edu.mutual.entity.ElcCrossStds;
import com.server.edu.mutual.vo.ElcMutualCrossStuVo;

import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

public interface ElcCrossStdsDao extends Mapper<ElcCrossStds>,MySqlMapper<ElcCrossStds> {
	List<ElcMutualCrossStuVo> getCrossStds(ElcMutualCrossStuDto dto);

	//返回单个po对象在切换学期时代码报错（切换未上送学生id所以返回多条记录），故统一使用list接收
	List<ElcMutualCrossStuVo> isInElcMutualStdList(ElcMutualCrossStuDto dto);

	List<Department> findFaculty(@Param("virtualDept") String virtualDept,
								 @Param("managDeptId") String managDeptId,
								 @Param("type") Integer type);

	/**
	 * 根据条件全量删除跨专业选课学生
	 * @param calendarId
	 * @param studentList
	 */
	int deleteCrossByParames(@Param("calendarId") long calendarId,@Param("studentList") List<String> studentList);


	/**
	 * 通过学院查询学生id
	 * @param facultyCondition
	 * @return
	 */
	List<String> queryStudentIdByFacuty(@Param("facultyCondition") String facultyCondition);
}