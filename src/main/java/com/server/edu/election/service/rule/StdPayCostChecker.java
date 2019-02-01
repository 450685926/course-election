package com.server.edu.election.service.rule;

import com.server.edu.election.dto.ElcCourseTakeDto;
import com.server.edu.election.service.rule.context.AbstractElectRuleExecutor;
import com.server.edu.election.service.rule.context.ElectState;

/**
 * 检查学生是否缴费，未缴费的不能进入选课
 * */
public class StdPayCostChecker extends AbstractElectRuleExecutor {

	@Override
    public boolean execute(ElectState state, ElcCourseTakeDto courseTake) {
//		ElectionCourseContext electContext = (ElectionCourseContext) context;
//		if(null == electContext.getStudent()) {
//			electContext.addError(new ElectMessage("缺少登陆学生信息", ElectRuleType.GENERAL, false, null));
//			return false;
//		}
//		OqlBuilder<StdPayResult> query = OqlBuilder.from(StdPayResult.class, "pay");
//		query.where("pay.std =:std", electContext.getStudent());
//		query.where("pay.paid = false");
//		List<StdPayResult> payResults = entityDao.search(query);
//		if(CollectionUtils.isNotEmpty(payResults)){
//			electContext.addMessage(new ElectMessage("请先进行缴学费", ElectRuleType.GENERAL, false, null));
//			return false;
//		}
		return true;
	}

}
