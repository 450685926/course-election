package com.server.edu.election.studentelec.preload;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.server.edu.common.entity.StudentCultureRel;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.context.ElecContext;

/**
 * 研究生第一外国语课程
 * @author xlluoc
 */
public class YJSFirstForeignCourseLoad extends DataProLoad<ElecContext>{
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Override
	public int getOrder() {
		return 4;
	}

	@Override
	public String getProjectIds() {
		return "2,4";
	}

	@Override
	public void load(ElecContext context) {
		String studentId = context.getRequest().getStudentId();
		StudentCultureRel studentCultureRel = new StudentCultureRel();
		studentCultureRel.setStudentId(studentId);
		
		List<StudentCultureRel> list = CultureSerivceInvoker.findStudentCultureRelList(studentCultureRel);
		context.setFirstForeignCourses(list);
	}

}
