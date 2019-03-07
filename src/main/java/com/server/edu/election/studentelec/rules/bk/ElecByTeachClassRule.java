package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.election.dao.StudentDao;
import com.server.edu.election.dao.TeachingClassElectiveRestrictAttrDao;
import com.server.edu.election.dto.SuggestProfessionDto;
import com.server.edu.election.entity.Student;
import com.server.edu.election.entity.TeachingClassElectiveRestrictAttr;
import com.server.edu.election.studentelec.cache.StudentInfoCache;
import com.server.edu.util.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;
import com.server.edu.election.studentelec.rules.RulePriority;

import java.util.List;

/**
 * 按教学班选课
 */
@Component("ElecByTeachClassRule")
public class ElecByTeachClassRule extends AbstractRuleExceutor {

	// 是否按教学班选课
	private static final String IF_ELECT_BY_TEACHCLASS = "TEACHCLASS";

	// 学生专业限制项
	private static final String RULE_PARAM_MAJOR = "MAJOR";

	// 学生行政班限制项
	private static final String RULE_PARAM_ADMINCLASS = "ADMINCLASS";

	// 学生性别限制项
	private static final String RULE_PARAM_GENDER = "GENDER";

	// 学生年级限制项
	private static final String RULE_PARAM_GRADE = "GRADE";

	// 学生类别限制项
	private static final String RULE_PARAM_STDTYPE = "STDTYPE";

	// 学生学生方向限制项
	private static final String RULE_PARAM_DIRECTION = "DIRECTION";

	// 学历层次
	private static final String RULE_PARAM_EDUCATION = "EDUCATION";

	// 培养计划限制项（对教务管理上没有“行政班”这一概念的学校有用）
	private static final String RULE_PARAM_PROGRAM = "PROGRAM";

	// 学生专业所在院系限制项
	private static final String RULE_PARAM_DEPARTMENT = "DEPARTMENT";

	// 是否新生选课
//	private static final String RULE_PARAM_IFNEW = "IFNEW";

	@Override
	public int getOrder() {
		return RulePriority.FIFTH.ordinal();
	}

	public ElecByTeachClassRule() {
		super();
	}

	@Autowired
	private TeachingClassElectiveRestrictAttrDao restrictAttrDao;

	@Autowired
	private StudentDao studentDao;


	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		Long teachClassId = courseClass.getTeachClassId();
		StudentInfoCache studentInfo = context.getStudentInfo();
		if(teachClassId!=null){
			List<String> stringList = restrictAttrDao.selectRestrictStudent(teachClassId);//限制学生
			List<SuggestProfessionDto> suggestProfessionDtos = restrictAttrDao.selectRestrictProfession(teachClassId);//限制专业
			TeachingClassElectiveRestrictAttr record =
					new TeachingClassElectiveRestrictAttr();
			record.setTeachingClassId(teachClassId);
			TeachingClassElectiveRestrictAttr restrictAttr = restrictAttrDao.selectOne(record);//其他限制条件
			if(CollectionUtil.isNotEmpty(stringList)){
				if(stringList.contains(studentInfo.getStudentId())){
					return true;
				}
			}
			if(CollectionUtil.isNotEmpty(suggestProfessionDtos)){
				Integer grade = studentInfo.getGrade();
				String major = studentInfo.getMajor();
				for (SuggestProfessionDto suggestProfessionDto : suggestProfessionDtos) {
					if(suggestProfessionDto.getGrade().intValue()==grade.intValue()&&suggestProfessionDto.getProfession().equals(major)){
						return true;
					}
				}
			}

			if(restrictAttr!=null){
				String isOverseas = restrictAttr.getIsOverseas();//是否留学限制
				String trainingLevel = restrictAttr.getTrainingLevel();//培养层次
				String spcialPlan = restrictAttr.getSpcialPlan();//专项计划
				String isDivsex = restrictAttr.getIsDivsex();//男女班.
				Student student = studentDao.findStudentByCode(studentInfo.getStudentId());
				
			}
		}
		return false;
	}

}