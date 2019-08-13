package com.server.edu.election.studentelec.rules.yjs;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.pagehelper.Page;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.election.constants.ElectRuleType;
import com.server.edu.election.dao.ExemptionApplyDao;
import com.server.edu.election.dto.ElectionRoundsDto;
import com.server.edu.election.dto.ExemptionApplyCondition;
import com.server.edu.election.service.ElecRoundService;
import com.server.edu.election.service.ElectionRuleService;
import com.server.edu.election.studentelec.cache.TeachingClassCache;
import com.server.edu.election.studentelec.context.ElecContext;
import com.server.edu.election.studentelec.context.ElecRespose;
import com.server.edu.election.studentelec.rules.AbstractElecRuleExceutor;
import com.server.edu.election.studentelec.service.impl.RoundDataProvider;
import com.server.edu.election.vo.ElectionRuleVo;
import com.server.edu.election.vo.ExemptionApplyManageVo;

/**
 *   英语小课门数限制
 *
 */
@Component("yjsEnglishElectionLimitRule")
public class EnglishElectionLimitRule extends AbstractElecRuleExceutor{
	
	public static final String MAX_ENGLISH_COURSE_COUNT_PARAM = "MAX_ENGLISH_COURSE_COUNT";

	public static final String GOT_ENGLISH_COURSE_COUNT_PAPAM = "GOT_ENGLISH_COURSE_COUNT";
	
	//硕士生门数上限
	private static final String RULE_PARAM_SSCOURSECOUNT = "SSCOURSECOUNT";
	
	//博士生门数上限
	private static final String RULE_PARAM_BSCOURSECOUNT = "BSCOURSECOUNT";
	
	private static final String ENGLISH_ELECTION_LIMIT = "英语小课门数限制";
	
	private static final int EXAMINE_RESULT = 0; // 待审核
	
    @Autowired
    private RoundDataProvider dataProvider;
    
    @Autowired
    private ElecRoundService electionRoundService;
    
	@Autowired
	private ElectionRuleService service;
	
    @Autowired
    private ExemptionApplyDao applyDao;
	
	@Override
	public boolean checkRule(ElecContext context, TeachingClassCache courseClass) {
		// 获取轮次的规则信息，如果没有规则信息则pass
		Long roundId = context.getRequest().getRoundId();
		ElectionRoundsDto electionRoundsDto = electionRoundService.get(roundId);
		List<Long> ruleIds = electionRoundsDto.getRuleIds();
	    Boolean hasRule = true;
		for (Long ruleId : ruleIds) {
			ElectionRuleVo rule =service.selectRuleDeatil(ruleId);
			String ruleName = rule.getName();
			if (StringUtils.equals(ruleName,ENGLISH_ELECTION_LIMIT)) {
				hasRule = false;
			}
		}
		
		// 没有此规则直接pass
		if (hasRule) {
			return hasRule;
		}
		
		// 不是选课操作则直接pass
		if (hasRule) {
			if (this.getType() != ElectRuleType.ELECTION) {
				return hasRule;
			}
		}
		
		// 是否为公选课
		if (!courseClass.isPublicElec()) {
			return true;
		}
		// 学生是否在免修申请阶段
		String studentCode = context.getStudentInfo().getStudentId();
		ExemptionApplyCondition condition = new ExemptionApplyCondition(studentCode);
		Page<ExemptionApplyManageVo> exemptionApply = applyDao.findExemptionApply(condition);
		List<ExemptionApplyManageVo> result = exemptionApply.getResult();
		if (CollectionUtils.isEmpty(result)) {
			return true;
		}else {
			for (ExemptionApplyManageVo exemptionApplyManageVo : result) {
				if (exemptionApplyManageVo.getExamineResult() == EXAMINE_RESULT) {
					ElecRespose respose = context.getRespose();
			        respose.getFailedReasons().put(courseClass.getCourseCodeAndClassCode(),I18nUtil.getMsg("ruleCheck.exemption"));
					return false;
				}
			}
		}
		// 查看学生是否有第一外国语课程
		
		
		
	
		
		
		
		
		
		
		
		
		
		return false;
	}

}
