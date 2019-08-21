package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.ElcResultDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcResultQuery;

import tk.mybatis.mapper.common.Mapper;

public interface ElcResultCountDao extends Mapper<Student> {
	
	/**
	 * 学生维度查找选课结果统计
	 * @param elcResult
	 * @return
	 */
	Page<ElcResultDto> getElcResult(@Param("elcResult") ElcResultQuery elcResult);

	/**
	 * 查找对应类别下学生已选课人数
	 * @param query
	 * @return
	 */
	Integer getNumberOfelectedPersons(@Param("query")ElcResultQuery query);

	/**
	 * 学生维度中选课门熟
	 * @param query
	 * @return
	 */
	Integer getElcGateMumber(@Param("query")ElcResultQuery query);

	/**
	 * 学生维度选课人次
	 * @param query
	 * @return
	 */
	Integer getElcPersonTime(@Param("query")ElcResultQuery query);

	/**
	 * 学院维度查找统计结果
	 * @param query
	 * @return
	 */
	Page<ElcResultDto> getElcResultByFacult(@Param("elcResult")ElcResultQuery elcResult);

	/**
	 * 从学院层面从查询每个学院每个转专业选课人数
	 * @param query
	 * @return
	 */
	Integer getNumberOfelectedPersonsByFaculty(@Param("query")ElcResultQuery query);

	/**
	 * 从学院层面从查询学生选课门数
	 * @param query
	 * @return
	 */
	Integer getElcGateMumberByFaculty(@Param("query")ElcResultQuery query);

	/**
	 * 从学院层面从查询学生选课人次
	 * @param query
	 * @return
	 */
	Integer getElcPersonTimeByFaculty(@Param("query")ElcResultQuery query);

	Integer getElcNumberByFaculty(@Param("query")ElcResultQuery condition);

	Integer getElcNumber(@Param("query")ElcResultQuery condition);
	
}