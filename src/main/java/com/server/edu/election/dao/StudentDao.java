package com.server.edu.election.dao;

import java.util.List;

import com.server.edu.election.vo.StudentVo;
import org.apache.ibatis.annotations.Param;

import com.github.pagehelper.Page;
import com.server.edu.election.dto.Student4Elc;
import com.server.edu.election.dto.StudentDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.StudentPayment;
import com.server.edu.election.query.ElcResultQuery;
import com.server.edu.election.studentelec.context.ElcCourseResult;
import com.server.edu.election.vo.AllCourseVo;

import tk.mybatis.mapper.common.Mapper;

public interface StudentDao extends Mapper<Student> {
    //通过学号获取学生信息
    Student findStudentByCode(String studentCode);
    
    //获取所有在校学生
    List<Student> findAllStudents();
    //通过学生code集合
    List<Student> findStudentByIds(@Param("studentIds") List<String> studentIds);
    List<Student> selectElcStudents(StudentDto student);
    List<Student> selectUnElcStudents(StudentDto student);
    List<Student> selectElcInvincibleStds(StudentVo studentVo);
    List<Student> selectUnElcInvincibleStds(Student student);


    /**根据轮次查询学生信息*/
    Student findStuRound(@Param("roundId") Long roundId, @Param("studentId")String studentId);

    /**是否是预警学生*/
    Student isLoserStu(@Param("roundId") Long roundId, @Param("studentId")String studentId);
	
    
    /** 
     * 获取全部课程信息
     * @param  allCourseVo
     * @return
     */
    List<ElcCourseResult> getAllCourse(AllCourseVo allCourseVo);

    /**
     * 获取全部课程的教学班id
     * @param  allCourseVo
     * @author
     * @return
     */
    List<String> getAllTeachClassIds(AllCourseVo allCourseVo);
    
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
	Integer isTakeNum(@Param("courseCode") String courseCode);
	
	StudentPayment getStudentPayment(@Param("studentId") String studentId,@Param("year") String year,@Param("semester") String semester);

    String findStuEmail(@Param("studentId") String studentId);

    /**
     * @param grade
     * @param profession
     * @return
     */
    String getStudentMajor(@Param("grade")Integer grade, @Param("profession")String profession);

    String getStudentCampus(@Param("calendarId")Long calendarId, @Param("grade")Integer grade, @Param("profession")String profession);
}


