package com.server.edu.election.service.impl;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.server.edu.common.PageCondition;
import com.server.edu.common.locale.I18nUtil;
import com.server.edu.common.rest.PageResult;
import com.server.edu.election.constants.Constants;
import com.server.edu.election.dao.ExemptionAuditSwitchDao;
import com.server.edu.election.entity.ExemptionApplyAuditSwitch;
import com.server.edu.election.service.ExemptionAuditSwitchService;
import com.server.edu.exception.ParameterValidateException;
import tk.mybatis.mapper.entity.Example;

@Service
public class ExemptionAuditSwitchServiceImpl implements ExemptionAuditSwitchService {
    @Autowired
    private ExemptionAuditSwitchDao exemptionAuditSwitchDao;
	
	@Override
	public void addExemptionAuditSwitch(ExemptionApplyAuditSwitch applyAuditSwitch) {
		Example example = new Example(ExemptionApplyAuditSwitch.class);
    	example.createCriteria()
    		.andEqualTo("grades", applyAuditSwitch.getGrades())
    		.andEqualTo("trainingLevels", applyAuditSwitch.getTrainingLevels())
    		.andEqualTo("formLearnings", applyAuditSwitch.getFormLearnings())
    		.andEqualTo("trainingCategorys", applyAuditSwitch.getTrainingCategorys())
    	    .andEqualTo("degreeTypes", applyAuditSwitch.getDegreeTypes())
    		.andEqualTo("enrolSeason", applyAuditSwitch.getEnrolSeason())
    		.andEqualTo("deleteStatus",String.valueOf(Constants.DELETE_FALSE));
    	
	    int count = exemptionAuditSwitchDao.selectCountByExample(example);
	    if (count > 0)
	    {
	        throw new ParameterValidateException(
	            I18nUtil.getMsg("exemptionApply.auditSwitch"));
	    }
		
	    Date date = new Date();
	    applyAuditSwitch.setCreatedAt(date);
	    applyAuditSwitch.setDeleteStatus(Constants.DELETE_FALSE);
	    exemptionAuditSwitchDao.insertSelective(applyAuditSwitch);
	}


	@Override
	public PageResult<ExemptionApplyAuditSwitch> queryExemptionAuditSwitch(
			PageCondition<ExemptionApplyAuditSwitch> condition) {
		PageHelper.startPage(condition.getPageNum_(), condition.getPageSize_());
		
		ExemptionApplyAuditSwitch applyAuditSwitch = condition.getCondition();
		Page<ExemptionApplyAuditSwitch> page = exemptionAuditSwitchDao.listPage(applyAuditSwitch);
		PageResult<ExemptionApplyAuditSwitch> result = new PageResult<>(page);
		return result;
	}


	@Override
	public ExemptionApplyAuditSwitch findExemptionAuditSwitchById(Long id) {
		return exemptionAuditSwitchDao.selectByPrimaryKey(id);
	}


	@Override
	public void updateExemptionAuditSwitch(ExemptionApplyAuditSwitch applyAuditSwitch) {
        Example example = new Example(ExemptionApplyAuditSwitch.class);
        example.createCriteria()
	        .andEqualTo("grades", applyAuditSwitch.getGrades())
			.andEqualTo("trainingLevels", applyAuditSwitch.getTrainingLevels())
			.andEqualTo("formLearnings", applyAuditSwitch.getFormLearnings())
			.andEqualTo("trainingCategorys", applyAuditSwitch.getTrainingCategorys())
			.andEqualTo("degreeTypes", applyAuditSwitch.getDegreeTypes())
			.andEqualTo("enrolSeason", applyAuditSwitch.getEnrolSeason())
			.andEqualTo("deleteStatus",String.valueOf(Constants.DELETE_FALSE))
            .andNotEqualTo("id", applyAuditSwitch.getId());
        int count = exemptionAuditSwitchDao.selectCountByExample(example);
        if (count > 0)
        {
            throw new ParameterValidateException(
                I18nUtil.getMsg("exemptionApply.auditSwitch"));
        }
        exemptionAuditSwitchDao.updateByPrimaryKeySelective(applyAuditSwitch);
	}


	@Override
	public void deleteExemptionAuditSwitch(List<Long> ids) {
		for (Long id : ids) {
			ExemptionApplyAuditSwitch auditSwitch = new ExemptionApplyAuditSwitch();
			auditSwitch.setId(id);
			auditSwitch.setDeleteStatus(Constants.DELETE_TRUE);
			exemptionAuditSwitchDao.updateByPrimaryKeySelective(auditSwitch);
		}
	}
}
