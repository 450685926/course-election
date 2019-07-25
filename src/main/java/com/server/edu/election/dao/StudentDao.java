package com.server.edu.election.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.studentelec.context.ElcCourseResult;
import com.server.edu.election.vo.AllCourseVo;

import tk.mybatis.mapper.common.Mapper;

public interface StudentDao extends Mapper<Student> {
    //通过学号获取学生信息
    Student findStudentByCode(String studentCode);
    List<Student> selectElcStudents(StudentDto student);
    List<Student> selectUnElcStudents(StudentDto student);
    List<Student> selectElcInvincibleStds(Student student);
    List<Student> selectUnElcInvincibleStds(Student student);


    /**根据轮次查询学生信息*/
    Student findStuRound(@Param("roundId") Long roundId, @Param("studentId")String studentId);

    /**是否是预警学生*/
    Student isLoserStu(@Param("roundId") Long roundId, @Param("studentId")String studentId);
	
    
    /** 
     * 获取全部课程信息
     * @param AllCourseVo allCourseVo
     * @return
     */
    List<ElcCourseResult> getAllCourse(AllCourseVo allCourseVo);
    
    /**
     * 查询未选课学生名单
     * @param condition
     * @return
     */
	Page<Student4Elc> getAllNonSelectedCourseStudent(@Param("query")ElcResultQuery query);
	Page<Student4Elc> getAllNonSelectedCourseStudent1(@Param("query")ElcResultQuery query);
	
	/**
	 * 查询可选课名单中培养计划中有这门课又没选该门课的学生信息
	 * @param cond
	 * @return
	 */
	Page<Student4Elc> getStudent4CulturePlan(@Param("query")ElcResultQuery cond);
	
	List<Student> getUnLimitStudents(StudentDto studentDto);

    String findCampus(String studentCode);

}


