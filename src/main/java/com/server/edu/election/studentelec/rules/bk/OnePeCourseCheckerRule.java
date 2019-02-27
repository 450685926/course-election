package com.server.edu.election.studentelec.rules.bk;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 只能选一门体育课的规则
 *
 */
@Component("OnePeCourseCheckerRule")
public class OnePeCourseCheckerRule extends AbstractRuleExceutor {
	
	private static final String PARAM_ELECTED_PE_COURSE_IDS = "PARAM_ELECTED_PE_COURSE_IDS";
	/**
	 * 执行选课操作时
	 */
	@Override
    public boolean checkRule(ElecContext context) {
//		ElectionCourseContext electContext = (ElectionCourseContext) context;
//		// 如果是换课，那么不检查这个玩意儿
//		if(ElectRuleType.EXCHANGE.equals(electContext.getOp())) {
//			return true;
//		}
//		ElectState state = electContext.getState();
//		if(Boolean.TRUE.equals(state.getParams().get("PEFree"))) {
//			return true;
//		}
//
//		Lesson lesson = electContext.getLesson();
//		Set<Long> courseIds =  state.getElectedCourseIds().keySet();
//		if(CollectionUtils.isEmpty(courseIds)) {
//			return true;
//		}
//		String[] peCodes = (String[]) electContext.getState().getParams().get(PARAM_ELECTED_PE_COURSE_IDS);
//		if(peCodes == null) {
//			peCodes = getPeCodes();
//			electContext.getState().getParams().put(PARAM_ELECTED_PE_COURSE_IDS, peCodes);
//		}
//		if(peCodes == null || peCodes.length = 0) {
//			return true;
//		}
//		boolean isPe = false;
//		for(String peCode : peCodes) {
//			if(lesson.getCourse().getCode().equals(peCode)) {
//				isPe = true;
//				break;
//			}
//		}
//		if(!isPe) {
//			return true;
//		}
//		List electedPeCourseIds = entityDao.search(
//				OqlBuilder
//					.from(Course.class, "c")
//					.select("c.id")
//					.where("c.id in (:cids)", courseIds)
//					.where("c.code in (:peCodes)", peCodes)
//					.cacheable(true));
//		boolean peElected = CollectionUtils.isNotEmpty(electedPeCourseIds);
//		
//		if(peElected) {
//			context.addMessage(new ElectMessage("只能选一门体育课", ElectRuleType.ELECTION, false, lesson));
//			return false;
//		}
		return true;
	}

	/**
	 * 判断是否体育课
	 * @param pcourse
	 * @return
	 */
	private String[] getPeCodes() {
	    List<String> res = new ArrayList<>();
//		OqlBuilder<ElectionConstant> query = OqlBuilder.from(ElectionConstant.class, "ec");
//		query.where("ec.key = :key", ElectionConstantKey.PE_COURSE_CODES).cacheable();
//		List<ElectionConstant> constants = entityDao.search(query);
//		if(constants.isEmpty()) {
//			return new String[]{};
//		}
//		for(ElectionConstant ec : constants) {
//			String[] peCodes = ec.getValue().split(",");
//			for(String s : peCodes) {
//				res.add(s);
//			}
//		}
		return res.toArray(new String[]{});
	}
	
}