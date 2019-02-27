package com.server.edu.election.studentelec.rules.bk;

import org.springframework.stereotype.Component;

import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.rules.AbstractRuleExceutor;

/**
 * 检查学生是否缴费，未缴费的不能进入选课
 * */
@Component("StdPayCostCheckerRule")
public class StdPayCostCheckerRule extends AbstractRuleExceutor {

	@Override
    public boolean checkRule(ElecContext context) {
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
