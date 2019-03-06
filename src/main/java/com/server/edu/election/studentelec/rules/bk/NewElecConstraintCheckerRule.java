package com.server.edu.election.studentelec.rules.bk;

import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.dao.ElectionConstantsDao;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * 选课限制检查器<br>
 * 采用全新的大而全的囊括所有门数/学分限制的ElectionConstraintBean来限制学生的选课行为<br>
 * 目前只限制学生的新选学分上限和重修门数上限
 */
@Component("NewElecConstraintCheckerRule")
public class NewElecConstraintCheckerRule extends AbstractRuleExceutor {

	public static final String STATE_PARAM = "NEW_ELECT_CONS";

	@Autowired
	private ElectionConstantsDao constantsDao;

	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {

		if(courseClass.getTeacherClassId()!=null){
			if("1".equals(courseClass.getTeacherClassType())){//重修
				String number = constantsDao.findRebuildCourseNumber();
				if(StringUtils.isBlank(number)){
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
							I18nUtil.getMsg("ruleCheck.rebuildElcNumNotExist"));
					return false;
				}
				int totalNumber = 0;
				try {
					totalNumber = Integer.parseInt(number);
					Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
					Set<SelectedCourse> collect = selectedCourses.stream().filter(selectedCourse -> selectedCourse.isRebuildElec() == true).collect(Collectors.toSet());
					int size = collect.size();//已选重修门数
					if(size<totalNumber){
						return true;
					}else{
						ElecRespose respose = context.getRespose();
						respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
								I18nUtil.getMsg("ruleCheck.rebuildElecNumberLimit"));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
							I18nUtil.getMsg("ruleCheck.psrseError"));

				}
			}else{//新选
				String credits = constantsDao.findNewCreditsLimit();
				if(StringUtils.isBlank(credits)){
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
							I18nUtil.getMsg("ruleCheck.rebuildElcNumNotExist"));
					return false;
				}
				double num = 0.0;
				try {
					num = Double.parseDouble(credits);
					Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
					Set<SelectedCourse> collect = selectedCourses.stream().filter(selectedCourse -> selectedCourse.isRebuildElec() == false).collect(Collectors.toSet());
						
					if(size<num){
						return true;
					}else{
						ElecRespose respose = context.getRespose();
						respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
								I18nUtil.getMsg("ruleCheck.rebuildElecNumberLimit"));
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons().put(courseClass.getTeacherClassId().toString(),
							I18nUtil.getMsg("ruleCheck.psrseError"));

				}
			}
		}
		return false;
	}

}
