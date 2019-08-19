package com.server.edu.election.studentelec.preload;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.server.edu.election.rpc.CultureSerivceInvoker;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.vo.ElecFirstLanguageContrastVo;

/**
 * 研究生全部第一外国语课程
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
		String prijectId = context.getStudentInfo().getManagerDeptId();
		List<ElecFirstLanguageContrastVo> list = CultureSerivceInvoker.getStudentFirstForeignLanguage(prijectId,0,999);
		//List<StudentCultureRel> list = CultureSerivceInvoker.findStudentCultureRelList(studentCultureRel);
		context.setFirstForeignCourses(list);
	}
}
