package com.server.edu.election.studentelec.rules.yjs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.entity.ElectionConstants;
import com.server.edu.election.service.ElectionConstantsService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.context.SelectedCourse;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.vo.ElecFirstLanguageContrastVo;
import com.server.edu.util.CollectionUtil;

/**
 *   英语小课门数限制
 *
 */
@Component("yjsEnglishElectionLimitRule")
public class EnglishElectionLimitRule extends AbstractElecRuleExceutor{
    @Autowired
    private ElectionConstantsService electionConstantsService;
	
	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		// 查看该课程是否是第一外国语课程
		List<ElecFirstLanguageContrastVo> foreignCourses = context.getFirstForeignCourses();
		List<ElecFirstLanguageContrastVo> collect = foreignCourses.stream()
				                                    .filter(vo->StringUtils.equals(vo.getCourseCode(), courseClass.getCourseCode()))
													.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(collect)) {
			// 不是第一外国语课程，直接pass
			return true;
		}else {
			// 是第一外国语课程，则判断已选门数
			// 获取研究生英语小课门数限制常量
			int foreignLimit = 0; 
			
			String trainingLevel = context.getStudentInfo().getTrainingLevel();
			String projectId = context.getStudentInfo().getManagerDeptId();
			List<ElectionConstants> list = electionConstantsService.getAllGraduateConstants(projectId);
			for (ElectionConstants electionConstants : list) {
				if (StringUtils.equals(electionConstants.getTrainingLevel(), trainingLevel)) {
					foreignLimit = Integer.valueOf(electionConstants.getValue());
				}
			}
			
			// 获取已选的第一外国语课程
			Set<SelectedCourse> selectedCourses = context.getSelectedCourses();
			
			List<SelectedCourse> list2 = new ArrayList<SelectedCourse>();  // 已选第一外国语课程
			for (ElecFirstLanguageContrastVo firstLanguage : foreignCourses) {
				for (SelectedCourse selectedCourse : selectedCourses) {
					if (StringUtils.equals(firstLanguage.getCourseCode(), selectedCourse.getCourseCode())) {
						list2.add(selectedCourse);
					}
				}
			}
			
			// 判断限值
			if (CollectionUtil.isEmpty(list2)) {
				return true;
			}else{
				if ((list2.size() < foreignLimit)) {
					return true;
				}else {
					ElecRespose respose = context.getRespose();
					respose.getFailedReasons().put(courseClass.getCourseCodeAndClassCode(),I18nUtil.getMsg("ruleCheck.exemption"));
					return false;
				}
			}
		}
	}
}
